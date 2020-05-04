package vinyarion.fukkit.sm.discordbot;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import sx.blah.discord.handle.obj.IChannel;

public interface IDiscordDispatcher {

	public void sendToChannel(FDiscordBot bot, IChannel channel, String message, EntityPlayerMP player);
	
	public default void shutdown() { }

}
