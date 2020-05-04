package vinyarion.fukkit.main.chestguis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CGButtonEmpty implements CGButton {

	public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
		// Nothing. Duh.
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		ItemStack ret = new ItemStack(Blocks.stained_glass_pane, 1, 8);
		ret.setStackDisplayName("");
		return ret;
//		return null; // TODO : Maybe null? <- No
	}

}
