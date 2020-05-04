package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;

public class FCommandRPGGui extends FCommand {

	private static final String[] _args = new String[]{
		"display", 
		"system/rpg/main"
	};

	public FCommandRPGGui(String phiName) {
		super(phiName);
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName();
	}

	public void processCommand(ICommandSender sender, String[] args) {
		FCommands.fukkit_gui.processCommand(sender, _args);
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}

}
