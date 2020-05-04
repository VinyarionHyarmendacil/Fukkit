package vinyarion.fukkit.sm.cmd;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.sm.blocklog.BlockLogQuerey;
import vinyarion.fukkit.sm.blocklog.FBlockLog;

public class FCommandBlockLogQuerey extends FCommand {

	public FCommandBlockLogQuerey(String phiName) {
		super(phiName);
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " [<constraint> <value>]...";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		BlockLogQuerey querey = new BlockLogQuerey();
		try {
			for(int i = 0; i < args.length-1; i++) {
				String constraint = args[i];
				String value = args[i+1];
				if(constraint.equals("mintime")) {
					querey.mintime(Long.parseLong(value));
				} else if(constraint.equals("maxtime")) {
					querey.maxtime(Long.parseLong(value));
				} else if(constraint.equals("minx")) {
					querey.minx(Integer.parseInt(value));
				} else if(constraint.equals("maxx")) {
					querey.maxx(Integer.parseInt(value));
				} else if(constraint.equals("miny")) {
					querey.miny(Integer.parseInt(value));
				} else if(constraint.equals("maxy")) {
					querey.maxy(Integer.parseInt(value));
				} else if(constraint.equals("minz")) {
					querey.minz(Integer.parseInt(value));
				} else if(constraint.equals("maxz")) {
					querey.maxz(Integer.parseInt(value));
				} else if(constraint.equals("block")) {
					querey.block(getBlockByText(sender, value));
				} else if(constraint.equals("meta")) {
					querey.meta(Integer.parseInt(value));
				} else if(constraint.equals("broke")) {
					querey.broke(Boolean.parseBoolean(value));
				} else if(constraint.equals("block")) {
					querey.block(getBlockByText(sender, value));
				} else if(constraint.equals("player")) {
					querey.player(playerFor(sender, value));
				} else Invalid(constraint);
			}
		} catch(Exception e) {
			Error(e.getMessage());
		}
		FBlockLog.INSTANCE.search(querey, sender);
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length % 2 == 0 ? new String[]{"mintime", "maxtime", "player", "block", "meta", "broke", "minx", "maxx", "miny", "maxy", "minz", "maxz"} : 
			args[args.length - 2].endsWith("x") ? String.valueOf(getLookPos(sender)[0]) : 
			args[args.length - 2].endsWith("y") ? String.valueOf(getLookPos(sender)[1]) : 
			args[args.length - 2].endsWith("z") ? String.valueOf(getLookPos(sender)[2]) : 
			args[args.length - 2].equals("block") ? Block.blockRegistry.getKeys() : 
			args[args.length - 2].equals("player") ? players() : 
			args[args.length - 2].equals("broke") ? new String[]{"true", "false"} : 
			args[args.length - 2].equals("meta") ? new String[]{"-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"} : 
			args[args.length - 2].endsWith("time") ? String.valueOf(System.currentTimeMillis()) : 
			null;
	}

}
