package vinyarion.fukkit.main.chestguis;

import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public abstract class CGButtonSelect implements CGButton {

	public CGButtonSelect(String name, Function<EntityPlayer, ItemStack> display) {
		this.name = name;
		this.display = display;
	}

	public CGButtonSelect(String name, ItemStack display) {
		this.name = name;
		this.display = p -> display;
	}

	protected final String name;
	private Function<EntityPlayer, ItemStack> display;

	public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
		if(this.isSelected(player, gui)) {
			if(this.canDeselect(player, gui)) {
				this.onDeselect(player, gui);
			}
		} else {
			if(this.canSelect(player, gui)) {
				this.onSelect(player, gui);
			}
		}
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		return this.display.apply(player);
	}

	public abstract boolean isSelected(EntityPlayer player, ChestGui gui);

	public abstract boolean canSelect(EntityPlayer player, ChestGui gui);

	public abstract boolean canDeselect(EntityPlayer player, ChestGui gui);

	public abstract void onSelect(EntityPlayer player, ChestGui gui);

	public abstract void onDeselect(EntityPlayer player, ChestGui gui);

}
