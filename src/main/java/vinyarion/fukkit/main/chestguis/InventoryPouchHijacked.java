package vinyarion.fukkit.main.chestguis;

import lotr.common.LOTRMod;
import lotr.common.inventory.LOTRContainerPouch;
import lotr.common.inventory.LOTRInventoryPouch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import vinyarion.fukkit.main.game.FPlayer;

public class InventoryPouchHijacked extends LOTRInventoryPouch {

	public ChestGui gui;
	ItemStack[] stacks;
	public String name;
	
	public InventoryPouchHijacked(EntityPlayerMP player, ChestGui cg, ContainerPouchHijacked container) {
		super(player, container);
		this.gui = cg;
		this.stacks = new ItemStack[this.getSizeInventory()];
	}

	public static void displayTo(EntityPlayerMP player, ChestGui gui) {
		if(player.openContainer != player.inventoryContainer) {
			player.closeScreen();
        }
		int hh = player.inventory.currentItem;
		ItemStack hand = player.inventory.getStackInSlot(hh);
		ItemStack pouch = new ItemStack(LOTRMod.pouch, 1, 2);
		savePouchContents(player, pouch, gui);
		if(gui.defaultText != null) {
			pouch.setStackDisplayName(gui.defaultText);
		}
		player.inventory.setInventorySlotContents(hh, pouch);
		FPlayer.resendEntireInventory(player);
		player.openGui(LOTRMod.instance, 15, player.worldObj, 0, 0, 0);
		ContainerPouchHijacked c = new ContainerPouchHijacked(player);
		player.openContainer = c;
		boolean remote = player.worldObj.isRemote;
		player.worldObj.isRemote = true;
		InventoryPouchHijacked inv = new InventoryPouchHijacked(player, gui, c);
		player.worldObj.isRemote = remote;
		if(gui.defaultText != null) {
			inv.name = gui.defaultText;
		}
		c.ich = inv;
		c.former = hand;
		c.init();
		player.openContainer.windowId = player.currentWindowId;
		player.openContainer.addCraftingToCrafters(player);
	}

	private static void savePouchContents(EntityPlayerMP player, ItemStack pouch, ChestGui gui) {
		if(!pouch.hasTagCompound()) {
			pouch.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList items = new NBTTagList();
		for(int i = 0; i < (gui.lines*9); i++) {
			ItemStack itemstack = gui.buttons[i].display(player, gui);
			if(itemstack != null) {
				NBTTagCompound itemData = new NBTTagCompound();
				itemData.setByte("Slot", (byte)i);
				itemstack.writeToNBT(itemData);
				items.appendTag(itemData);
			}
		}
		nbt.setTag("Items", items);
		pouch.getTagCompound().setTag("LOTRPouchData", nbt);
	}

	@Override
	public int getSizeInventory() {
		return this.gui.lines * 9;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.stacks[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return this.getStackInSlot(slot);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) { }

	@Override
	public String getInventoryName() {
		return gui.name;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() { }

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() { }

	@Override
	public void closeInventory() { }

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

}
