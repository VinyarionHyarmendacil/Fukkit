package vinyarion.fukkit.main.chestguis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerChestHijacked extends ContainerChest {

	InventoryChestHijacked ich;
	EntityPlayer player;

	public ContainerChestHijacked(EntityPlayer player, InventoryChestHijacked chest) {
		super(player.inventory, chest);
		this.ich = chest;
		this.player = player;
		this.ich.gui.addContainer(this);
	}

	/** If (button==2&&shift==3), then it's a middle click
	 * @param slot the slot
	 * @param button 0 = left, 1 = right
	 * @param shift 0 = up, 1 = down
	 */
	@Override
	public ItemStack slotClick(int slot, int button, int shift, EntityPlayer player) {
		this.ich.gui.onSlotClicked(player, slot, button == 0, button == 1, (button == 2 && shift == 3), shift == 1);
		return null;
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int i1, int i2, boolean flag) {
		return false;
	}

	@Override
	public boolean canDragIntoSlot(Slot slot) {
		return false;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		this.ich.gui.removeContainer(this);
		super.onContainerClosed(player);
	}

}
