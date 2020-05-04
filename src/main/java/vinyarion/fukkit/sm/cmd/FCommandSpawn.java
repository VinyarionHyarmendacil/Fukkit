package vinyarion.fukkit.sm.cmd;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.cmd.FCommands;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FWarps;
import vinyarion.fukkit.main.util.Colors;

public class FCommandSpawn extends FCommand {
	
	public FCommandSpawn(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " [<set>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		FWarps warps = FWarps.instance();
		if(args.length == 1 && args[0].equals("set") && sender instanceof EntityPlayer) {
			Assert(FPermissions.instance().hasPermission((EntityPlayer)sender, FCommands.fukkit_perms.getCommandName()), "You do not have permission to set spawn!");
			warps.add(sender, "spawn");
			NotifyAll(sender, "Set world spawn.");
		} else if(sender instanceof EntityPlayerMP) {
			warps.warp((EntityPlayerMP)sender, "spawn");
			Info(sender, "Warping to spawn.");
		} else Error("Must be a player!");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
