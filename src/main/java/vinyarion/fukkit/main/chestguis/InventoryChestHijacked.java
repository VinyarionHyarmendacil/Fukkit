package vinyarion.fukkit.main.chestguis;

import lotr.common.LOTRMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;

public class InventoryChestHijacked implements IInventory {
	
	public static final String NextFukkitGui = "NextFukkitGui";
	public static final String REGEX = "\u00ac"; // ¬
	
	public static void displayTo(EntityPlayerMP player, ChestGui cg) {
		InventoryChestHijacked inv = new InventoryChestHijacked(cg);
		if(player.openContainer != player.inventoryContainer) {
			player.closeScreen();
        }
		player.getNextWindowId();
		player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, inv.getInventoryName(), inv.getSizeInventory(), inv.hasCustomInventoryName()));
		player.openContainer = new ContainerChestHijacked(player, inv);
		player.openContainer.windowId = player.currentWindowId;
		player.openContainer.addCraftingToCrafters(player);
	}
	
	public static void displayTo(EntityPlayerMP player, String id) {
		FPlayerData.forPlayer(player).tagVolatile().setString(InventoryChestHijacked.NextFukkitGui, id);
	}
	
	public ChestGui gui;
	ItemStack[] stacks;
	
	public InventoryChestHijacked(ChestGui cg) {
		this.gui = cg;
		this.stacks = new ItemStack[this.getSizeInventory()];
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
