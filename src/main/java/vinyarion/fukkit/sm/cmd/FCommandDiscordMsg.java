package vinyarion.fukkit.sm.cmd;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.cmd.FCommandNick;
import vinyarion.fukkit.main.cmd.FCommandTitles;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;

public class FCommandDiscordMsg extends FCommand {
	
	public FCommandDiscordMsg(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <discordUser> <msg>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(FDiscordBot.botInitialized(), "Bot not started! Ask a staff member to start it.");
		Assert(FDiscordBot.getInstance().hasChannel(), "Channel not set! Ask a staff member to set it.");
		Assert(args.length >= 1, "Not enough args!");
		String op = args[0];
		Assert(args.length >= 2, "Specify a message!");
		String msg = rest(args, 1);
		IGuild guild = FDiscordBot.getInstance().getChannel().getGuild();
		for(IUser user : guild.getUsers()) {
			if(user.getDisplayName(guild).replace(" ", "").equals(op) || 
				user.getName().replace(" ", "").equals(op)) {
				String name = sender.getCommandSenderName();
				if(sender instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP)sender;
					String prefix = "";
					String suffix = "";
					NBTTagCompound tag = FPlayerData.forPlayer(name).tag();
					if(tag.hasKey(FCommandTitles.CHAT_TAGS, NBT.COMPOUND)) {
						NBTTagCompound tags = tag.getCompoundTag(FCommandTitles.CHAT_TAGS);
						if(tags.hasKey("prefix", NBT.STRING)) {
							prefix = tags.getString("prefix");
						}
						if(tags.hasKey("suffix", NBT.STRING)) {
							suffix = tags.getString("suffix");
						}
					}
					name = "(" + player.getCommandSenderName() + ") " + prefix + FCommandNick.name(player).getUnformattedTextForChat() + suffix;
				}
				IPrivateChannel pmc = user.getOrCreatePMChannel();
				if(pmc.getCreationDate().isAfter(LocalDateTime.now().minusSeconds(10))) {
					pmc.sendMessage("You are recieving private messages from people on the server.");
					pmc.sendMessage("To privately respond to them, type 'tell <player> <message...>' or 'msg <player> <message...>'");
				}
				pmc.sendMessage(Colors.clean(name + Colors.TALK + msg));
				println(sender, "&f[&6Discord&f]: you to " + op + Colors.TALK + msg);
				return;
			}
		}
		Error("No one on Discord with that name!");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		if(!FDiscordBot.botInitialized()) {
			Info(sender, "Bot not started! Ask a staff member to start it.");
			return null;
		}
		IGuild guild = FDiscordBot.getInstance().getChannel().getGuild();
		List<String> names = new ArrayList<String>();
		for(IUser user : guild.getUsers()) {
			names.add(user.getDisplayName(guild).replace(" ", ""));
		}
		return names;
	}
	
}
