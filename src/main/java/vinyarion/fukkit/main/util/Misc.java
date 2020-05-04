package vinyarion.fukkit.main.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;

public class Misc {
	
	public static boolean debug = false;
	
	public static void report(Exception e) {
		e.printStackTrace();
		if(debug) {
			FCommand.NotifyDev(MinecraftServer.getServer(), "Unexpected error! " + e.getMessage() + " : " + e.getLocalizedMessage());
			for(StackTraceElement ste : e.getStackTrace()) {
				FCommand.NotifyDev(MinecraftServer.getServer(), "   " + ste.toString());
			}
		}
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasLog()) {
			String say = "Unexpected error! " + e.getMessage() + " : " + e.getLocalizedMessage();
			String sayl = say;
			int i = 4;
			for(StackTraceElement ste : e.getStackTrace()) {
				sayl = say;
				say = say + "\n   " + ste.toString();
				if(i-- < 0) {
					break;
				}
			}
			FDiscordBot.getInstance().queueLog(say.length() > 2000 ? sayl : say);
		}
	}
	
	public static final UUID zero = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	public static final Random rand = new Random();
	
	public static String concat(String[] sa, String with) {
		String ret = "";
		for(String s : sa) {
			ret += s + with;
		}
		return sa.length > 0 ? ret.substring(0, ret.length() - with.length()) : ret;
	}
	
	public static <T> T[] concat(T[][] mda) {
		int length = 0;
		for(T[] tt : mda) length += tt.length;
		T[] ret = (T[])Array.newInstance(mda.getClass().getComponentType().getComponentType(), length);
		length = 0;
		for(T[] tt : mda) for(T t : tt) {
			ret[length] = t;
			length++;
		}
		return ret;
	}
	
	public static boolean chanceOutOf(double d, double max) {
		return rand.nextDouble() * max <= d;
	}

	public static int intInRangeInclusive(int min, int max) {
		if(min >= max) return min;
		return rand.nextInt(1 + max - min) + min;
	}
	
	public static boolean percent(double d) {
		return chanceOutOf(d, 100.0D);
	}
	
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public static int parseInt(String s) {
		return Integer.decode(s).intValue();
	}
	
	public static String truncateTo(String in, int max) {
		return in.substring(0, Math.min(in.length(), max));
	}
	
	public static String titleCase(String in) {
		StringBuffer buf = new StringBuffer(in.toLowerCase());
		StringBuffer ret = new StringBuffer();
		boolean capital = true;
		for(int i = 0; i < buf.length(); i++) {
			char c = buf.charAt(i);
			if(!Character.isLowerCase(c)) {
				ret.appendCodePoint(c);
				capital = Character.isWhitespace(c);
			} else if(capital) {
				capital = false;
				ret.appendCodePoint(Character.toUpperCase(c));
			} else {
				ret.appendCodePoint(c);
			}
		}
		return ret.toString();
	}
	
	public static String times(String in, int times) {
		String ret = "";
		for(int i = 1; i < times; i++) {
			ret += in;
		}
		return ret;
	}
	
	public static List<String> simplify(List<String> in) {
		List<String> out = new ArrayList<String>();
		for(String string : in) {
			if(!out.contains(string)) {
				out.add(string);
			}
		}
		return out.stream().sorted().collect(Collectors.<String>toList());
	}
	
	public static MovingObjectPosition look(EntityPlayer player) {
		return ((FNetHandlerPlayServer)((EntityPlayerMP)player).playerNetServerHandler).data().objectMouseOver;
	}
	
	public static Map<String, AtomicInteger> periodic = new HashMap<String, AtomicInteger>();
	
	public static boolean every(int times, String type, String thing) {
		String key = type + "#" + thing;
		AtomicInteger i = periodic.get(key);
		if(i != null) {
			return i.incrementAndGet() % times == 0;
		} else {
			periodic.put(key, new AtomicInteger(0));
			return true;
		}
	}
	
	public static float deviationInLooking(EntityLivingBase looker, Entity target) {
		float[] should = getRotationsNeeded(looker, target);
		float diffPitch = should[1] - looker.rotationPitch;
		float diffYaw = should[0] - looker.rotationYaw;
		float ys = diffYaw * diffYaw;
		float ps = diffPitch * diffPitch;
		return MathUtils.sqrt(ps + ys);
	}
	
    public static float[] getRotationsNeeded(EntityLivingBase looker, Entity target) {
        double diffY = target instanceof EntityLivingBase ? 
        	(target.posY + (double)target.getEyeHeight() * 0.9 - (looker.posY + (double)looker.getEyeHeight())): 
        	(target.boundingBox.minY + target.boundingBox.maxY) / 2.0 - (looker.posY + (double)looker.getEyeHeight());
        double diffX = target.posX - looker.posX;
        double diffZ = target.posZ - looker.posZ;
        double dist = MathUtils.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(MathUtils.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(- MathUtils.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{looker.rotationYaw + MathUtils.wrapDegrees(yaw - looker.rotationYaw), looker.rotationPitch + MathUtils.wrapDegrees(pitch - looker.rotationPitch)};
    }

    public static void resetArrowLooking(EntityArrow arrow, EntityLivingBase shooter) {
    	arrow.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        arrow.posX -= (double)(MathHelper.cos(arrow.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        arrow.posY -= 0.10000000149011612D;
        arrow.posZ -= (double)(MathHelper.sin(arrow.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        arrow.setPosition(arrow.posX, arrow.posY, arrow.posZ);
        arrow.yOffset = 0.0F;
        arrow.motionX = (double)(-MathHelper.sin(arrow.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(arrow.rotationPitch / 180.0F * (float)Math.PI));
        arrow.motionZ = (double)(MathHelper.cos(arrow.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(arrow.rotationPitch / 180.0F * (float)Math.PI));
        arrow.motionY = (double)(-MathHelper.sin(arrow.rotationPitch / 180.0F * (float)Math.PI));
    }
    
    public static Vec3 interpolate(float pitch, float yaw) {
    	float f1 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f3 = -MathHelper.cos(-pitch * 0.017453292F);
        float f4 = MathHelper.sin(-pitch * 0.017453292F);
        return Vec3.createVectorHelper((double)(f2 * f3), (double)f4, (double)(f1 * f3));
    }
    
    public static float[] interpolate(Vec3 vec) {
        double dist = MathUtils.sqrt(vec.xCoord * vec.xCoord + vec.zCoord * vec.zCoord);
        float yaw = (float)(MathUtils.atan2(vec.zCoord, vec.xCoord) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(- MathUtils.atan2(vec.yCoord, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

	public static <T> T make(Supplier<T> supplier) {
		return supplier.get();
	}
	
}
