package vinyarion.fukkit.sm.cmd;

import net.minecraft.command.ICommandSender;
import vinyarion.fukkit.main.cmd.FCommand;

public class FCommandPMSpy extends FCommand {
	
	public FCommandPMSpy(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return null;
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
