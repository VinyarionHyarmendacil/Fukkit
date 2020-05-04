package vinyarion.fukkit.main.cmd;

import java.io.File;
import java.util.List;

import net.minecraft.command.ICommandSender;
import vinyarion.fukkit.main.data.FData;

public class FCommandStartup extends FCommand {
	
	private static File file = FData.instance().file("startup", "txt");
	
	public FCommandStartup(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|set|rem|clear|list> <index> <command>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 3, "Not enough args!");
		String op = args[0];
		String op2 = args[1];
		String cmd = rest(args, 2);
		int i = parseInt(sender, op2);
		List<String> cmds = FData.readRawLines(file);
		boolean save = false;
		if(op.equals("add")) {
			if(i == -1) {
				save = cmds.add(cmd);
			} else {
				cmds.add(i, cmd);
				save = true;
			}
			Info(sender, save ? "Added." : "Not added.");
		} else if(op.equals("set")) {
			save = cmds.set(i, cmd) != null;
			Info(sender, save ? "Set." : "Not set.");
		} else if(op.equals("rem")) {
			save = cmds.remove(i) != null;
			Info(sender, save ? "Removed." : "Not removed.");
		} else if(op.equals("clear")) {
			cmds.clear();
			save = true;
			Info(sender, "Cleared.");
		} else if(op.equals("list")) {
			for(String s : cmds) {
				Info(sender, s);
			}
		}
		if(save) {
			FData.writeRawLines(file, cmds);
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "set", "rem", "clear", "list"} : null;
	}
	
}
