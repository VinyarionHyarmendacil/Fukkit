package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FServer;
import vinyarion.fukkit.main.util.Colors;

public class FCommandHeader extends FCommand {
	
	public FCommandHeader(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <'reset'|'show'|headerString>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 1, "Not enough args!");
		String op = rest(args, 0);
		if(op.equals("show")) {
			sender.addChatMessage(new ChatComponentText(Colors.uncolor(Colors.FUKKIT)));
		} else if(op.equals("reset")) {
			FServer.instance().tag.setString("header", Colors.FUKKIT = Colors.FUKKIT_default);
			Info(sender, "Reset header.");
		} else {
			FServer.instance().tag.setString("header", Colors.FUKKIT = Colors.color(op));
			Info(sender, "Set header.");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
