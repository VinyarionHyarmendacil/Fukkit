package vinyarion.fukkit.main.game;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FWands {
	
	private static FWands instance;
	
	static {
		instance = new FWands();
		FMod.log(Level.INFO, "FWands initialized!");
	}
	
	public static FWands instance() {
		return instance;
	}
	
	public void wand(PlayerInteractEvent event) {
		ItemStack held = event.entityPlayer.getHeldItem();
		if(held.hasTagCompound()) {
			NBTTagCompound tag = held.getTagCompound();
			if(tag.hasKey("Staves")) {
				try {
					fire((EntityPlayerMP)event.entityPlayer, held, tag.getCompoundTag("Staves"));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void fire(EntityPlayerMP player, ItemStack held, NBTTagCompound tag) {
		Vec3 look = player.getLookVec();
		if(tag.hasKey("shots")) {
			int s = Math.min(128, Math.max(1, tag.getInteger("shots")));
			for(int i = 0; i < s; i++) {
				FEntityWand magic = new FEntityWand(player.worldObj, player, look.xCoord, look.yCoord, look.zCoord, tag.getDouble("deviation"));
				if(tag.hasKey("particleFly")) {
					magic.particleFly = tag.getString("particleFly");
				}
				if(tag.hasKey("soundFly")) {
					magic.soundFly = tag.getString("soundFly");
				}
				if(tag.hasKey("particleHit")) {
					magic.particleHit = tag.getString("particleHit");
				}
				if(tag.hasKey("soundHit")) {
					magic.soundHit = tag.getString("soundHit");
				}
				if(tag.hasKey("velocityFly")) {
					magic.velocityFly = tag.getFloat("velocityFly");
				}
				if(tag.hasKey("effectHit")) {
					magic.effectHit = tag.getString("effectHit");
				}
				if(tag.hasKey("effectValue")) {
					magic.effectValue = tag.getString("effectValue");
				}
				if(tag.hasKey("areaSize")) {
					magic.areaSize = tag.getDouble("areaSize");
				}
				if(tag.hasKey("flame")) {
					magic.flame = tag.getBoolean("flame");
				}
				if(tag.hasKey("gravity")) {
					magic.gravity = tag.getDouble("gravity") / 200D;
				}
				player.worldObj.spawnEntityInWorld(magic);
			}
		}
	}
	
}
