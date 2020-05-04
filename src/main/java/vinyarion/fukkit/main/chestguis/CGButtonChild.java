package vinyarion.fukkit.main.chestguis;

import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class CGButtonChild implements CGButton {

	public CGButtonChild(String child, Function<EntityPlayer, ItemStack> display) {
		this.child = child;
		this.display = display;
	}

	public CGButtonChild(String child, ItemStack display) {
		this.child = child;
		this.display = p -> display;
	}

	protected String child;
	private Function<EntityPlayer, ItemStack> display;

	public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
		guis.get(this.child).displayTo((EntityPlayerMP)player);
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		return this.display.apply(player);
	}

}
