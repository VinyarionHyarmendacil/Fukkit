package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.FLOTRHooks;

public class FCommandTPPos extends FCommand {

	public FCommandTPPos(String phiName) {
		super(phiName);
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <player> <x> <y> <z> [<dim>] [<locationName>]";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length > 3, "Not enough args!");
		EntityPlayerMP player = getPlayer(sender, args[0]);
		Assert(player != null, "Must specify a player!");
		int x = parseInt(sender, args[1]);
		int y = parseInt(sender, args[2]);
		int z = parseInt(sender, args[3]);
		int dim = args.length > 4 ? parseInt(sender, args[4]) : player.dimension;
		FLOTRHooks.warpViaFT(player, args.length > 5 ? rest(args, 5) : "somewhere", x, y, z, dim);
		Info(sender, "Teleported " + args[0] + " to " + args[1] + " " + args[2] + " " + args[3] + ".");
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? String.valueOf(getLookPos(sender)[1]) : 
			args.length == 3 ? String.valueOf(getLookPos(sender)[2]) : 
			args.length == 4 ? String.valueOf(getLookPos(sender)[3]) : 
			args.length == 5 ? String.valueOf(sender.getEntityWorld().provider.dimensionId) : 
			null;
	}

}
