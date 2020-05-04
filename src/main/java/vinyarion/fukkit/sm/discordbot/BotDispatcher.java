package vinyarion.fukkit.sm.discordbot;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import sx.blah.discord.handle.obj.IChannel;
import vinyarion.fukkit.main.cmd.FCommandNick;
import vinyarion.fukkit.main.cmd.FCommandTitles;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class BotDispatcher implements IDiscordDispatcher {
	
	public void sendToChannel(FDiscordBot bot, IChannel channel, String message, EntityPlayerMP player) {
		String prefix = "";
		String suffix = "";
		String name = player.getCommandSenderName();
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
		name = prefix + FCommandNick.name(player).getUnformattedTextForChat() + suffix;
		String send = Colors.clean(name) + Colors.TALK + message;
		bot.queueChannel(send);
	}
	
}
