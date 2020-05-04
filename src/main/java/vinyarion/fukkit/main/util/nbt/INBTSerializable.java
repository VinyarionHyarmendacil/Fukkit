package vinyarion.fukkit.main.util.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTSerializable {
	
	public void fromNBT(NBTTagCompound nbt);
	
	public void toNBT(NBTTagCompound nbt);
	
}
