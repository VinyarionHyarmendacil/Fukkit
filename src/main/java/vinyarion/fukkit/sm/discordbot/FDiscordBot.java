package vinyarion.fukkit.sm.discordbot;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.cmd.FCommandNick;
import vinyarion.fukkit.main.cmd.FCommandTitles;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FDiscordBot {
	
	private static FDiscordBot instance;
	private static String token = null;
	
	private static List<String> logTasks = new ArrayList<String>();
	private static List<String> dispatcherTasks = new ArrayList<String>();
	private static List<IChatComponent> minecraftTasks = new ArrayList<IChatComponent>();
	
	public static void purge() {
		logTasks.clear();
		dispatcherTasks.clear();
		minecraftTasks.clear();
	}
	
	public static boolean hastoken() {
		return token != null;
	}
	
	public static boolean botInitialized() {
		return instance != null;
	}
	
	public static void setToken(String tokenString) {
		token = tokenString;
	}
	
	public static FDiscordBot getInstance() {
		return instance;
	}

	public static void start() {
		if(!hastoken()) {
			FCommand.NotifyAll(MinecraftServer.getServer(), "Must set a token!");
			return;
		}
		if(botInitialized()) {
			FCommand.NotifyAll(MinecraftServer.getServer(), "Already started!");
			return;
		}
		instance = new FDiscordBot(new ClientBuilder().withToken(token).build());
		instance.client.login();
		instance.client.getDispatcher().registerListener(new ChatListener());
		final Object lock = new Object();
		synchronized(lock) {
			while(!instance.client.isReady()) {
				try {
					lock.wait(10);
				} catch(Exception e) { }
			}
		}
	}
	
	public static void flush(boolean wait) {
		if(!botInitialized()) {
			FCommand.NotifyAll(MinecraftServer.getServer(), "Bot not running!");
			return;
		}
		final Object lock = new Object();
		synchronized(lock) {
			while(instance.tickDispatcher()) {
				try {
					lock.wait(400);
				} catch(Exception e) { }
				if(!wait) break;
			}
		}
	}
	
	public static void stop() {
		if(!botInitialized()) {
			FCommand.NotifyAll(MinecraftServer.getServer(), "Bot not running!");
			return;
		}
		instance.client.logout();
		instance = null;
	}
	
//	public static final String botID = "419545404061909002";
//	public static final String botAddURL = "https://discordapp.com/api/oauth2/authorize?client_id=419545404061909002&scope=bot&permissions=1";
	public static final String botAddURL = "https://discordapp.com/api/oauth2/authorize?client_id=419545404061909002&scope=bot&permissions=1";
	
	private FDiscordBot(IDiscordClient client) {
		this.client = client;
	}
	
	public boolean tickDispatcher() {
		boolean ret = false;
		try {
			while(!minecraftTasks.isEmpty()) {
				IChatComponent chat = minecraftTasks.get(0);
				MinecraftServer.getServer().getConfigurationManager().sendChatMsg(chat);
				minecraftTasks.remove(0);
			}
		} catch(Exception e) { 
			ret = true;
		}
		try {
			while(!logTasks.isEmpty()) {
				String msg = logTasks.get(0);
				discordLog.sendMessage(msg);
				logTasks.remove(0);
			}
		} catch(Exception e) {
			ret = true;
		}
		try {
			while(!dispatcherTasks.isEmpty()) {
				String msg = dispatcherTasks.get(0);
				discordChat.sendMessage(msg);
				dispatcherTasks.remove(0);
			}
		} catch(Exception e) {
			ret = true;
		}
		return ret;
	}
	
	public IDiscordDispatcher dispatcher = FMod.config.doWebhooks ? new WebhookDispatcher() : new BotDispatcher();
	private IDiscordClient client;
	private IChannel discordChat = null;
	private IChannel discordLog = null;
	
	public IDiscordClient getClient() {
		return client;
	}
	
	public boolean hasChannel() {
		return discordChat != null;
	}
	
	public IChannel getChannel() {
		return discordChat;
	}
	
	public boolean hasLog() {
		return discordLog != null;
	}
	
	public IChannel getLog() {
		return discordLog;
	}
	
	public int setChat(String string) {
		try {
			String[] args = string.split(":");
			String guild = args[0];
			String category = args.length >= 3 ? args[1] : null;
			String channel = args.length >= 3 ? args[2] : args[1];
			long lg = 0;
			long lq = 0;
			long lc = 0;
			try {
				lg = Long.parseUnsignedLong(guild);
			} catch(Exception e) { }
			try {
				lq = Long.parseUnsignedLong(category);
			} catch(Exception e) { }
			try {
				lc = Long.parseUnsignedLong(channel);
			} catch(Exception e) { }
			for(IChannel c : client.getChannels()) {
				if(c.getName().replace(" ", "").equals(channel) || c.getLongID() == lc) {
					if(category == null) {
						if(c.getGuild().getName().replace(" ", "").equals(guild) || c.getGuild().getLongID() == lg) {
							discordChat = c;
							return 0;
						}
					} else {
						if(c.getCategory().getName().replace(" ", "").equals(category) || c.getCategory().getLongID() == lq) {
							if(c.getGuild().getName().replace(" ", "").equals(guild) || c.getGuild().getLongID() == lg) {
								discordChat = c;
								return 0;
							}
						}
					}
				}
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			return 1;
		} catch(NumberFormatException e) {
			return 2;
		} catch(Exception e) {
			return -2;
		}
		return -1;
	}
	
	public int setLog(String string) {
		try {
			String[] args = string.split(":");
			String guild = args[0];
			String category = args.length >= 3 ? args[1] : null;
			String channel = args.length >= 3 ? args[2] : args[1];
			long lg = 0;
			long lq = 0;
			long lc = 0;
			try {
				lg = Long.parseUnsignedLong(guild);
			} catch(Exception e) { }
			try {
				lq = Long.parseUnsignedLong(category);
			} catch(Exception e) { }
			try {
				lc = Long.parseUnsignedLong(channel);
			} catch(Exception e) { }
			for(IChannel c : client.getChannels()) {
				if(c.getName().replace(" ", "").equals(channel) || c.getLongID() == lc) {
					if(category == null) {
						if(c.getGuild().getName().replace(" ", "").equals(guild) || c.getGuild().getLongID() == lg) {
							discordLog = c;
							return 0;
						}
					} else {
						if(c.getCategory().getName().replace(" ", "").equals(category) || c.getCategory().getLongID() == lq) {
							if(c.getGuild().getName().replace(" ", "").equals(guild) || c.getGuild().getLongID() == lg) {
								discordLog = c;
								return 0;
							}
						}
					}
				}
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			return 1;
		} catch(NumberFormatException e) {
			return 2;
		} catch(Exception e) {
			return -2;
		}
		return -1;
	}
	
	public void dmUserIfStarted(final String name, final String message) {
		if(!FDiscordBot.botInitialized() || !FDiscordBot.getInstance().hasChannel()) {
			return;
		}
		IGuild guild = FDiscordBot.getInstance().getChannel().getGuild();
		List<String> names = new ArrayList<String>();
		for(IUser user : guild.getUsers()) {
			if(name.equals(user.getName())) {
				user.getOrCreatePMChannel().sendMessage(message);
				return;
			}
		}
	}
	
	public void queueChat(final IMessage chat) {
		if(!FDiscordBot.botInitialized() || !FDiscordBot.getInstance().hasChannel()) {
			return;
		}
		if(chat.getAuthor().isBot()) {
			return;
		}
		String name = chat.getAuthor().getDisplayName(chat.getGuild());
		String msg = chat.getFormattedContent();
		if(!chat.getChannel().equals(discordChat)) {
			if(chat.getChannel().isPrivate()) {
				if(msg.startsWith("msg") || msg.startsWith("tell")) {
					String[] sa = msg.split(" ");
					EntityPlayerMP to = CommandBase.getPlayer(MinecraftServer.getServer(), sa[1]);
					String msgimpl = FCommand.rest(sa, 2);
					ChatComponentText text = new ChatComponentText(Colors.color("&f[&6Discord&f] " + name + " to you" + Colors.TALK + msgimpl));
					to.addChatMessage(text);
				} else if(FDiscordConsoleUsers.instance().has(chat.getAuthor().getName())) {
					MinecraftServer.getServer().getCommandManager().executeCommand(new DiscordCommandSender(chat.getAuthor(), chat.getChannel()), chat.getContent());
				}
			} else if(chat.getChannel().equals(discordLog)) {
				MinecraftServer.getServer().getCommandManager().executeCommand(new DiscordCommandSender(chat.getAuthor(), chat.getChannel()), chat.getContent());
			}
			return;
		}
		ChatComponentText text = (ChatComponentText)Colors.MAKE("&f[&6Discord&f] ").appendText(name);
		text.getChatStyle().setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/dmsg " + name.replace(" ", "") + " "));
		text.appendText(Colors.TALK + msg);
		minecraftTasks.add(text);
	}
	
	public void queueChannel(final String msg) {
		if(!FDiscordBot.botInitialized() || !FDiscordBot.getInstance().hasChannel()) {
			return;
		}
		dispatcherTasks.add(msg);
	}
	
	public void queueLog(final String msg) {
		if(!FDiscordBot.botInitialized() || !FDiscordBot.getInstance().hasLog()) {
			return;
		}
		logTasks.add(msg);
	}
	
	public void mc2discord(EntityPlayerMP player, String msg) {
		this.dispatcher.sendToChannel(this, discordChat, msg, player);
	}
	
	public void discord2mc(IMessage chat) {
		String msg = chat.getFormattedContent();
		if(msg.startsWith("/")) {
			if(msg.startsWith("/help")) {
				String say = "/help, /list (or /online)";
				dispatcherTasks.add(0, say);
			} else if(msg.startsWith("/list") || msg.startsWith("/online")) {
				String say = MinecraftServer.getServer().getCurrentPlayerCount() + " players: " + FCommand.joinNiceString(MinecraftServer.getServer().getAllUsernames()) + " are online.";
				dispatcherTasks.add(0, say);
			} else {
				dispatcherTasks.add(0, "That is not a recognized command. Type _/help_ for the Discord chat commands.");
			}
		} else {
			this.queueChat(chat);
		}
	}
	
	public static class ChatListener {
		
		@EventSubscriber
		public void handle(MessageReceivedEvent event) { try {
			FDiscordBot.getInstance().discord2mc(event.getMessage());
		} catch(Exception exception) { Misc.report(exception); }}
		
	}
	
}
