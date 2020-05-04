package vinyarion.fukkit.sm.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.playerdata.FPlayerData;

public class FCommandRespond extends FCommand {
	
	public FCommandRespond(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <message>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		FPlayerData data = FPlayerData.forPlayer(sender.getCommandSenderName());
		if(data != null) {
			if(data.tagVolatile().hasKey("lastmsg")) {
				MinecraftServer.getServer().getCommandManager().executeCommand(sender, "/msg " + data.tagVolatile().getString("lastmsg") + " " + (args.length > 0 ? rest(args, 0) : ""));
			} else Error("You are not currently messaging someone.");
		} else Error("Must be a player to do this!");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
