package vinyarion.fukkit.rpg.chestguis;

import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.chestguis.CGButton;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.ChestGuiPerm;

public class PouchGuiPerm extends ChestGuiPerm {

	public PouchGuiPerm(NBTTagCompound data, String[] items, String[] commands) {
		super(data, items, commands);
		this.setPouch();
	}

	public PouchGuiPerm(ChestGui parent, String display, CGButton... slots) {
		super(parent, display, 3, slots);
		this.setPouch();
	}

	public PouchGuiPerm(String display, String parent, CGButton... slots) {
		super(display, parent, 3, slots);
		this.setPouch();
	}

	public PouchGuiPerm(String display, CGButton... slots) {
		super(display, 3, slots);
		this.setPouch();
	}

}
