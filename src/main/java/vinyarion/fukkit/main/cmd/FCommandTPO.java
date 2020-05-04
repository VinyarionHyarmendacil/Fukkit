package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FWarps;
import vinyarion.fukkit.sm.cmd.FCommandTPA.Request;

public class FCommandTPO extends FCommand {
	
	public FCommandTPO(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player> [<toPlayer>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		FWarps warps = FWarps.instance();
		FPermissions perms = FPermissions.instance();
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP p0 = (EntityPlayerMP)sender;
		Assert(args.length > 0, "Must give a player!");
		String op = args[0];
		EntityPlayerMP p1 = playerFor(p0, op);
		Assert(p1 != null, "Must specify one player!");
		if(args.length >= 2) {
			String op2 = args[1];
			EntityPlayerMP p2 = playerFor(p0, op2);
			Assert(p2 != null, "Must specify one player!");
			FLOTRHooks.warpViaFT(p1, p2.getCommandSenderName(), p2.posX, p2.posY, p2.posZ, p2.dimension);
			Info(sender, "Teleported " + op + " to " + op2 + ".");
		} else {
			FLOTRHooks.warpViaFT(p0, p1.getCommandSenderName(), p1.posX, p1.posY, p1.posZ, p1.dimension);
			Info(sender, "Teleported to " + op + ".");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}
	
}
