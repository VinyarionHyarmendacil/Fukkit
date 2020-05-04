package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FServer;

public class FCommandReloadConfig extends FCommand {
	
	public FCommandReloadConfig(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName();
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		FMod.config.set();
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
