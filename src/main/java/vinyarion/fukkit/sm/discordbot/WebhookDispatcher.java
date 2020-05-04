package vinyarion.fukkit.sm.discordbot;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IWebhook;
import vinyarion.fukkit.main.cmd.FCommandNick;
import vinyarion.fukkit.main.cmd.FCommandTitles;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class WebhookDispatcher implements IDiscordDispatcher {
	
	private Thread taskThread = new Thread(this::runDispatcher);
	private volatile boolean stopped = false;
	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1024);
	
	public WebhookDispatcher() {
		taskThread.start();
	}
	
	public void shutdown() {
		stopped = true;
	}
	
	public void sendToChannel(FDiscordBot bot, IChannel channel, String message, EntityPlayerMP player) {
		for(IWebhook webhook : channel.getWebhooks()) {
			if(webhook.getDefaultName().equals(player.getCommandSenderName())) {
				this.send(message, player, webhook);
				return;
			}
		}
		this.send(message, player, channel.createWebhook(player.getCommandSenderName()));
	}
	
	private void send(String message, EntityPlayerMP player, IWebhook webhook) {
		String prefix = "";
		String suffix = "";
		NBTTagCompound tag = FPlayerData.forPlayer(player.getCommandSenderName()).tag();
		if(tag.hasKey(FCommandTitles.CHAT_TAGS, NBT.COMPOUND)) {
			NBTTagCompound tags = tag.getCompoundTag(FCommandTitles.CHAT_TAGS);
			if(tags.hasKey("prefix", NBT.STRING)) {
				prefix = tags.getString("prefix");
			}
			if(tags.hasKey("suffix", NBT.STRING)) {
				suffix = tags.getString("suffix");
			}
		}
		String name = Colors.getTextWithoutAnyFormattingCodes(prefix + FCommandNick.name(player).getUnformattedTextForChat() + suffix);
//		String send = Colors.clean(name) + Colors.TALK + message;
		try {
			queue.put(()->{
				webhook.changeDefaultName(name);
				webhook.getChannel().sendMessage(message);
				webhook.changeDefaultName(player.getCommandSenderName());
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void runDispatcher() {
		while(!stopped) {
			try {
				queue.take().run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
