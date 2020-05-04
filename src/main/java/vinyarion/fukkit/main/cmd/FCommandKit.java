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
import net.minecraft.util.IChatComponent;
import vinyarion.fukkit.main.data.FKits;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.game.FKit;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandKit extends FCommand {
	
	public FCommandKit(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <kit>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		Assert(args.length >= 1, "Not enough args!");
		String op = args[0];
		FKit kit = FKits.instance().get(op);
		Assert(kit != null, "Kit " + op + " does not exist!");
		Assert(FPermissions.instance().hasPermission(player, "kit." + kit.name), "You don't have permission to use the " + op + " kit!");
		FPlayerData data = FPlayerData.forPlayer(player.getCommandSenderName());
		NBTTagCompound kk = NBT.ensure(data.tag(), "kit_cooldown");
		if(kk.hasKey(kit.name)) {
			String used = kk.getString(kit.name);
			String cool = kit.cooldown;
			Assert(!Time.hasPassed(used, cool), "This kit is still on a cooldown! " + Time.differenceReadable(used, cool) + " to go.");
		}
		kit.giveTo(player);
		kk.setString(kit.name, Time.instantNow());
		Info(sender, "Recieved kit " + op + ".");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? FKits.instance().kits() : new String[0];
	}
	
}
