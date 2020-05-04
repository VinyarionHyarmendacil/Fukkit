package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.util.Colors;

public class FukkitCommand extends FCommand {
	
	public FukkitCommand(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName();
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Info(sender, "Version " + FMod.version);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
