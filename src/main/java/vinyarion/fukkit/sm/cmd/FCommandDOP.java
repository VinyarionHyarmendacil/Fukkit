package vinyarion.fukkit.sm.cmd;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;
import vinyarion.fukkit.sm.discordbot.FDiscordConsoleUsers;

public class FCommandDOP extends FCommand {
	
	public FCommandDOP(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <user>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length > 0, "Not enough args!");
		String op = rest(args, 0);
		if(FDiscordConsoleUsers.instance().add(op)) {
			Info(sender, "Opped " + op);
			FDiscordBot.getInstance().dmUserIfStarted(op, "You are now op!");
		} else {
			Info(sender, op + " is already opped!");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		if(!FDiscordBot.botInitialized()) {
			Info(sender, "Bot not started! Ask a staff member to start it.");
			return null;
		}
		if(!FDiscordBot.getInstance().hasChannel()) {
			Info(sender, "Channel not set! Ask a staff member to set it.");
			return null;
		}
		IGuild guild = FDiscordBot.getInstance().getChannel().getGuild();
		List<String> names = new ArrayList<String>();
		for(IUser user : guild.getUsers()) {
			names.add(user.getName());
		}
		return names;
	}
	
}
