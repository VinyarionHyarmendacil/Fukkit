package vinyarion.fukkit.main.cmd;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChunkCoordinates;

public class FEntitySelector {
	
	public static List<Entity> getEntitiesFor(ICommandSender sender, String attoken) {
		List<Entity> ret = new ArrayList<Entity>();
		FEntitySelector sel = new FEntitySelector(sender, attoken);
		for(Object ontity : sender.getEntityWorld().loadedEntityList) {
			Entity entity = (Entity)ontity;
			if(sel.checks(entity)) {
				ret.add(entity);
			}
		}
		return ret;
	}
	
	private float healthmin;
	private float healthmax;
	private int rangemin;
	private int rangemax;
	private int x;
	private int y;
	private int z;
	private int count;
	private String type;
	private String name;
	
	public FEntitySelector(ICommandSender sender, String attoken) {
		try {
			String[] token = attoken.substring(attoken.indexOf("[") + 1, attoken.indexOf("]")).split(",");
			x = getI("x", token, sender.getPlayerCoordinates().posX);
			y = getI("y", token, sender.getPlayerCoordinates().posY);
			z = getI("z", token, sender.getPlayerCoordinates().posZ);
			rangemin = getI("rm", token, 0);
			rangemax = getI("r", token, Integer.MAX_VALUE);
			healthmin = getF("hm", token, 0);
			healthmax = getF("h", token, Float.MAX_VALUE);
			count = getI("c", token, Integer.MAX_VALUE);
			type = get("type", token, "Entity");
			name = get("name", token, "Entity");
		} catch(Exception e) {
			
		}
	}
	
	private boolean checks(Entity entity) {
		if(count <= 0) {
			return false;
		}
		--count;
		boolean ok = true;
		double d = Math.sqrt(new ChunkCoordinates(x, y, z).getDistanceSquared(entity.chunkCoordX, entity.chunkCoordY, entity.chunkCoordZ));
		ok = ok && rangemin <= d && d <= rangemax;
		if(entity instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase)entity;
			ok = ok && healthmin <= elb.getHealth() && elb.getHealth() <= healthmax;
		}
		ok = ok && type.equals("Entity") ? true : type.equals(EntityList.classToStringMapping.get(entity.getClass()));
		String n = entity.getDataWatcher().getWatchableObjectString(10);
		ok = ok && name.equals("Entity") ? true : name.equals(n.length() > 0 ? n : entity.getCommandSenderName());
		return ok;
	}
	
	public static String get(String key, String[] token, String def) {
		for(String thing : token) {
			if(thing.startsWith(key + "=")) {
				return thing.substring(key.length() + 1);
			}
		}
		return def;
	}
	
	public static int getI(String key, String[] token, int def) {
		for(String thing : token) {
			if(thing.startsWith(key + "=")) {
				return Integer.parseInt(thing.substring(key.length() + 1));
			}
		}
		return def;
	}
	
	public static float getF(String key, String[] token, float def) {
		for(String thing : token) {
			if(thing.startsWith(key + "=")) {
				return Float.parseFloat(thing.substring(key.length() + 1));
			}
		}
		return def;
	}
	
	public static double getD(String key, String[] token, double def) {
		for(String thing : token) {
			if(thing.startsWith(key + "=")) {
				return Double.parseDouble(thing.substring(key.length() + 1));
			}
		}
		return def;
	}
	
	public static String[] getArgs(String attoken) {
		return attoken.substring(3, attoken.length() - 1).split(":");
	}
	
}
