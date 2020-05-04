package vinyarion.fukkit.sm.cmd;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandGroups extends FCommand {
	
	public FCommandGroups(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|remove> <playerName> <group>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 3, "Not enough arguments!");
		String op = args[0];
		String op2 = args[1];
		String op3 = rest(args, 2);
		EntityPlayerMP player = playerFor(sender, op2);
		Assert(player != null, "Must specify one player!");
		NBTTagCompound tag = FPlayerData.forPlayer(player.getCommandSenderName()).tag();
		if(op.equals("add")) {
			tag.setString(FPlayerData.GROUPS_TAG, op3);
			Info(sender, "Set " + op2 + "'s group to " + op3 + ".");
		} else if(op.equals("remove")) {
			tag.setString(FPlayerData.GROUPS_TAG, "");
			Info(sender, "Removed " + op2 + "'s group info.");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "remove"} : 
			args.length == 2 ? MinecraftServer.getServer().getAllUsernames() : null;
	}
	
}
