package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.data.FScripts;
import vinyarion.fukkit.main.script.FScript;

public class FCommandScript extends FCommand {

	public FCommandScript(String phiName) {
		super(phiName);
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <reload|view|run> <scriptToExecute> [<player|'server'>]";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		args = reparse(args);
		Assert(args.length > 1, "Not enough args!");
		String op = args[0];
		String sn = args[1];
		FScript sc = FScripts.instance().get(sn);
		Assert(sc != null, "That script does not exist!");
		if(op.equals("reload")) {
			FScripts.instance().reload(sn);
			Info(sender, "Reloaded the script'" + sn + "'.");
		} else if(op.equals("view")) {
			Info(sender, "Commands of the script'" + sn + "':");
			if(sc.data.size() == 0) {
				Info(sender, "Empty");
			} else {
				for(String cmd : sc.data) {
					Info(sender, ">" + cmd);
				}
			}
		} else if(op.equals("run")) {
			EntityPlayerMP player = sender instanceof EntityPlayerMP ? (EntityPlayerMP)sender : null;
			boolean server = false;
			if(player == null || args.length > 2) {
				Assert(args.length > 2, "Not enough args!");
				String pn = args[2];
				server = pn.equals("'server'");
				player = args.length > 2 ? playerFor(sender, args[2]) : null;
			}
			if(!server) Assert(player != null, "Either become a player or specify one!");
			sc.command(player);
		} else Invalid(op);
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"reload", "view", "run"} : 
			args.length == 2 ? FScripts.instance().names() : 
				allOf(players(), "'server'");
	}

}
