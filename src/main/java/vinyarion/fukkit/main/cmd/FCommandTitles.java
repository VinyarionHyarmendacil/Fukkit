package vinyarion.fukkit.main.cmd;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandTitles extends FCommand {
	
	public static final String CHAT_TAGS = "chat_tags";
	
	public FCommandTitles(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <prefix|suffix|!prefix|!suffix> <player> [<value>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		EntityPlayerMP player = playerFor(sender, args[1]);
		Assert(player != null, "Must be a player, who must also be online!");
		NBTTagCompound tags = NBT.ensure(FPlayerData.forPlayer(player.getCommandSenderName()).tag(), CHAT_TAGS);
		if(args[0].equals("prefix")) {
			String pre = Colors.color(rest(args, 2));
			tags.setString("prefix", pre);
			Info(sender, "Added prefix: " + pre);
		} else if(args[0].equals("suffix")) {
			String suf = Colors.color(rest(args, 2));
			tags.setString("suffix", suf);
			Info(sender, "Added suffix: " + suf);
		} else if(args[0].equals("!prefix")) {
			tags.removeTag("prefix");
			Info(sender, "Removed prefix.");
		} else if(args[0].equals("!suffix")) {
			tags.removeTag("suffix");
			Info(sender, "Removed suffix.");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"prefix", "suffix", "!prefix", "!suffix"} : 
			args.length == 2 ? MinecraftServer.getServer().getAllUsernames() : null;
	}
	
	public static IChatComponent getClickableFormattedTitle(EntityPlayerMP player) {
		String prefix = null;
		String suffix = null;
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
		IChatComponent c = FCommandNick.name(player);
		c = (prefix == null) ? c : Colors.make(prefix).appendSibling(c);
		c = (suffix == null) ? c : c.appendSibling(Colors.make(suffix));
		return c;
	}
	
	public static String colorIfPerm(EntityPlayerMP player, String message) {
		return FPermissions.instance().hasPermission(player, FPermissions.Others.perm_ColorChat) ? Colors.color(message) : message;
	}
	
}
