package vinyarion.fukkit.sm.cmd;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FTempPerms;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandTempPerms extends FCommand {
	
	public FCommandTempPerms(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player> <hh:mm:ss|revoke|revokeAll> [<perm>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		String op = args[0];
		String op2 = args[1];
		FPlayerData data = FPlayerData.forPlayer(op);
		Assert(data != null, "Must specify a player!");
		if(op2.equals("revoke")) {
			Assert(args.length >= 3, "Not enough args!");
			String op3 = args[2];
			if(FTempPerms.revoke(data.tag(), op3)) {
				Info(sender, "Revoked temp permission " + op3);
				Info(data.player(), "Your permission for " + op3 + " was revoked!");
			} else {
				Info(sender, "Player " + op + " did not have applicable temp permission " + op3);
			}
		} else if(op2.equals("revokeAll")) {
			FTempPerms.revokeAll(data.tag());
			Info(sender, "Revoked all temp permissions for " + op);
			Info(data.player(), "All your temporary permissions were revoked!");
		} else if(Time.ishhmmss(op2)) {
			Assert(args.length >= 3, "Not enough args!");
			String op3 = args[2];
			int i = FTempPerms.add(data.tag(), op3, Time.instantNow(), op2);
			String rt = Time.readable(Time.hhmmss(op2));
			Info(sender, "Added temp permission " + op3 + " to " + op + " for " + rt + ".");
			Info(data.player(), "You recieved the temporary permission " + op3 + " for " + rt + ".");
		} else Invalid(op2);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? new String[]{"00:00:00", "revoke", "revokeAll"} : 
			args.length == 3 ? FPermissions.instance().attribs() : null;
	}
	
}
