package vinyarion.fukkit.main.cmd;

import java.io.File;
import java.util.UUID;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import lotr.common.LOTRLevelData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.storage.SaveHandler;
import vinyarion.fukkit.main.playerdata.FPlayerData;

public class FCommandPlayerDataSave extends FCommand {
	
	public FCommandPlayerDataSave(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <fukkit|lotr|vanilla> <name|uuid>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		String type = args[0];
		String player = args[1];
		File file = null;
		EntityPlayerMP emp = playerFor(sender, player);
		Assert(emp != null, "Must specify a player!");
		if("fukkit".equals(type)) {
			FPlayerData pd = FPlayerData.forPlayer(emp);
			Assert(pd != null, "Data not found!");
			pd.save();
			Info(sender, "Player data for "+player+" saved!");
		} else if("lotr".equals(type)) {
			LOTRLevelData.saveData(emp.getGameProfile().getId());
			Info(sender, "Player data for "+player+" saved!");
		} else if("vanilla".equals(type)) {
			SaveHandler sh = ObfuscationReflectionHelper.getPrivateValue(ServerConfigurationManager.class, MinecraftServer.getServer().getConfigurationManager(), "field_72412_k");
			sh.writePlayerData(emp);
			Info(sender, "Player data for "+player+" saved!");
		} else Invalid(type);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"vanilla", "lotr", "fukkit"} : 
			args.length == 2 ? players() : 
			null;
	}
	
}
