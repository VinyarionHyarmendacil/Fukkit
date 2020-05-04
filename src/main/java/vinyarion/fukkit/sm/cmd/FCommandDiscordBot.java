package vinyarion.fukkit.sm.cmd;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.ForgeHooks;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FServer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;

public class FCommandDiscordBot extends FCommand {
	
	public static final String DISCORDINVITE = "DiscordInvite";

	public FCommandDiscordBot(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <info|token|start|stop|hstart|hstop|status|channel|console|purge|invite> [<tokenString|guild:cat:channel|guild:channel|inviteLink>]";
	}
	
	public void processCommand(final ICommandSender sender, String[] args) {
		Assert(args.length >= 1, "Not enough args!");
		String op = args[0];
		if(op.equals("info")) {
			Info(sender, "Link to add bot to discord:");
			sender.addChatMessage(ForgeHooks.newChatWithLinks(FDiscordBot.botAddURL));
			Info(sender, "All channels:");
			if(FDiscordBot.botInitialized()) {
				for(IChannel c : FDiscordBot.getInstance().getClient().getChannels()) {
					String cn = c.getName();
					String qn = c.getCategory().getName();
					String gn = c.getGuild().getName();
					String cl = Long.toUnsignedString(c.getLongID());
					String ql = Long.toUnsignedString(c.getCategory().getLongID());
					String gl = Long.toUnsignedString(c.getGuild().getLongID());
					Info(sender, "Channel " + cn + ":");
					Info(sender, "  " + gn.replace(" ", "") + ":" + qn.replace(" ", "") + ":" + cn.replace(" ", ""));
					Info(sender, "  " + gl + ":" + ql + ":" + cl);
				}
			}
			return;
		} else if(op.equals("status")) {
			Assert(FDiscordBot.botInitialized(), "Bot not available, start it!");
			IChannel c = FDiscordBot.getInstance().getChannel();
			Info(sender, "Current channel: " + (c == null ? null : (c.getName() + "@" + c.getCategory().getName() + "@" + c.getGuild().getName())));
			if(c != null) {
				int i = 0;
				int j = 0;
				for(IUser user : c.getGuild().getUsers()) {
					StatusType s = user.getPresence().getStatus();
					if(s == StatusType.ONLINE || s == StatusType.IDLE || s == StatusType.DND) {
						i++;
					}
					j++;
				}
				Info(sender, "There are " + i + " of " + j + " total members online.");
			}
			return;
		} else if(op.equals("start")) {
			Assert(!FDiscordBot.botInitialized(), "Bot already started!");
			Assert(FDiscordBot.hastoken(), "No token set!");
			new Thread(new Runnable() { public void run() {
				FDiscordBot.start();
				if(FMod.config.doConsoleBotStart) NotifyAll(sender, "Started bot!"); else Info(sender, "Started bot!");
			}}).start();
			return;
		} else if(op.equals("stop")) {
			Assert(FDiscordBot.botInitialized(), "Bot already stopped!");
			new Thread(new Runnable() { public void run() {
				FDiscordBot.stop();
				if(FMod.config.doConsoleBotStart) NotifyAll(sender, "Stopped bot!"); else Info(sender, "Stopped bot!");
			}}).start();
			return;
		} else if(op.equals("hstart")) {
			Assert(!FDiscordBot.botInitialized(), "Bot already started!");
			Assert(FDiscordBot.hastoken(), "No token set!");
			FDiscordBot.start();
			if(FMod.config.doConsoleBotStart) NotifyAll(sender, "Started bot!"); else Info(sender, "Started bot!");
			return;
		} else if(op.equals("hstop")) {
			Assert(FDiscordBot.botInitialized(), "Bot already stopped!");
			FDiscordBot.stop();
			if(FMod.config.doConsoleBotStart) NotifyAll(sender, "Stopped bot!"); else Info(sender, "Stopped bot!");
			return;
		}
		Assert(args.length >= 2, "Not enough args!");
		String op2 = args[1];
		if(op.equals("token")) {
			FDiscordBot.setToken(op2);
			if(FMod.config.doConsoleBotStart) NotifyAll(sender, "Set token!"); else Info(sender, "Set token!");
			return;
		}
		Assert(FDiscordBot.botInitialized(), "Bot not available, start it!");
		if(op.equals("channel")) {
			int i = FDiscordBot.getInstance().setChat(op2);
			if(i == 0) {
				if(FMod.config.doConsoleBotStart) NotifyAll(sender, "Set listener channel!"); else Info(sender, "Set listener channel!");
			} else {
				if(i == -2) {
					Error("Unknown error!");
				} else if(i == -1) {
					Error("No such channel!");
				} else if(i == 1) {
					Error("Invalid ':' format!");
				} else if(i == 2) {
					Error("Invalid long id!");
				}
			}
		} else if(op.equals("console")) {
			int i = FDiscordBot.getInstance().setLog(op2);
			if(i == 0) {
				if(FMod.config.doConsoleBotStart) NotifyAll(sender, "Set listener console!"); else Info(sender, "Set listener console!");
			} else {
				if(i == -2) {
					Error("Unknown error!");
				} else if(i == -1) {
					Error("No such channel!");
				} else if(i == 1) {
					Error("Invalid ':' format!");
				} else if(i == 2) {
					Error("Invalid long id!");
				}
			}
		} else if(op.equals("purge")) {
			FDiscordBot.purge();
			Info(sender, "Bot message queue purged!");
		} else if(op.equals("invite")) {
			String link = op2.contains("/") ? op2.substring(op2.lastIndexOf("/"), op2.length()) : op2;
			FServer.instance().tag.setString(DISCORDINVITE, link);
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"info", "token", "start", "stop", "hstart", "hstop", "status", "channel", "console", "purge", "invite"} : null;
	}
	
}
