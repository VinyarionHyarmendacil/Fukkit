package vinyarion.fukkit.sm.cmd;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.util.Colors;

public class FCommandInvis extends FCommand {

	public FCommandInvis(String phiName) {
		super(phiName);
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " [<player> [<on|off|toggle>]]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayerMP player = null;
		String op = "toggle";
		if(args.length == 0) {
			Assert(sender instanceof EntityPlayerMP, "Must specify a player!");
			player = (EntityPlayerMP) sender;
		} else if(args.length >= 1) {
			player = this.playerFor(sender, args[0]);
			if(args.length >= 2) {
				op = args[1];
			}
		}
		Assert(player != null, "Must specify a player!");
		boolean flag = !player.isInvisible();
		if(op.equals("on")) {
			flag = true;
		} else if(op.equals("off")) {
			flag = false;
		} else if(!op.equals("toggle")) Invalid(op);
		player.setInvisible(flag);
		invis.remove(player.getCommandSenderName());
		if(flag) {
			invis.add(player.getCommandSenderName());
		}
		sender.addChatMessage(Colors.CHAT("Set " + player.getCommandSenderName() + "'s invisibility to [" + (flag ? "ON" : "OFF") + "]"));
		if(player != sender) {
			player.addChatMessage(Colors.CHAT("Your invisibility was set to [" + (flag ? "ON" : "OFF") + "]"));
		}
	}

	@Override
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? new String[]{"on", "off", "toggle"} : null;
	}

	public static List<String> invis = new ArrayList<String>();

}
