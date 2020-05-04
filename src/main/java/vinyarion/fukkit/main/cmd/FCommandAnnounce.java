package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FServer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;

public class FCommandAnnounce extends FCommand {
	
	public FCommandAnnounce(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <message> (messages support scolor codes, prefixed by '&')";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 1, "Not enough args!");
		String msg = rest(args, 0);
		doAnnounce(msg);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}

	public static void doAnnounce(String msg) {
		MinecraftServer.getServer().addChatMessage(new ChatComponentText(msg));
		ChatComponentText send = Colors.CHAT(msg);
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
			((EntityPlayerMP)o).addChatMessage(send);
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasLog())
			FDiscordBot.getInstance().queueLog(msg);
	}
	
}
