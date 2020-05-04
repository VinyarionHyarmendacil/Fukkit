package vinyarion.fukkit.main.chestguis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.util.Colors;

public class CGButtonBack extends CGButtonChild {

	private CGButtonBack(ItemStack stack) {
		super(null, stack);
	}

	public CGButtonBack() {
		super(null, new ItemStack(Items.paper, 1, 0));
	}

	public CGButtonBack(String child) {
		super(child, new ItemStack(Items.paper, 1, 0));
	}

	public void onAdded(ChestGui owner) {
		if(this.child == null) {
			this.child = owner.parent.id;
		}
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		return super.display(player, gui).copy().setStackDisplayName(Colors.RESET + Colors.BOLD + "\u2190 Back"); // Left arrow
	}

	public static class Exit extends CGButtonBack {
		public ItemStack display(EntityPlayer player, ChestGui gui) {
			return super.display(player, gui).copy().setStackDisplayName(Colors.RESET + Colors.BOLD + "\u2190 Exit"); // Left arrow
		}
		public void onAdded(ChestGui owner) {
		}
		public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
			player.closeScreen();
		}
	}

	public static class Decline extends CGButtonBack {
		private final String display;
		public Decline(String display) {
			super(new ItemStack(Items.paper, 1, 0));
			this.display = display;
		}
		public ItemStack display(EntityPlayer player, ChestGui gui) {
			return super.display(player, gui).copy().setStackDisplayName(Colors.RESET + Colors.BOLD + "\u2190 " + this.display); // Left arrow
		}
	}

}
