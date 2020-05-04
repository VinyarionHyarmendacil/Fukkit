package vinyarion.fukkit.main.cmd;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRLevelData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.storage.SaveHandler;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.playerdata.FPlayerData;

public class FCommandPlayerDataReset extends FCommand {
	
	public FCommandPlayerDataReset(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <fukkit|lotr|vanilla> <name|uuid>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		String type = args[0];
		String player = args[1];
		UUID uuid = FPlayerData.toUUID(player);
		Assert(uuid != null, "Invalid playername or uuid!");
		File file = null;
		if("fukkit".equals(type)) {
			file = new File(FData.instance().dir("players"), uuid + ".nbt");
		} else if("lotr".equals(type)) {
			try {
				file = (File)ReflectionHelper.findMethod(LOTRLevelData.class, null, new String[]{"getLOTRPlayerDat"}, UUID.class).invoke(null, uuid);
			} catch(Exception e) {
				Error("Failed to reflect!");
			}
		} else if("vanilla".equals(type)) {
			SaveHandler sh = ObfuscationReflectionHelper.getPrivateValue(ServerConfigurationManager.class, MinecraftServer.getServer().getConfigurationManager(), "field_72412_k");
			File dir = ObfuscationReflectionHelper.getPrivateValue(SaveHandler.class, sh, "field_75771_c");
			file = new File(dir, uuid+".dat");
		} else Invalid(type);
		Assert(file != null, "File not set! This should never happen. "+player);
		if(file.exists()) {
			if(file.delete()) {
				Info(sender, "Deleted playerdata for "+player+" ("+uuid+")!");
			} else {
				Info(sender, "Failed to delete playerdata for "+player+" ("+uuid+")!");
			}
		} else {
			Info(sender, "Playerdata nonexistant for "+player+" ("+uuid+")!");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"vanilla", "lotr", "fukkit"} : 
			args.length == 2 ? players() : 
			null;
	}
	
}
