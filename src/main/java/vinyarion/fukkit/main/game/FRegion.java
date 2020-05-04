package vinyarion.fukkit.main.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FPermissions;

public class FRegion {

	public static enum Perm {
		block_break, 
		block_place, 
		useblock_door, 
		useblock_trapdoor, 
		useblock_button, 
		useblock_lever, 
		interact_attack, 
		interact_takedamage, 
		block_bucket, 
		use_container, 
		deprecated_warzone, 
		useblock_generic, 
		region_naturalspawn, 
		interact_itemframe;
		public final int bit = 1 << ordinal();
	}
	
	public static final int BIT_BREAK =        0x00000001;//1
	public static final int BIT_PLACE =        0x00000002;//2
	public static final int BIT_DOOR =         0x00000004;//4
	public static final int BIT_TRAPDOOR =     0x00000008;//8
	public static final int BIT_BUTTON =       0x00000010;//16
	public static final int BIT_LEVER =        0x00000020;//32
	public static final int BIT_ATTACK =       0x00000040;//64
	public static final int BIT_DAMAGE =       0x00000080;//128
	public static final int BIT_BUCKET =       0x00000100;//256    //don't laugh
	public static final int BIT_CONTAINER =    0x00000200;//512
	public static final int BIT_WARZONE =      0x00000400;//1024
	public static final int BIT_USEBLOCK =     0x00000800;//2048
	public static final int BIT_NATURALSPAWN = 0x00000800;//4096
	
	public static final int ALL_BITS =         0xFFFFFFFF;
	
	public final boolean bit(int bit) {
		return (perms & bit) != bit;
	}
	
	public String name;
	int xMax;
	int yMax;
	int zMax;
	int xMin;
	int yMin;
	int zMin;
	int dim;
	int perms;
	public List<Entry<String, String>> attributes = new ArrayList<Entry<String, String>>();
	
	public FRegion(NBTTagCompound region) {
		name = region.getString("name");
		xMax = region.getInteger("xMax");
		yMax = region.getInteger("yMax");
		zMax = region.getInteger("zMax");
		xMin = region.getInteger("xMin");
		yMin = region.getInteger("yMin");
		zMin = region.getInteger("zMin");
		dim = region.getInteger("dim");
		perms = region.getInteger("perms");
	}
	
	public boolean applies(EntityPlayerMP player, int x, int y, int z) {
		if(player.dimension == dim && xMin <= x && x <= xMax && yMin <= y && y <= yMax && zMin <= z && z <= zMax) {
			if(FPermissions.instance().hasPermission(player, "region." + name)) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean applies(Entity entity, int x, int y, int z) {
		boolean ret = (entity.dimension == dim) && (xMin <= x && x <= xMax) && (yMin <= y && y <= yMax) && (zMin <= z && z <= zMax);
		return ret;
	}
	
	public boolean deniesBlockBreak(EntityPlayerMP player, int x, int y, int z) {
		return bit(Perm.block_break.bit);
	}
	
	public boolean deniesBlockPlace(EntityPlayerMP player, int x, int y, int z) {
		return bit(Perm.block_place.bit);
	}
	
	public boolean deniesInteraction(EntityPlayerMP player, int x, int y, int z, Action action, Block looking) {
		if(action != Action.RIGHT_CLICK_AIR) {
			if(looking instanceof BlockDoor) {
				return bit(Perm.useblock_door.bit);
			} else if(looking instanceof BlockTrapDoor) {
				return bit(Perm.useblock_trapdoor.bit);
			} else if(looking instanceof BlockButton) {
				return bit(Perm.useblock_button.bit);
			} else if(looking instanceof BlockLever) {
				return bit(Perm.useblock_lever.bit);
			}
		}
		return bit(Perm.useblock_generic.bit);
	}
	
	public boolean deniesBucketFill(EntityPlayerMP player, int x, int y, int z) {
		return bit(Perm.block_bucket.bit);
	}
	
	public boolean deniesDamage(EntityPlayerMP player, int x, int y, int z) {
		return bit(Perm.interact_takedamage.bit);
	}
	
	public boolean deniesAttack(EntityPlayerMP player, int x, int y, int z) {
		return bit(Perm.interact_attack.bit);
	}
	
	public boolean deniesContainer(EntityPlayerMP player, int x, int y, int z) {
		return bit(Perm.use_container.bit);
	}
	
	public boolean isWarzone(EntityPlayerMP player, int x, int y, int z) {
		return !bit(Perm.deprecated_warzone.bit);
	}
	
	public boolean deniesNaturalSpawn(Entity entity, int x, int y, int z) {
		return bit(Perm.region_naturalspawn.bit);
	}

	public boolean deniesItemFrame(EntityPlayerMP player, EntityItemFrame frame, int x, int y, int z) {
		return bit(Perm.interact_itemframe.bit);
	}
	
	public static class Cylinder extends FRegion {
		
		int xp;
		int zp;
		int r;
		
		public Cylinder(NBTTagCompound region) {
			super(region);
			xp = region.getInteger("x");
			zp = region.getInteger("z");
			r = region.getInteger("r");
		}
		
		public boolean applies(EntityPlayerMP player, int x, int y, int z) {
			if(FPermissions.instance().hasPermission(player, "region." + name)) {
				return false;
			}
			double dx = (double)x - (double)xp;
			double dz = (double)z - (double)zp;
			boolean ret = dx * dx + dz * dz < r * r;
			return ret;
		}
		
		public boolean applies(Entity entity, int x, int y, int z) {
			double dx = (double)x - (double)xp;
			double dz = (double)z - (double)zp;
			boolean ret = dx * dx + dz * dz < r * r;
			return ret;
		}
		
	}
	
}
