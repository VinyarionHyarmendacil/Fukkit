package vinyarion.fukkit.sm.cmd;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.cmd.FCommands;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FWarps;
import vinyarion.fukkit.main.util.Colors;

public class FCommandWarp extends FCommand {
	
	public FCommandWarp(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <warpname|add|remove> [<warpname> [<x> <y> <z> [<dimension>]]]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		FWarps warps = FWarps.instance();
		FPermissions perms = FPermissions.instance();
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		if(args.length > 0) {
			String warp = args[0];
			if(warp.equals("add")) {
				Assert(perms.hasPermission(player, FCommands.fukkit_perms.getCommandName()), "You do not have permission to add warps!");
				int x = MathHelper.floor_double(player.posX);
				int y = MathHelper.floor_double(player.posY + 0.1D);
				int z = MathHelper.floor_double(player.posZ);
				int dim = player.dimension;
				String name = args[1];
				if(args.length >= 5) {
					x = parseInt(player, args[2]);
					y = parseInt(player, args[3]);
					z = parseInt(player, args[4]);
				}
				if(args.length >= 6) {
					dim = parseInt(player, args[5]);
				}
				warps.add(player, name, x, y, z, dim);
				NotifyAll(sender, "Added warp '" + args[1] + "' at " + x + ", " + y + ", " + z + ", in dimension " + dim + ".");
			} else if(warp.equals("remove")) {
				Assert(perms.hasPermission(player, FCommands.fukkit_perms.getCommandName()), "You do not have permission to remove warps!");
				warps.remove(args[1]);
				NotifyAll(sender, "Removed warp '" + args[1] + "'.");
			} else if(warps.has(warp)) {
				warps.warp(player, warp);
				Info(sender, "Warping to " + warp + ".");
			} else Error("Warp nonexistant!");
		} else Error("Use /warp <warpName>");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		FWarps warps = FWarps.instance();
		return getListOfStringsFromIterableMatchingLastWord(args, warps.warps());
	}
	
}
