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

public class FCommandChatMUTE extends FCommand {
	
	public FCommandChatMUTE(String phiName) {
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
		int i = FChatRP.onMUTE(target);
		if(i == 0) {
			Info(sender, "Muted " + target.getCommandSenderName() + ".");
			target.addChatMessage(Colors.CHAT("You are now muted!"));
		} else if(i == 1) {
			Info(sender, target.getCommandSenderName() + " is already muted.");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
