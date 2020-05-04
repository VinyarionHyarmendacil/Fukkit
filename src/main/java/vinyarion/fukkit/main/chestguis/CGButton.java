package vinyarion.fukkit.main.chestguis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.data.FChestGuis;

public interface CGButton {

	static final FChestGuis guis = FChestGuis.instance();

	public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift);

	public ItemStack display(EntityPlayer player, ChestGui gui);

	default void onAdded(ChestGui gui) {}

}
