package vinyarion.fukkit.main.chestguis;

import java.util.function.BiFunction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.util.memory.RTC.Failable;

public class CGButtonAction implements CGButton {

	public CGButtonAction(ItemStack display) {
		this((player, gui) -> display);
	}

	public CGButtonAction(BiFunction<EntityPlayer, ChestGui, ItemStack> bifunction) {
		this.bifunction = bifunction;
	}

	private final BiFunction<EntityPlayer, ChestGui, ItemStack> bifunction;
	private Action action = null;

	public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
		if(this.action != null) {
			this.action.onSlotClicked(new ActionState(player, gui, slot, left, right, middle, isShift));
		}
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		return this.bifunction.apply(player, gui);
	}

	public CGButtonAction with(Action action) {
		this.action = action;
		return this;
	}

	public static interface Action {
		public void onSlotClicked(ActionState state);
	}

	public static class ActionState {
		public final EntityPlayer player;
		public final ChestGui gui;
		public final int slot;
		public final boolean left, right, middle, isShift;
		private ActionState(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
			this.player = player;
			this.gui = gui;
			this.slot = slot;
			this.left = left;
			this.right = right;
			this.middle = middle;
			this.isShift = isShift;
		}
	}

}
