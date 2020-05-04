package vinyarion.fukkit.main.cmd;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.data.FSeenData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Time;

public class FCommandSeen extends FCommand {
	
	public FCommandSeen(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender args) {
		return "/" + getCommandName() + " <name>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length > 0, "Must specify a player name!");
		NBTTagCompound tag = FSeenData.instance().getFor(args[0]);
		try {
			String seen = "Last logout " + Time.readable(Time.since(tag.getString(FSeenData.TIME))) + " ago, " + (tag.getBoolean(FSeenData.BANNED) ? "and is banned." : "not banned.");
			Info(sender, seen);
		} catch(Exception e) {
			Info(sender, "That name has not logged on! Maybe the player changed it, they have never joined, or you misspelt it?");
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}
	
}
