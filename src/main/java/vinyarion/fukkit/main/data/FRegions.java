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
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.game.FRegion;
import vinyarion.fukkit.main.recipes.FShapedFacRecipe;
import vinyarion.fukkit.main.recipes.FShapelessFacRecipe;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.sm.cmd.FCommandWarzone;

public class FRegions {
	
	private static FRegions instance = null;
	
	public static FRegions instance() {
		return instance;
	}
	
	static {
		instance = new FRegions();
		instance.tag = FData.instance().tag("regions");
		if(!instance.tag.hasKey("regions", NBT.LIST)) {
			instance.tag.setTag("regions", new NBTTagList());
		}
		NBTTagList list = instance.tag.getTagList("regions", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			instance.load(list.getCompoundTagAt(i));
		}
		FMod.log(Level.INFO, "FRegions initialized!");
	}
	
	private NBTTagCompound tag;
    
	public NBTTagCompound tag() {
		return tag;
	}
	
	public boolean denyBlockBreak(EntityPlayerMP player, int x, int y, int z) {
		for(FRegion region : regions) {
			if(region.applies(player, x, y, z)) {
				if(region.deniesBlockBreak(player, x, y, z)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean denyBlockPlace(EntityPlayerMP player, int x, int y, int z) {
		for(FRegion region : regions) {
			if(region.applies(player, x, y, z)) {
				if(region.deniesBlockPlace(player, x, y, z)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean denyBucketFill(EntityPlayerMP player, int x, int y, int z) {
		for(FRegion region : regions) {
			if(region.applies(player, x, y, z)) {
				if(region.deniesBucketFill(player, x, y, z)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean denyInteraction(EntityPlayerMP player, int x, int y, int z, Action action) {
		Block looking = player.worldObj.getBlock(x, y, z);
		for(FRegion region : regions) {
			if(region.applies(player, x, y, z)) {
				if(region.deniesInteraction(player, x, y, z, action, looking)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean denyDamage(EntityPlayerMP player, int x, int y, int z) {
		if(FCommandWarzone.isSafe(player)) {
			return false;
		}
		for(FRegion region : regions) {
			if(region.applies(player, x, y, z)) {
				if(region.deniesDamage(player, x, y, z)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean denyAttack(EntityPlayerMP player, int x, int y, int z) {
		if(FCommandWarzone.isSafe(player)) {
			return false;
		}
		for(FRegion region : regions) {
			if(region.applies(player, x, y, z)) {
				if(region.deniesAttack(player, x, y, z)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean denyContainer(EntityPlayerMP player, int x, int y, int z) {
		for(FRegion region : regions) {
			if(region.applies(player, x, y, z)) {
				if(region.deniesContainer(player, x, y, z)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isWithin(EntityPlayerMP player, String name) {
		for(FRegion region : regions) {
			if(region.name.equals(name)) {
				if(region.applies(player, (int)player.posX, (int)player.posY, (int)player.posZ)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public boolean denyNaturalSpawn(CheckSpawn event) {
		int x = (int)event.x;
		int y = (int)event.y;
		int z = (int)event.z;
		
		for(FRegion region : regions) {
			if(region.applies(event.entity, x, y, z)) {
				if(region.deniesNaturalSpawn(event.entity, x, y, z)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean denyItemFrame(EntityPlayerMP player, EntityItemFrame frame, int x, int y, int z) {
		for(FRegion region : regions) {
			if(region.applies(player, x, y, z)) {
				if(region.deniesItemFrame(player, frame, x, y, z)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private List<FRegion> regions = new ArrayList<FRegion>();
	
	public List<FRegion> get() {
		return regions;
	}
	
	public boolean remove(String name) {
		boolean ret = false;
		NBTTagList list = tag.getTagList("regions", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound region = list.getCompoundTagAt(i);
			if(region.hasKey("name", NBT.STRING) && region.getString("name").equals(name)) {
				list.removeTag(i);
				FData.changed();
				ret = true;
			} else if(!region.hasKey("name", NBT.STRING)) {
				list.removeTag(i);
				FData.changed();
				i--;
				FMod.log(Level.INFO, "Removed unnamed region!");
			}
		}
		List<FRegion> old = new ArrayList<FRegion>();
		for(FRegion region : regions) {
			if(region.name.equals(name)) {
				old.add(region);
			}
		}
		regions.removeAll(old);
		return ret;
	}
    
	public NBTTagCompound retreive(String name) {
		int i = 0;
		for(FRegion region : regions) {
			if(region.name.equals(name)) {
				return tag.getTagList("regions", NBT.COMPOUND).getCompoundTagAt(i);
			}
			i++;
		}
		return null;
	}
	
	public void submit(String region) throws NBTException {
		submit((NBTTagCompound)JsonToNBT.func_150315_a(region));
	}
	
	public void submit(NBTTagCompound region) {
		String name = region.getString("name");
		this.remove(name);
		NBTTagList list = tag.getTagList("regions", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound rr = list.getCompoundTagAt(i);
			if(rr.getString("name").equals(name)) {
				FMod.log(Level.INFO, "Overwriting region!");
				list.removeTag(i);
				i--;
			}
		}
		list.appendTag(region);
		FData.changed();
		load(region);
	}
    
	public String[] regions() {
		List<String> names = new ArrayList<String>();
		for(FRegion region : regions) {
			names.add(region.name);
		}
		return names.toArray(new String[names.size()]);
	}
	
	public FRegion load(NBTTagCompound region) {
		String type = region.getString("type");
		FRegion parsed;
		if(type.equals("cyl")) {
			parsed = new FRegion.Cylinder(region);
		} else {
			parsed = new FRegion(region);
		}
		regions.add(parsed);
		return parsed;
	}
	
}
