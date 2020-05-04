package vinyarion.fukkit.main.cmd;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import lotr.common.item.LOTRItemCoin;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.data.FItems;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FScripts;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.script.FScript;
import vinyarion.fukkit.main.util.Colors;

public class FCommandExchange extends FCommand {

	public FCommandExchange(String phiName) {
		super(phiName);
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <player> <scriptToExecute|`commandToRun`> <money|item|customitem|empty> [<amount|{valid nbt tag}>] [<customItemName>]";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		args = reparse(args);
		Assert(args.length >= 3, "Not enough args!");
		EntityPlayerMP player = getPlayer(sender, args[0]);
		Assert(player != null, "Must specify a player!");
		FScript sc = FScripts.instance().get(args[1]);
		Assert(sc != null, "That is not a script!");
		String type = args[2];
		if(type.equals("money")) {
			Assert(args.length >= 4, "Not enough args!");
			int coins = parseInt(sender, args[3]);
			int has = LOTRItemCoin.getInventoryValue(player);
			if(coins > has) {
				Info(player, "You do not have enough money to do this!");
				Info(sender, args[0] + " does not have enough money for script " + args[1]);
			} else {
				LOTRItemCoin.takeCoins(coins, player);
				sc.command(player);
			}
		} else if(type.equals("item")) {
			Assert(args.length >= 4, "Not enough args!");
			NBTTagCompound required = null;
			try {
				required = (NBTTagCompound)JsonToNBT.func_150315_a(rest(args, 3));
			} catch (NBTException e) {
				Error("Invalid NBT given!");
			}
			ItemStack needed = ItemStack.loadItemStackFromNBT(required);
			int amount = needed.stackSize;
			List<Integer> slots = Lists.newArrayList();
			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack slot = player.inventory.getStackInSlot(i);
				if(slot != null ? (needed.isItemEqual(slot) && ItemStack.areItemStackTagsEqual(needed, slot)) : false) {
					if(amount <= slot.stackSize) {
						player.inventory.decrStackSize(i, amount);
						amount = 0;
						break;
					} else {
						amount -= slot.stackSize;
						slots.add(i);
					}
				}
			}
			if(amount <= 0) {
				for(int i : slots) {
					player.inventory.setInventorySlotContents(i, null);
				}
				sc.command(player);
			} else {
				Info(player, "You do not have enough items to do this!");
				Info(sender, args[0] + " does not have enough items for script " + args[1]);
			}
		} else if(type.equals("customitem")) {
			Assert(args.length >= 5, "Not enough args!");
			FItem custom = FItems.instance().get(args[4]);
			int amount = parseInt(sender, args[3]);
			List<Integer> slots = Lists.newArrayList();
			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack slot = player.inventory.getStackInSlot(i);
				if(slot != null ? custom.equals(slot) : false) {
					if(amount <= slot.stackSize) {
						player.inventory.decrStackSize(i, amount);
						amount = 0;
						break;
					} else {
						amount -= slot.stackSize;
						slots.add(i);
					}
				}
			}
			if(amount <= 0) {
				for(int i : slots) {
					player.inventory.setInventorySlotContents(i, null);
				}
				sc.command(player);
			} else {
				Info(player, "You do not have enough items to do this!");
				Info(sender, args[0] + " does not have enough items for script " + args[1]);
			}
		} else if(type.equals("empty")) {
			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack slot = player.inventory.getStackInSlot(i);
				if(slot != null) {
					Info(player, "You have items in your inventory!");
					Error(args[0] + " has items, script " + args[1] + " requires empty");
				}
			}
			sc.command(player);
		} else Invalid(type);
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? FScripts.instance().names() : 
			args.length == 3 ? new String[]{"money", "item", "customitem", "empty"} : 
			null;
	}

}
