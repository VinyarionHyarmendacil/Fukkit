package vinyarion.fukkit.main.chestguis;

import lotr.common.LOTRMod;
import lotr.common.inventory.LOTRContainerPouch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPouchHijacked extends LOTRContainerPouch {

	InventoryPouchHijacked ich;
	EntityPlayer player;
	ItemStack former;

	public ContainerPouchHijacked(EntityPlayer player) {
		super(player);
		this.player = player;
	}

	public void init() {
		this.ich.gui.addContainer(this);
	}

	@Override
	public void renamePouch(String name) {
		super.renamePouch(name);
		ich.name = name;
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
		System.err.println(this.ich);
		System.err.println(this.ich.gui);
		
		this.ich.gui.removeContainer(this);
		int hh = player.inventory.currentItem;
		player.inventory.setInventorySlotContents(hh, this.former);
		super.onContainerClosed(player);
	}

}
