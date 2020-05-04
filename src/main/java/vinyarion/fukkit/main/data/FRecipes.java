package vinyarion.fukkit.main.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.CommandEvent;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.attrib.FAttribute;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.recipes.FAttributeRecipeShapeless;
import vinyarion.fukkit.main.recipes.FShapedFacRecipe;
import vinyarion.fukkit.main.recipes.FShapelessFacRecipe;
import vinyarion.fukkit.main.recipes.Permenant;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FRecipes {
	
	private static FRecipes instance = null;
	
	public static FRecipes instance() {
		return instance;
	}
	
	static {
		instance = new FRecipes();
		instance.tag = FData.instance().tag("recipies");
		if(!instance.tag.hasKey("recipes", NBT.LIST)) {
			instance.tag.setTag("recipes", new NBTTagList());
		}
		NBTTagList list = instance.tag.getTagList("recipes", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			instance.load(list.getCompoundTagAt(i));
		}
//		for(IFFacRecipe r : FHardRecipes.hardRecipes) {
//			instance.recipes.add(r);
//		}
		FMod.log(Level.INFO, "FRecipes initialized!");
	}
	
	private NBTTagCompound tag;
    
	public NBTTagCompound tag() {
		return tag;
	}
	
	public void updatePlayer(EntityPlayerMP player) {
//		MinecraftServer.getServer().getConfigurationManager().syncPlayerInventory(player);
		if(player.openContainer != null) {
			if(player.openContainer instanceof ContainerWorkbench) {
				ContainerWorkbench bench = (ContainerWorkbench)player.openContainer;
				if(bench.craftResult.getStackInSlot(0) != null) {
					if(this.checkCustomItems(player)) {
						bench.craftResult.setInventorySlotContents(0, null);
					}
					return;
				}
				ItemStack result = this.findMatchingRecipe(bench.craftMatrix, player);
				if(result != null) {
					bench.craftResult.setInventorySlotContents(0, result);
				}
			} else if(player.openContainer instanceof ContainerPlayer) {
				ContainerPlayer bench = (ContainerPlayer)player.openContainer;
				if(bench.craftResult.getStackInSlot(0) != null) {
					if(this.checkCustomItems(player)) {
						bench.craftResult.setInventorySlotContents(0, null);
					}
					return;
				}
				ItemStack result = this.findMatchingRecipe(bench.craftMatrix, player);
				if(result != null) {
					bench.craftResult.setInventorySlotContents(0, result);
				}
			}
//			player.inventory.markDirty();
//			bench.detectAndSendChanges();
//			
//			ItemStack result = findMatchingRecipe(bench.craftMatrix, player.worldObj);
//			if(result != null) {
//				bench.craftResult.setInventorySlotContents(0, result);
//			}
//			player.openContainer.detectAndSendChanges();
		}
	}
	
	public void addPerm(IFFacRecipe recipe) {
		this.recipes.add(recipe);
	}
	
	private List<IFFacRecipe> recipes = new ArrayList<IFFacRecipe>();
	
	public boolean remove(String name) {
		for(IFFacRecipe recipe : new ArrayList<IFFacRecipe>(recipes)) {
			if(recipe instanceof Permenant) continue;
			if(recipe.getName().equals(name)) {
				recipes.remove(recipe);
			}
		}
		NBTTagList list = tag.getTagList("recipes", NBT.COMPOUND);
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
		NBTTagList list = tag.getTagList("recipes", NBT.COMPOUND);
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
		NBTTagList list = tag.getTagList("recipes", NBT.COMPOUND);
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
    
	public String[] recipes() {
		List<String> names = new ArrayList<String>();
		for(IFFacRecipe recipe : new ArrayList<IFFacRecipe>(recipes)) {
			names.add(recipe.getName());
		}
//		NBTTagList list = instance.tag.getTagList("recipes", NBT.COMPOUND);
//		for(int i = 0; i < list.tagCount(); i++) {
//			names.add(list.getCompoundTagAt(i).getString("name"));
//		}
		return names.toArray(new String[names.size()]);
	}
	
	public IFFacRecipe load(NBTTagCompound recipe) {
		String name = recipe.getString("name");
		FMod.log(Level.INFO, "Recipe: " + name);
//		for(Object o : recipe.func_150296_c()) {
//			FMod.log(Level.INFO, "  Tag: \"" + o + "\" : " + recipe.getTag((String)o));
//		}
		ItemStack result = recipe.hasKey("result", NBT.COMPOUND) ? parseResult(recipe.getCompoundTag("result")) : null;
		List<Object> params = new ArrayList<Object>();
		NBTTagList list = (NBTTagList)recipe.getTag("params");
		String[] factions = new String[]{"vanilla"};
		String rectype = "shaped";
		if(recipe.hasKey("faction", NBT.STRING)) {
			factions = new String[]{recipe.getString("faction")};
		} else if(recipe.hasKey("faction", NBT.LIST)) {
			NBTTagList ftl = recipe.getTagList("faction", NBT.STRING);
			List<String> fsl = new ArrayList<String>();
			for(int i = 0; i < ftl.tagCount(); i++) {
				fsl.add(ftl.getStringTagAt(i));
			}
			factions = fsl.toArray(new String[fsl.size()]);
		}
//		FMod.log(Level.INFO, "Faction: " + Misc.concat(factions, ","));
		boolean isPublic;
		if(recipe.hasKey("isCraftableByAnyone")) {
			isPublic = recipe.getBoolean("isCraftableByAnyone");
		} else {
			isPublic = true;
		}
//		FMod.log(Level.INFO, "Visibility: " + isPublic);
		if(recipe.hasKey("type", NBT.STRING)) {
			rectype = recipe.getString("type");
		}
//		FMod.log(Level.INFO, "Type: " + rectype);
		if(rectype.equals("shaped")) {
			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound part = list.getCompoundTagAt(i);
//				FMod.log(Level.INFO, part.toString());
				String type = part.getString("type");
				if(type.equals("line")) {
					params.add(part.getString("value"));
				} else if(type.equals("custom")) {
					params.add(part.getString("identifier").charAt(0));
					params.add(FItems.instance().get(part.getString("value")).stack());
				} else if(type.equals("block")) {
					params.add(part.getString("identifier").charAt(0));
					params.add(CommandBase.getBlockByText(MinecraftServer.getServer(), part.getString("value")));
				} else if(type.equals("item")) {
					params.add(part.getString("identifier").charAt(0));
					params.add(CommandBase.getItemByText(MinecraftServer.getServer(), part.getString("value")));
				} else if(type.equals("itemstack")) {
					params.add(part.getString("identifier").charAt(0));
					params.add(ItemStack.loadItemStackFromNBT(part.getCompoundTag("value")));
				}
			}
		} else if(rectype.equals("shapeless")) {
			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound part = list.getCompoundTagAt(i);
				String type = part.getString("type");
				if(type.equals("custom")) {
					params.add(FItems.instance().get(part.getString("value")).stack());
				} else if(type.equals("block")) {
					params.add(Item.getItemFromBlock(CommandBase.getBlockByText(MinecraftServer.getServer(), part.getString("value"))));
				} else if(type.equals("item")) {
					params.add(CommandBase.getItemByText(MinecraftServer.getServer(), part.getString("value")));
				} else if(type.equals("itemstack")) {
					params.add(ItemStack.loadItemStackFromNBT(part.getCompoundTag("value")));
				}
			}
		} else if(rectype.equals("attribute")) {
			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound part = list.getCompoundTagAt(i);
				String type = part.getString("type");
//				if(type.equals("levelByNumber")) {
//					params.add(part.getBoolean("value"));
//				} else 
				if(type.equals("custom")) {
					params.add(FItems.instance().get(part.getString("value")).stack());
				} else if(type.equals("block")) {
					params.add(Item.getItemFromBlock(CommandBase.getBlockByText(MinecraftServer.getServer(), part.getString("value"))));
				} else if(type.equals("item")) {
					params.add(CommandBase.getItemByText(MinecraftServer.getServer(), part.getString("value")));
				} else if(type.equals("attribute")) {
					params.add(FAttributes.get(part.getString("value")));
				}
			}
		}
		FData.changed();
		IFFacRecipe parsed;
		try {
			if(rectype.equals("shaped")) {
				parsed = parseShapedRecipe(factions, name, isPublic, result, params);
			} else if(rectype.equals("shapeless")) {
				parsed = parseShapelessRecipe(factions, name, isPublic, result, params);
			} else if(rectype.equals("attribute")) {
				parsed = parseAttributeRecipe(factions, name, isPublic, result, params);
			} else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
			Misc.report(e);
			FMod.log(Level.ERROR, "Recipe was " + recipe.toString());
			return null;
		}
		for(IFFacRecipe old : new ArrayList<IFFacRecipe>(recipes)) {
			if(old instanceof Permenant) continue;
			if(old.getName().equals(name)) {
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
	
	private boolean checkCustomItems(EntityPlayerMP player) {
		if(player.openContainer instanceof ContainerWorkbench) {
			ContainerWorkbench bench = (ContainerWorkbench)player.openContainer;
			for(int i = 0; i < bench.craftMatrix.getSizeInventory(); i++) {
				ItemStack stack = bench.craftMatrix.getStackInSlot(i);
				if(stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("FItem", NBT.STRING)) {
					return true;
				}
			}
		} else if(player.openContainer instanceof ContainerPlayer) {
			ContainerPlayer bench = (ContainerPlayer)player.openContainer;
			for(int i = 0; i < bench.craftMatrix.getSizeInventory(); i++) {
				ItemStack stack = bench.craftMatrix.getStackInSlot(i);
				if(stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("FItem", NBT.STRING)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public ItemStack findMatchingRecipe(InventoryCrafting matrix, EntityPlayerMP player) {
//		for(int i = 0; i < matrix.getSizeInventory(); i++) {
//			ItemStack stack = matrix.getStackInSlot(i);
//			FMod.log.info("Stack " + i + " is " + String.valueOf(stack));
//		}
        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;
        int j;
        for (j = 0; j < matrix.getSizeInventory(); ++j) {
            ItemStack itemstack2 = matrix.getStackInSlot(j);
            if (itemstack2 != null) {
                if (i == 0) {
                    itemstack = itemstack2;
                }
                if (i == 1) {
                    itemstack1 = itemstack2;
                }
                ++i;
            }
        }
        if (i == 2 && itemstack.getItem() == itemstack1.getItem() && itemstack.stackSize == 1 && itemstack1.stackSize == 1 && itemstack.getItem().isRepairable()) {
            Item item = itemstack.getItem();
            int j1 = item.getMaxDamage() - itemstack.getItemDamageForDisplay();
            int k = item.getMaxDamage() - itemstack1.getItemDamageForDisplay();
            int l = j1 + k + item.getMaxDamage() * 5 / 100;
            int i1 = item.getMaxDamage() - l;
            if (i1 < 0) {
                i1 = 0;
            }
            return new ItemStack(itemstack.getItem(), 1, i1);
        } else {
        	String tablefac = FLOTRHooks.getNameFromBench(player.openContainer);
            for (j = 0; j < this.recipes.size(); ++j) {
                IRecipe irecipe = (IRecipe)this.recipes.get(j);
                IFFacRecipe facrecipe = (IFFacRecipe)irecipe;
                if (irecipe instanceof IFFacRecipe) {
                	String[] fac = ((IFFacRecipe)irecipe).getFactions();
                	boolean good = false;
                	for(String s : fac) {
	                	if(!s.equals(tablefac)) {
	                    	if(s.equals("all")) {
	                    		good = true;
	                    	}
	                	} else {
	                		good = true;
	                	}
                	}
                	if(!good) {
                		continue;
                	}
                }
//                player.addChatMessage(Colors.MAKE(facrecipe.getName()));
//                for(int k = 0; k < matrix.getSizeInventory(); k++) {
//                    player.addChatMessage(Colors.MAKE(String.valueOf(matrix.getStackInSlot(k))));
//                }
//                if (facrecipe instanceof FAttributeRecipeShapeless) {
//                	FAttributeRecipeShapeless ars = (FAttributeRecipeShapeless)facrecipe;
//                    player.addChatMessage(Colors.MAKE("Attribute!"));
//                    for(Object o : ars.items) {
//                        player.addChatMessage(Colors.MAKE(String.valueOf(o)));
//                    }
//                }
//              if (irecipe.matches(matrix, player.worldObj)) {
//                    player.addChatMessage(Colors.MAKE("Match found!"));
//            	}
                if (irecipe.matches(matrix, player.worldObj)) {
                	ItemStack res = irecipe.getCraftingResult(matrix);
            		String name = facrecipe.getName();
            		if (facrecipe.isPublic()) {
                		return res;
            		} else if (FPermissions.instance().hasPermission(player, "recipe." + name)) {
                		return res;
            		}
                }
            }
            return null;
        }
    }
	
	public interface IFFacRecipe {
		public String[] getFactions();
		public String getName();
		public boolean isPublic();
	}
	
	public FShapelessFacRecipe parseShapelessRecipe(String[] factions, String name, boolean isPublic, ItemStack result, List params) {
		ArrayList arraylist = new ArrayList();
        for (int j = 0; j < params.size(); ++j) {
            Object object1 = params.get(j);
            if (object1 instanceof ItemStack) {
                arraylist.add(((ItemStack)object1).copy());
            } else if (object1 instanceof Item) {
                arraylist.add(new ItemStack((Item)object1));
            } else {
                if (!(object1 instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipe!");
                }
                arraylist.add(new ItemStack((Block)object1));
            }
        }
        return new FShapelessFacRecipe(factions, name, isPublic, result, arraylist);
	}
	
    public FShapedFacRecipe parseShapedRecipe(String[] factions, String name, boolean isPublic, ItemStack stack, List params) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;
        if (params.get(i) instanceof String[]) {
            String[] astring = (String[])((String[])params.get(i++));
            for (int l = 0; l < astring.length; ++l) {
                String s1 = astring[l];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        } else {
            while (params.get(i) instanceof String) {
                String s2 = (String)params.get(i++);
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }
        HashMap hashmap;
        for (hashmap = new HashMap(); i < params.size(); i += 2) {
            Character character = (Character)params.get(i);
            ItemStack itemstack1 = null;
            if (params.get(i + 1) instanceof Item) {
                itemstack1 = new ItemStack((Item)params.get(i + 1));
            } else if (params.get(i + 1) instanceof Block) {
                itemstack1 = new ItemStack((Block)params.get(i + 1), 1, 32767);
            } else if (params.get(i + 1) instanceof ItemStack) {
                itemstack1 = (ItemStack)params.get(i + 1);
            }
            hashmap.put(character, itemstack1);
        }
        ItemStack[] aitemstack = new ItemStack[j * k];
        for (int i1 = 0; i1 < j * k; ++i1) {
            char c0 = s.charAt(i1);
            if (hashmap.containsKey(Character.valueOf(c0))) {
                aitemstack[i1] = ((ItemStack)hashmap.get(Character.valueOf(c0))).copy();
            } else {
                aitemstack[i1] = null;
            }
        }
        return new FShapedFacRecipe(j, k, aitemstack, stack, factions, name, isPublic);
    }
	
	public FAttributeRecipeShapeless parseAttributeRecipe(String[] factions, String name, boolean isPublic, ItemStack result, List params) {
		return new FAttributeRecipeShapeless(factions, name, isPublic, /*(Boolean)params.get(0),*/ params.subList(1, params.size()), (FAttribute)params.get(0));
	}
	
}
