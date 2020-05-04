package vinyarion.fukkit.sm.cmd;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;
import vinyarion.fukkit.sm.discordbot.FDiscordConsoleUsers;

public class FCommandDOPList extends FCommand {
	
	public FCommandDOPList(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName();
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		String[] peeps = FDiscordConsoleUsers.instance().names();
		if(peeps.length == 0) {
			Info(sender, "No one is opped right now!");
		} else {
			for(String peep : peeps) {
				Info(sender, "  --> " + peep);
			}
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
