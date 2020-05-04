package vinyarion.fukkit.main.chestguis;

import java.util.function.BiFunction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CGButtonInfo implements CGButton {

	public CGButtonInfo(ItemStack display) {
		this.bifunction = (player, gui) -> display;
	}

	public CGButtonInfo(BiFunction<EntityPlayer, ChestGui, ItemStack> bifunction) {
		this.bifunction = bifunction;
	}

	private final BiFunction<EntityPlayer, ChestGui, ItemStack> bifunction;

	public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
		// Nothing
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		return this.bifunction.apply(player, gui);
	}

}
