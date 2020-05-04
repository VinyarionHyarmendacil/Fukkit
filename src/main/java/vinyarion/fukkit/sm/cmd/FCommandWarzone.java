package vinyarion.fukkit.sm.cmd;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.cmd.FCommands;
import vinyarion.fukkit.main.data.FRecipes;
import vinyarion.fukkit.main.data.FRegions;
import vinyarion.fukkit.main.game.FRegion;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandWarzone extends FCommand {
	
	public FCommandWarzone(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <regionName> <radius>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough arguments!");
		String op = args[0];
		String op2 = args[1];
		FCommands.fukkit_regioncyl.processCommand(sender, 
			new String[]{
				"add", 
				op, 
				String.valueOf(sender.getPlayerCoordinates().posX), 
				String.valueOf(sender.getPlayerCoordinates().posZ), 
				op2, 
				String.valueOf(sender.getEntityWorld() != null ? sender.getEntityWorld().provider.dimensionId : 0), 
				String.valueOf(FRegion.ALL_BITS)});
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}

	private static List<String> safe = new ArrayList<String>();
	private static List<String> war = new ArrayList<String>();
	
	public static boolean isSafe(EntityPlayerMP player) {
		return isSafe(player.getCommandSenderName());
	}
	
	public static boolean isSafe(String name) {
		return safe.contains(name) && !war.contains(name);
	}
	
	public static boolean isWar(EntityPlayerMP player) {
		return isWar(player.getCommandSenderName());
	}
	
	public static boolean isWar(String name) {
		return war.contains(name);
	}
	
	public static void set(EntityPlayerMP player, boolean on) {
		String name = player.getCommandSenderName();
		if(safe.contains(name)) {
			safe.remove(name);
			if(isWar(player)) {
				Info(player, "You disabled pvp, but you are in a warzone, so it won't affect you right now!");
			} else {
				Info(player, "You can now be attacked!");
			}
		} else {
			safe.add(name);
			if(isWar(player)) {
				Info(player, "You enabled pvp, but you are in a warzone, so it won't affect you right now!");
			} else {
				Info(player, "You are no longer in PvP!");
			}
		}
	}
	
	public static void warzone(EntityPlayerMP player, boolean in) {
		String name = player.getCommandSenderName();
		if(war.contains(name) && !in) {
			war.remove(name);
			Info(player, "You left the warzone.");
		} else if(!war.contains(name) && in) {
			war.add(name);
			Info(player, "You entered a warzone, so you must PvP!");
		}
	}
	
}
