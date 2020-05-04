package vinyarion.fukkit.sm.cmd;

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
import net.minecraft.util.IChatComponent;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.cmd.FCommandNick;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandRealname extends FCommand {
	
	public FCommandRealname(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <nick>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 1, "Not enough args!");
		String op = Colors.uncolor(args[0]).replace("~", " ");
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP player = (EntityPlayerMP) o;
			NBTTagCompound tags = NBT.ensure(FPlayerData.forPlayer(player.getCommandSenderName()).tag(), FCommandNick.CHAT_TAGS);
			String nick = tags.hasKey("nick", NBT.STRING) ? tags.getString("nick") : null;
			if(nick == null) {
				continue;
			}
			if(op.equals(Colors.uncolor(nick))) {
				Info(sender, "Nick " + op + " is player " + player.getCommandSenderName() + ".");
				return;
			}
		}
		Info(sender, "Didn't find anyone with that nickname.");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
