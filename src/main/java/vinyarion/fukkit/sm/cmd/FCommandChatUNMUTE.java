package vinyarion.fukkit.sm.cmd;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.game.FChatRP;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;

public class FCommandChatUNMUTE extends FCommand {
	
	public FCommandChatUNMUTE(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 1, "Not enough args!");
		String op = args[0];
		EntityPlayerMP target = playerFor(sender, op);
		Assert(target != null, "Must be a player!");
		int i = FChatRP.onUNMUTE(target);
		if(i == 0) {
			Info(sender, "Unmuted " + target.getCommandSenderName() + ".");
			target.addChatMessage(Colors.CHAT("You can now talk!"));
		} else if(i == 1) {
			Info(sender, target.getCommandSenderName() + " wasn't muted to begin with.");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
