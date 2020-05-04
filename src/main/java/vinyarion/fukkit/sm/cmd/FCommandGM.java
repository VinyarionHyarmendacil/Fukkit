package vinyarion.fukkit.sm.cmd;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.cmd.FCommand;

public class FCommandGM extends FCommand {
	
	private static ICommand gm = (ICommand)MinecraftServer.getServer().getCommandManager().getCommands().get("gamemode");
	
	public FCommandGM(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return gm.getCommandUsage(sender);
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		gm.processCommand(sender, args);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return gm.addTabCompletionOptions(sender, args);
	}
	
}
