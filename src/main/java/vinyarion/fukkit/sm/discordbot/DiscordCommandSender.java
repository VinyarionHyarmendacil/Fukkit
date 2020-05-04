package vinyarion.fukkit.sm.discordbot;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import vinyarion.fukkit.main.util.Colors;

public class DiscordCommandSender implements ICommandSender {
	
	private final IChannel channel;
	private final IUser user;
	
	public DiscordCommandSender(IUser user, IChannel channel) {
		this.channel = channel;
		this.user = user;
	}
	
	public String getCommandSenderName() {
		return "[D]" + user.getName();
	}
	
	public IChatComponent func_145748_c_() {
        ChatComponentText chatcomponenttext = new ChatComponentText(this.getCommandSenderName());
        chatcomponenttext.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dmsg " + user.getName().replace(" ", "") + " "));
        return chatcomponenttext;
    }
	
	public void addChatMessage(IChatComponent comp) {
		if(channel == FDiscordBot.getInstance().getLog()) {
			FDiscordBot.getInstance().queueLog(Colors.clean(comp.getUnformattedText()));
		} else if(channel == FDiscordBot.getInstance().getChannel()) {
			FDiscordBot.getInstance().queueChannel(Colors.clean(comp.getUnformattedText()));
		} else {
			channel.sendMessage(Colors.clean(comp.getUnformattedText()));
		}
	}
	
	public boolean canCommandSenderUseCommand(int i, String string) {
		return MinecraftServer.getServer().canCommandSenderUseCommand(i, string);
	}
	
	public ChunkCoordinates getPlayerCoordinates() {
		return MinecraftServer.getServer().getPlayerCoordinates();
	}
	
	public World getEntityWorld() {
		return MinecraftServer.getServer().getEntityWorld();
	}
	
}
