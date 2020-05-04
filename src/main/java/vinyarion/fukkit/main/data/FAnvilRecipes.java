package vinyarion.fukkit.main.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.base.Throwables;

import lotr.common.inventory.LOTRContainerAnvil;
import lotr.common.inventory.LOTRSlotAnvilOutput;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.data.FRecipes.IFFacRecipe;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.recipes.FAnvilRecipe;
import vinyarion.fukkit.main.recipes.FAnvilRecipeSimple;
import vinyarion.fukkit.main.recipes.FSpikedBootsRecipe;
import vinyarion.fukkit.main.recipes.Permenant;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAnvilRecipes {
	
	private static FAnvilRecipes instance = null;
	
	public static FAnvilRecipes instance() {
		return instance;
	}
	
	static {
		instance = new FAnvilRecipes();
		instance.tag = FData.instance().tag("anvil_recipes");
		FMod.log(Level.INFO, "FAnvilRecipes initialized!");
		instance.recipes.add(new FSpikedBootsRecipe("generag_spiked_boots", true));
	}
	
	private NBTTagCompound tag;
	
	public void updatePlayer(EntityPlayerMP player) {
		if(player.openContainer != null) {
			if(player.openContainer instanceof LOTRContainerAnvil) {
				LOTRContainerAnvil anvil = (LOTRContainerAnvil)player.openContainer;
				ItemStack l = anvil.getSlot(0).getStack();
				ItemStack t = anvil.getSlot(1).getStack();
				ItemStack b = anvil.getSlot(2).getStack();
				ItemStack stack = this.findMatchingRecipe(player, l, t, b);
				if(stack != null) {
					anvil.getSlot(3).putStack(stack);
				}
			}
		}
	}
	
	private List<FAnvilRecipe> recipes = new ArrayList<FAnvilRecipe>();
	
	public boolean remove(String name) {
		for(FAnvilRecipe recipe : new ArrayList<FAnvilRecipe>(recipes)) {
			if(recipe instanceof Permenant) continue;
			if(recipe.name.equals(name)) {
				recipes.remove(recipe);
			}
		}
		NBTTagList list = tag.getTagList("anvil_recipes", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound recipe = list.getCompoundTagAt(i);
			if(recipe.hasKey("name", NBT.STRING) && recipe.getString("name").equals(name)) {
				list.removeTag(i);
				FData.changed();
				return true;
			} else if(!recipe.hasKey("name", NBT.STRING)) {
				list.removeTag(i);
				FData.changed();
				i--;
				FMod.log(Level.INFO, "Removed unnamed recipe!");
			}
		}
		return false;
	}
    
	public NBTTagCompound retreive(String name) {
		NBTTagList list = tag.getTagList("anvil_recipes", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound recipe = list.getCompoundTagAt(i);
			if(recipe.hasKey("name", NBT.STRING) && recipe.getString("name").equals(name)) {
				return recipe;
			}
		}
		return null;
	}
	
	public void submit(String recipe) throws NBTException {
		submit((NBTTagCompound)JsonToNBT.func_150315_a(recipe));
	}
	
	public void submit(NBTTagCompound recipe) {
		String name = recipe.getString("name");
		NBTTagList list = tag.getTagList("anvil_recipes", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound rr = list.getCompoundTagAt(i);
			if(rr.getString("name").equals(name)) {
				FMod.log(Level.INFO, "Overwriting recipe!");
				list.removeTag(i);
				i--;
			}
		}
		list.appendTag(recipe);
		load(recipe);
	}
    
	public String[] anvilrecipes() {
		List<String> names = new ArrayList<String>();
		for(FAnvilRecipe recipe : new ArrayList<FAnvilRecipe>(recipes)) {
			names.add(recipe.getName());
		}
//		NBTTagList list = instance.tag.getTagList("anvil_recipes", NBT.COMPOUND);
//		for(int i = 0; i < list.tagCount(); i++) {
//			names.add(list.getCompoundTagAt(i).getString("name"));
//		}
		return names.toArray(new String[names.size()]);
	}
	
	public FAnvilRecipe load(NBTTagCompound recipe) {
		String name = recipe.getString("name");
		FMod.log(Level.INFO, "Anvil Recipe: " + name);
		ItemStack result = recipe.hasKey("result", NBT.COMPOUND) ? parseResult(recipe.getCompoundTag("result")) : null;
		List<Object> params = new ArrayList<Object>();
		NBTTagList list = (NBTTagList)recipe.getTag("params");
		boolean isPublic;
		if(recipe.hasKey("isCraftableByAnyone")) {
			isPublic = recipe.getBoolean("isCraftableByAnyone");
		} else {
			isPublic = true;
		}
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound part = list.getCompoundTagAt(i);
			String type = part.getString("type");
			if(type.equals("custom")) {
				params.add(FItems.instance().get(part.getString("value")).stack());
			} else if(type.equals("block")) {
				params.add(new ItemStack(CommandBase.getBlockByText(MinecraftServer.getServer(), part.getString("value"))));
			} else if(type.equals("item")) {
				params.add(new ItemStack(CommandBase.getItemByText(MinecraftServer.getServer(), part.getString("value"))));
			} else if(type.equals("itemstack")) {
				params.add(ItemStack.loadItemStackFromNBT(part.getCompoundTag("value")).getItem());
			} else if(type.equals("null")) {
				params.add(null);
			}
		}
		FData.changed();
		FAnvilRecipe parsed;
		try {
//			parsed = new FAnvilRecipeSimple(name, isPublic, params.get(0) == null ? null : ((ItemStack)params.get(0)).getItem(), 
//					params.get(1) == null ? null : ((ItemStack)params.get(1)).getItem(), 
//					params.get(2) == null ? null : ((ItemStack)params.get(2)).getItem(), 
//					result);
			parsed = new FAnvilRecipeSimple(
					name, 
					isPublic, 
					(ItemStack)params.get(0), 
					(ItemStack)params.get(1), 
					(ItemStack)params.get(2), 
					result);
		} catch(Exception e) {
			e.printStackTrace();
			Misc.report(e);
			FMod.log(Level.ERROR, "Recipe was " + recipe.toString());
			return null;
		}
		for(FAnvilRecipe old : new ArrayList<FAnvilRecipe>(recipes)) {
			if(old instanceof Permenant) continue;
			if(old.name.equals(name)) {
				recipes.remove(old);
			}
		}
		recipes.add(parsed);
		return parsed;
	}

	private ItemStack parseResult(NBTTagCompound tag) {
		if(tag.hasKey("custom", NBT.STRING)) {
			short c = tag.hasKey("Count") ? tag.getShort("Count") : -1;
			short d = tag.hasKey("Damage") ? tag.getShort("Damage") : -1;
			NBTTagCompound t = tag.hasKey("tag") ? tag.getCompoundTag("tag") : null;
			FItem item = FItems.instance().get(tag.getString("custom"));
			return item == null ? null : c == -1 ? item.stack() : d == -1 ? item.stack(c) : t == null ? item.stack(c, d) : item.stack(c, d, t);
		} else {
			return ItemStack.loadItemStackFromNBT(tag);
		}
	}
	
	public ItemStack findMatchingRecipe(EntityPlayerMP player, ItemStack left, ItemStack top, ItemStack bottom) {
        for(int j = 0; j < this.recipes.size(); ++j) {
            FAnvilRecipe recipe = (FAnvilRecipe)this.recipes.get(j);
            if(recipe.matches(left, top, bottom)) {
            	ItemStack res = recipe.getResult(left, top, bottom);
        		String name = recipe.getName();
        		if (recipe.isPublic()) {
            		return res;
        		} else if (FPermissions.instance().hasPermission(player, "anvilrecipe." + name)) {
            		return res;
        		}
            }
        }
        return null;
    }
	
	public ItemStack onSlotRemovedMatchingRecipe(EntityPlayerMP player, LOTRContainerAnvil anvil, LOTRSlotAnvilOutput slot, ItemStack left, ItemStack top, ItemStack bottom, ItemStack out) throws Exception {
        for(int j = 0; j < this.recipes.size(); ++j) {
            FAnvilRecipe recipe = (FAnvilRecipe)this.recipes.get(j);
            if(recipe.matches(left, top, bottom)) {
            	return recipe.clickResult(anvil, out);
            }
        }
        throw new Exception();
    }

	public void addPerm(FAnvilRecipe recipe) {
		this.recipes.add(recipe);
	}
	
}
