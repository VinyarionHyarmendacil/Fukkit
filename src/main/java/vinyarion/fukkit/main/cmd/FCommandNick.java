package vinyarion.fukkit.main.cmd;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandNick extends FCommand {
	
	public static final String CHAT_TAGS = "chat_tags";
	
	public FCommandNick(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <nick> [<player>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 1, "Not enough args!");
		EntityPlayerMP player;
		if(args.length == 1) {
			Assert(sender instanceof EntityPlayerMP, "Must be a player to nick yourself!");
			player = (EntityPlayerMP)sender;
		} else {
			if(sender instanceof EntityPlayerMP) {
				Assert(FPermissions.instance().hasPermission((EntityPlayerMP)sender, FPermissions.Others.perm_NickOthers), "You do not have permission to nickname another player!");
			}
			player = playerFor(sender, args[1]);
		}
		Assert(player != null, "Must be a player, who must also be online!");
		NBTTagCompound tags = NBT.ensure(FPlayerData.forPlayer(player.getCommandSenderName()).tag(), CHAT_TAGS);
		String op1 = args[0].replace("~", " ");
		tags.setString("nick", FCommandTitles.colorIfPerm(player, op1));
		Info(sender, "Nicked " + (player.getCommandSenderName().equals(sender.getCommandSenderName()) ? "you" : player.getCommandSenderName()) + " " + op1 + ".");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
	public static IChatComponent name(EntityPlayerMP player) {
		String name = player.getDisplayName();
		NBTTagCompound tags = NBT.ensure(FPlayerData.forPlayer(player.getCommandSenderName()).tag(), CHAT_TAGS);
		if(tags.hasKey("nick", NBT.STRING)) {
			name = tags.getString("nick");
		}
	    ChatComponentText chatcomponenttext = FMod.config.ditchScoreboardColors ? new ChatComponentText(name) : new ChatComponentText(ScorePlayerTeam.formatPlayerName(player.getTeam(), name));
	    chatcomponenttext.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getCommandSenderName() + " "));
	    chatcomponenttext.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Colors.MAKE("&l" + player.getCommandSenderName() + " &7&oClick to /msg")));
	    return chatcomponenttext;
	}
	
}
