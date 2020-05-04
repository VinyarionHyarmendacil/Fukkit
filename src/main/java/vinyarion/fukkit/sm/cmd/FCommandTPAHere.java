package vinyarion.fukkit.sm.cmd;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FWarps;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.sm.cmd.FCommandTPA.Request;
import vinyarion.fukkit.sm.cmd.FCommandTPA.ReverseRequest;

public class FCommandTPAHere extends FCommand {
	
	public FCommandTPAHere(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		FWarps warps = FWarps.instance();
		FPermissions perms = FPermissions.instance();
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		Assert(args.length > 0, "Must give a player!");
		String name = args[0];
		EntityPlayerMP target = getPlayer(player, name);
		Assert(target != null, "Must specify one player!");
		ReverseRequest req = new ReverseRequest(player, target);
		Request ar = null;
		Request tr = null;
		for(Request r : FCommandTPA.pending) {
			if(r.asker.equals(player)) {
				ar = r;
			}
			if(r.target.equals(target)) {
				tr = r;
			}
		}
		if(ar != null) {
			FCommandTPA.pending.remove(ar);
		}
		if(tr != null) {
			FCommandTPA.pending.remove(tr);
		}
		
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}
	
}
