package vinyarion.fukkit.main.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.server.management.UserListBans;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FSeenData {
	
	public UserListBans banList;
	
	public static final String BANNED = "banned";
	public static final String LOGOUT = "logout";
	public static final String TIME = "time";
	
	private static FSeenData instance = null;
	
	public static FSeenData instance() {
		return instance;
	}
	
	static {
		instance = new FSeenData();
		instance.tag = FData.instance().tag("seen");
		instance.initBans();
		FMod.log(Level.INFO, "FSeenData initialized!");
	}
	
	public void initBans() {
		try {
			Field f = ServerConfigurationManager.class.getDeclaredFields()[8];
			f.setAccessible(true);
			banList = (UserListBans) f.get(MinecraftServer.getServer().getConfigurationManager());
			FMod.log(Level.INFO, "Retrieved banlist!");
		} catch(Exception e) {
			FMod.log(Level.ERROR, "Failed to retrieve banlist!");
			e.printStackTrace();
		}
	}
	
	private NBTTagCompound tag;
	
	public void lastVisit(EntityPlayer player) {
		NBTTagCompound seen = new NBTTagCompound();
		if(tag.hasKey(player.getUniqueID().toString(), NBT.COMPOUND)) {
			seen = tag.getCompoundTag(player.getUniqueID().toString());
		} else {
			seen.setBoolean(BANNED, banList.func_152703_a(player.getCommandSenderName()) != null);
		}
		seen.setString(TIME, Time.instantNow());
		seen.setBoolean(LOGOUT, true);
		
		tag.setString(player.getCommandSenderName(), player.getUniqueID().toString());
		tag.setTag(player.getUniqueID().toString(), seen);
		FData.changed();
	}
	
	public NBTTagCompound getFor(String player) {
		if(tag.hasKey(player, NBT.STRING)) {
			player = tag.getString(player);
		}
		if(tag.hasKey(player, NBT.COMPOUND)) {
			return tag.getCompoundTag(player);
		}
		return null;
	}
	
	public List<String> players() {
		List<String> ret = new ArrayList<String>();
		return ret;
	}
	
}
