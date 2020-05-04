package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.data.FServer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.rpg.FPlayerDataRPG;

public class FCommandRPGSkillPoint extends FCommand {
	
	public FCommandRPGSkillPoint(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <player> <view|add|set> [<amount>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		EntityPlayerMP target = playerFor(sender, args[0]);
		Assert(target != null, "Must specify a player!");
		FPlayerDataRPG ps = FPlayerDataRPG.of(target);
		String op = args[1];
		if(op.equals("view")) {
			sender.addChatMessage(Colors.make(target.getCommandSenderName() + " has " + ps.skillPoints + " skill points."));
		} else if(op.equals("add")) {
			Assert(args.length >= 3, "Not enough args!");
			int add = parseInt(sender, args[2]);
			ps.skillPoints += add;
			sender.addChatMessage(Colors.make(target.getCommandSenderName() + " now has " + ps.skillPoints + " skill points; added " + add + "."));
		} else if(op.equals("set")) {
			Assert(args.length >= 3, "Not enough args!");
			int set = parseInt(sender, args[2]);
			ps.skillPoints = set;
			sender.addChatMessage(Colors.make(target.getCommandSenderName() + " now has " + ps.skillPoints + " skill points."));
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? new String[]{"view", "add", "set"} : 
			null;
	}
	
}
