package vinyarion.fukkit.main.chestguis;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.Colors;

public class ChestGui {

	public ChestGui parent = null;

	public String id = null;
	public String name;
	public int lines;
	public CGButton[] buttons;

	public ChestGui(NBTTagCompound data, String[] items, String[] commands) {
		lines = data.getInteger("Lines");
		name = data.getString("Name");
		buttons = new CGButton[lines * 9];
		ItemStack[] inv = new ItemStack[lines * 9];
		for(int i = 0; (i < items.length && i < lines * 9); i++) {
			try {
				inv[i] = items[i].length() > 0 ? ItemStack.loadItemStackFromNBT((NBTTagCompound)JsonToNBT.func_150315_a(items[i])) : null;
			} catch(Exception e) { }
		}
		for(int i = 0; i < lines * 9; i++) {
			CGButtonCommand nb = new CGButtonCommand();
			buttons[i] = nb;
			nb.with(i < commands.length ? commands[i].split(InventoryChestHijacked.REGEX) : new String[0]);
			try {
				nb.with(i < items.length ? ItemStack.loadItemStackFromNBT((NBTTagCompound)JsonToNBT.func_150315_a(items[i])) : null);
			} catch (NBTException e) {
				nb.with((ItemStack)null);
			}
			buttons[i].onAdded(this);
		}
	}

	public ChestGui(ChestGui par, String display, int rows, CGButton... slots) {
		parent = par;
		lines = rows;
		name = Colors.color(display);
		buttons = new CGButton[lines * 9];
		for(int i = 0; i < lines * 9; i++) {
			if(i > slots.length ? true : slots[i] == null) {
				buttons[i] = new CGButtonEmpty();
			} else {
				buttons[i] = slots[i];
			}
			buttons[i].onAdded(this);
		}
	}

	public void setPouch() {
		this.isPouch = true;
	}
	private boolean isPouch = false;
	public final boolean isPouch() {
		return this.isPouch;
	}
	public String defaultText = null;
	public ChestGui setDefaultText(String defaultText) {
		this.defaultText = defaultText;
		return this;
	}

	public String getPouchText(EntityPlayer player) {
		Container c = player.openContainer;
		if(c != null && c instanceof ContainerPouchHijacked) {
			return ((ContainerPouchHijacked)c).ich.name;
		}
		return null;
	}

	public void displayTo(EntityPlayerMP player) {
		InventoryChestHijacked.displayTo(player, this.id);
	}

	public void displayParentTo(EntityPlayerMP player) {
		if(this.parent != null) {
			InventoryChestHijacked.displayTo(player, this.parent.id);
		} else {
			player.closeScreen();
		}
	}

	private List<ContainerChestHijacked> containers = Lists.newArrayList();

	public void removeContainer(ContainerChestHijacked container) {
		containers.remove(container);
	}

	public void addContainer(ContainerChestHijacked container) {
		containers.add(container);
		refresh(container);
	}

	private List<ContainerPouchHijacked> pcontainers = Lists.newArrayList();

	public void removeContainer(ContainerPouchHijacked container) {
		pcontainers.remove(container);
	}

	public void addContainer(ContainerPouchHijacked container) {
		pcontainers.add(container);
		refresh(container);
	}

	public void refresh(EntityPlayer player) {
		if(player.openContainer != null && player.openContainer instanceof ContainerChestHijacked) {
			refresh((ContainerChestHijacked)player.openContainer);
		}
	}

	public void refresh(ContainerChestHijacked container) {
		for(int i = 0; i < Math.min(lines * 9, container.ich.getSizeInventory()); i++) {
			container.ich.stacks[i] = container.ich.gui.buttons[i].display(container.player, container.ich.gui);
		}
	}

	public void refresh(ContainerPouchHijacked container) {
		for(int i = 0; i < Math.min(lines * 9, container.ich.getSizeInventory()); i++) {
			container.ich.stacks[i] = container.ich.gui.buttons[i].display(container.player, container.ich.gui);
		}
	}

	public void onSlotClicked(EntityPlayer player, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
		if(slot >= 0 && slot < this.buttons.length) try {
			this.buttons[slot].onSlotClicked(player, this, slot, left, right, middle, isShift);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
