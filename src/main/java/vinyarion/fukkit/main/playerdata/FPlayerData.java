package vinyarion.fukkit.main.playerdata;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FWarps;
import vinyarion.fukkit.main.util.nbt.INBTSerializable;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.FPlayerDataRPG;

public class FPlayerData {
	
//	public static UUID nameToUUID(String name) {
//		for(Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
//			if(entry.getValue().equals(name)) return entry.getKey();
//		}
//		return null;
//	}
//	
//	public static String UUIDToName(String uuid) {
//		return UsernameCache.getLastKnownUsername(UUID.fromString(uuid));
//	}
	
	public static UUID toUUID(String in) {
		for(Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
			if(entry.getValue().equals(in)) return entry.getKey();
		}
		try {
			return UUID.fromString(in);
		} catch(Exception e) {
			return null;
		}
	}
	
	public static String toName(String in) {
		try {
			String ret = UsernameCache.getLastKnownUsername(UUID.fromString(in));
			return ret == null ? in : ret;
		} catch(Exception e) {
			return in;
		}
	}
	
	public static final String GROUPS_TAG = "group";
	
	public static FPlayerData load(EntityPlayer player) {
		FPlayerData data = new FPlayerData(player);
		map.put(player.getCommandSenderName(), data);
		FMod.log(Level.INFO, "Loaded player data for " + data.name + " @ " + data.uuid);
		return data;
	}
	
	public static FPlayerData forPlayer(EntityPlayer player) {
		return forPlayer(player.getCommandSenderName());
	}
	
	public static FPlayerData forPlayer(String name) {
//		if(!map.containsKey(name)) {
//			return null;
//		}
		return map.get(name);
	}
	
	private static Map<String, FPlayerData> map = new HashMap<String, FPlayerData>();
	
	public FPlayerData(EntityPlayer player) {
		this.uuid = player.getGameProfile().getId().toString();
		this.name = player.getCommandSenderName();
		this.file = new File(FData.instance().dir("players"), uuid + ".nbt");
		if(!file.exists()) {
			FMod.log(Level.INFO, "New file: Writing default data.");
			try {
				FData.writeRawText(file, "{}");
			} catch (Exception e) {
				FMod.log(Level.ERROR, "Couldn't write the default data!");
				e.printStackTrace();
			}
		}
		for(IPlayerData data : datas) {
			datasmap.put(data.getClass(), data);
		}
		load();
	}
	
	public void doNewPlayer(EntityPlayerMP player) {
		if(!FPermissions.instance().hasGroup(player, "default")) {
			FPermissions.instance().addPermission("default", player);
		}
		FWarps.instance().warp(player, "spawn");
	}
	
	private File file;
	private String name;
	private String uuid;
	private NBTTagCompound tag;
	private NBTTagCompound tagSettings;
	private NBTTagCompound tagVolatile = new NBTTagCompound();
	private List<IPlayerData> datas = Lists.newArrayList(
		new FPlayerDataRPG()
	);
	private Map<Class<? extends IPlayerData>, IPlayerData> datasmap = Maps.newHashMap();
	
	public <T extends IPlayerData> T getData(Class<T> clazz) {
		return (T)datasmap.get(clazz);
	}
	
	public void load() {
		boolean def = false;
		try {
			tag = (NBTTagCompound)JsonToNBT.func_150315_a(FData.readRawText(file));
		} catch (Exception e) {
			def = true;
			tag = new NBTTagCompound();
			e.printStackTrace();
		}
		tagSettings = NBT.ensure(tag, "FUKKIT_SETTINGS");
		for(IPlayerData data : datas) {
			if(def) {
				data.generateDefaults();
			} else {
				data.fromNBT(NBT.ensure(tag, data.getID()));
			}
			data.onLoad(this);
		}
		ticksTotal = tag.getInteger("ticksPlayed");
	}
	
	public void save() {
		try {
			for(IPlayerData data : datas) {
				data.toNBT(NBT.overwrite(tag, data.getID()));
			}
			tag.setInteger("ticksPlayed", ticksTotal);
			try {
				FData.writeRawText(file, tag.toString());
			} catch (Exception e) {
				System.err.println("An exception occurred whilst saving playerdata:");
				e.printStackTrace();
			}
		} catch(Exception e) {
			System.err.println("An exception occurred whilst saving playerdata:");
			e.printStackTrace();
		}
	}
	
	public void destroy() {
		save();
		map.remove(name);
	}
	
	public NBTTagCompound tag() {
		return tag;
	}
	
	public NBTTagCompound tagSettings() {
		return tagSettings;
	}
	
	public NBTTagCompound tagVolatile() {
		return tagVolatile;
	}
	
	public EntityPlayerMP player() {
		return MinecraftServer.getServer().getConfigurationManager().func_152612_a(name);
	}
	
	public void tick() {
		ticksTotal++;
		ticksCurrent++;
	}
	
	private int ticksTotal = 0;
	private int ticksCurrent = 0;
	
	public boolean every(int ticks) {
		return (ticksCurrent % ticks) == 0;
	}

	public int ticksTotal() {
		return ticksTotal;
	}
	
	public int ticksCurrent() {
		return ticksCurrent;
	}
	
}
