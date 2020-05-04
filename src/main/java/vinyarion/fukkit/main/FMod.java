package vinyarion.fukkit.main;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.math.IntMath;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.NetworkModHolder;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import lotr.common.world.genlayer.LOTRGenLayerWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import vinyarion.fukkit.main.cmd.FCommands;
import vinyarion.fukkit.main.data.FBannedMods;
import vinyarion.fukkit.main.data.FBlockLogger;
import vinyarion.fukkit.main.data.FCommandAliases;
import vinyarion.fukkit.main.data.FConfig;
import vinyarion.fukkit.main.data.FCustomProtectionBlocks;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.memorysubtypes.FukkitSecurityManager;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.script.FStaticScripts;
import vinyarion.fukkit.main.territories.FTerritories;
import vinyarion.fukkit.main.util.Later;
import vinyarion.fukkit.main.util.Updates;
import vinyarion.fukkit.main.util.memory.Memory;
import vinyarion.fukkit.main.util.memory.RTC;
import vinyarion.fukkit.rpg.archetypes.Faction;
import vinyarion.fukkit.rpg.chestguis.FRPGGuis;
import vinyarion.fukkit.rpg.forging.FBoundSkills;
import vinyarion.fukkit.rpg.forging.FMarkOfMaker;
import vinyarion.fukkit.rpg.forging.rings.FRingForging;
import vinyarion.fukkit.rpg.palantir.FPalantir;
import vinyarion.fukkit.sm.blocklog.FBlockLog;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;
import vinyarion.lotrclientutil.ServerPacketHandler;
import vinyarion.lotrclientutil.common.packets.Packets;

@Mod(modid = FMod.modID, name = FMod.name, version = FMod.version, acceptableRemoteVersions = "*")
public class FMod {
	
	public static final String modID = "fukkit";
	public static final String name = "Fukkit";
	public static final String version = "1.4.1";
	public static final int increment = 0;
	
	private static final Logger log = LogManager.getFormatterLogger("Fukkit");

	static {
		FPreloadClasses.load();
	}
	
	public static final SimpleNetworkWrapper tirathurinNet = NetworkRegistry.INSTANCE.newSimpleChannel("tírathurin_");

	static void log(String msg) {
		log.log(Level.INFO, msg);
	}

	public static void log(Level level, String msg) {
		log.log(level, msg);
		boolean init = msg.toLowerCase().contains("initiali");
		boolean save = msg.toLowerCase().contains("save");
		boolean net = msg.toLowerCase().contains("net handler");
		boolean startup = msg.toLowerCase().contains("startup");
		if(!save) if(!init) 
		if(net ? config.doConsoleJoin : true) 
		if(startup ? config.doConsoleStartup : true) 
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasLog()) {
			FDiscordBot.getInstance().queueLog(msg);
		}
	}
	
	public static boolean debug = true;
	
	@Instance(modID)
	public static FMod theMod;
	
	public FMod() {
		theMod = this;
	}
	
	@NetworkCheckHandler
	public Boolean checkNetwork(Map<String, String> map, Side side) {
		// TODO : check if the player has permissions
		if(map.containsKey("tírathurin")) {
			return true;
		}
		for(Entry<String, String> mod : map.entrySet()) {
			String id = mod.getKey();
			String version = mod.getValue();
			if(FBannedMods.instance().banned().contains(id)) return false;
		}
		return true;
	}
	
	public static FConfig config;
	public static Later later;
	
	@EventHandler
	public void pre(FMLPreInitializationEvent event) {
		if(event.getSide() != Side.SERVER) {
			throw new RuntimeException("This mod can only be used on servers!");
		}
		config = new FConfig(event.getSuggestedConfigurationFile());
		later = new Later();
		Packets.registerTo(tirathurinNet);
		Packets.setHandler(new ServerPacketHandler());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		FEvents events = new FEvents();
		FMLCommonHandler.instance().bus().register(events);
		MinecraftForge.EVENT_BUS.register(events);
		FRingForging.initSpecialItems();
		FMarkOfMaker.init();
		FBoundSkills.init();
		Faction.init();
		FRPGGuis.init();
		FBannedMods.instance().setup();
		FTerritories.init();
		FPalantir.init();
	}
	
	@EventHandler
	public void post(FMLPostInitializationEvent event) {
		
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		FCommandAliases.instance().setup();
		FCommands.registerServerCommands(event);
		FBlockLog.INSTANCE.init();
		FCustomProtectionBlocks.instance().init();
	}
	
	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		log(Level.INFO, "Executing startup commands!");
		FStaticScripts.serverStart().command(null);
		{
			File file = FData.instance().file("startup", "txt");
			if(!file.exists()) {
				FData.writeRawText(file, "#This is the default startup file. Add one command per line, and use the '#' character to comment.\n");
			} else 
			for(String line : FData.readRawLines(file)) {
				if(line.length() == 0) continue; else 
				if(line.startsWith("#")) {
					FMod.log(Level.INFO, "Ignoring: " + line);
				} else {
					FMod.log(Level.INFO, "Executing: " + line);
					MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), line);
				}
			}
		}
		log(Level.INFO, "Finished startup commands!");
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasChannel()) {
			try {
				if(config.doNotifyStart) {
					FDiscordBot.getInstance().queueChannel(":rocket: Server started!");
				}
			} catch(Exception e) {}
		}
		Updates.check();
		MinecraftServer server = MinecraftServer.getServer();
		if(server instanceof DedicatedServer) {
			RTC.rtc(server, FDedicatedServer.class);
		}
	}
	
	@EventHandler
	public void serverStop(FMLServerStoppingEvent event) {
		this.serverStopImpl();
	}

	private volatile boolean stopped = false;
	public synchronized void serverStopImpl() {
		if(stopped) return;
		stopped = true;
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			FPlayerData.forPlayer(((EntityPlayer)o).getCommandSenderName()).destroy();
		}
		FData.instance().save();
		FBlockLogger.save();
		FStaticScripts.serverStop().command(null);
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasChannel()) {
			log(Level.INFO, "Bot was on! Stopping that!");
			try {
				if(config.doNotifyStart) {
					FDiscordBot.getInstance().queueChannel(":x: Server closed!");
				}
			} catch(Exception e) {}
			FDiscordBot.flush(true);
			FDiscordBot.getInstance().dispatcher.shutdown();
			FDiscordBot.stop();
		}
	}
	
}
