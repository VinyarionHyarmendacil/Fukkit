package vinyarion.fukkit.main.cmd;

import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.tileentity.LOTRTileEntityChest;
import lotr.common.tileentity.LOTRTileEntitySign;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MovingObjectPosition;
import vinyarion.fukkit.main.data.FLootTables;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.FieldHandle;
import vinyarion.fukkit.main.util.Misc;

public class FCommandLoot extends FCommand {
	
	public FCommandLoot(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|remove|addhere|removehere> <lootTableName> [<x> <y> <z>] [<reward|cooldown(hh:mm:ss)> <minItems> <maxItems>]";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		String op = args[0];
		String op2 = args[1];
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		if(op.equals("add")) {
			Assert(args.length >= 8, "Not enough args!");
			int x = parseInt(sender, args[2]);
			int y = parseInt(sender, args[3]);
			int z = parseInt(sender, args[4]);
			String op6 = args[5];
			String cooldown = op6.equals("reward") ? "9999:00:00" : op6;
			int min = parseInt(sender, args[6]);
			int max = parseInt(sender, args[7]);
			TileEntity te = player.worldObj.getTileEntity(x, y, z);
			IInventory chest = getChestOrThrow(te);
			if(FLootTables.instance().addLoot(op2, chest, cooldown, Math.min(min, max), Math.max(min, max))) {
				Info(sender, "Overwrote loot table " + op2);
			} else {
				Info(sender, "Added loot table " + op2);
			}
		} else if(op.equals("remove")) {
			if(FLootTables.instance().removeLoot(op2)) {
				Info(sender, "Removed loot table " + op2);
			} else {
				Info(sender, "Loot table " + op2 + " does not exist!");
			}
		} else if(op.equals("addhere")) {
			Assert(args.length >= 5, "Not enough args!");
			int x = parseInt(sender, args[2]);
			int y = parseInt(sender, args[3]);
			int z = parseInt(sender, args[4]);
			TileEntity te = player.worldObj.getTileEntity(x, y, z);
			IInventory chest = getChestOrThrow(te);
			if(FLootTables.instance().addData(op2, te)) {
				Info(sender, "Overwrote loot chest " + op2 + " at " + x + ", " + y + ", " + z);
			} else {
				Info(sender, "Added loot chest " + op2 + " at " + x + ", " + y + ", " + z);
			}
		} else if(op.equals("removehere")) {
			Assert(args.length >= 5, "Not enough args!");
			int x = parseInt(sender, args[2]);
			int y = parseInt(sender, args[3]);
			int z = parseInt(sender, args[4]);
			TileEntity te = player.worldObj.getTileEntity(x, y, z);
			IInventory chest = getChestOrThrow(te);
			if(FLootTables.instance().removeData(te)) {
				Info(sender, "Removed loot chest " + op2 + " at " + x + ", " + y + ", " + z);
			} else {
				Info(sender, "No such loot chest exists!");
			}
		} else Invalid(op);
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "remove", "addhere", "removehere"} : 
			args.length == 2 ? FLootTables.instance().tables() : 
			args.length == 3 ? new String[]{String.valueOf(getLookPos(sender)[0])} : 
			args.length == 4 ? new String[]{String.valueOf(getLookPos(sender)[1])} : 
			args.length == 5 ? new String[]{String.valueOf(getLookPos(sender)[2])} : null;
	}

	public static IInventory getChestOrThrow(TileEntity te) {
		Assert(te != null, "Not looking at a tile entity!");
		Assert(te instanceof IInventory, "Look at a chest, not that. What even is that?");
		return (IInventory)te;
	}

	public static IInventory getChestOrNull(TileEntity te) {
		return getChestApplicable(te) ? (IInventory)te : null;
	}

	public static boolean getChestApplicable(TileEntity te) {
		return te != null && te instanceof IInventory;
	}

	private static final FieldHandle<LOTRTileEntityChest, Integer> numPlayersUsing = FieldHandle.of(ReflectionHelper.findField(LOTRTileEntityChest.class, "numPlayersUsing"));
	public static boolean isCurrentlyUnused(TileEntity te) {
		return te != null ? (
			te instanceof TileEntityChest ? ((TileEntityChest)te).numPlayersUsing == 0 :
			te instanceof LOTRTileEntityChest ? numPlayersUsing.get(((LOTRTileEntityChest)te)).intValue() == 0 :
			false
		) : false;
	}

}
