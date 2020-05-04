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
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;

public class FCommandChatRP extends FCommand {
	
	public FCommandChatRP(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName();
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		int i = FChatRP.onRP((EntityPlayerMP)sender);
		if(i == 0) {
			Info(sender, "You have been added to the rp chat.");
		} else if(i == 1) {
			Info(sender, "You have been removed from the rp chat.");
		} else if(i == -1) {
			Info(sender, "You are muted. Y U DO DIS?!?!");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
