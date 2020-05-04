package vinyarion.fukkit.main.chestguis;

import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.chestguis.CGButtonBack.Exit;
import vinyarion.fukkit.main.data.FChestGuis;
import vinyarion.fukkit.main.recipes.Permenant;

public class ChestGuiPerm extends ChestGui implements Permenant {

	public ChestGuiPerm(NBTTagCompound data, String[] items, String[] commands) {
		super(data, items, commands);
	}

	public ChestGuiPerm(ChestGui parent, String display, int rows, CGButton... slots) {
		super(parent, display, rows, slots);
	}

	public ChestGuiPerm(String display, String parent, int rows, CGButton... slots) {
		super(FChestGuis.instance().get(parent), display, rows, slots);
	}

	public ChestGuiPerm(String display, int rows, CGButton... slots) {
		super(null, display, rows, slots);
	}

}
