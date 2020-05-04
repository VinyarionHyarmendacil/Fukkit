package vinyarion.fukkit.sm.cmd;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FWarps;

public class FCommandTPADeny extends FCommand {
	
	public FCommandTPADeny(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName();
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		FCommandTPA.deny((EntityPlayerMP)sender);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
