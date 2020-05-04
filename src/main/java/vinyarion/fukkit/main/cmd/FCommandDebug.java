package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import vinyarion.fukkit.main.util.Misc;

public class FCommandDebug extends FCommand {
	
	public FCommandDebug(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName();
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Info(sender, "Debug is now " + ((Misc.debug = !Misc.debug) ? "&aON" : "&cOFF"));
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
