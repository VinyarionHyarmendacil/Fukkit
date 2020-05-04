package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribMace extends FAttribute<Double> {

	public FAttribMace(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public boolean isOn(ItemStack stack) {
		return this.isOnLore(stack);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, name);
		String lvl = "";
		tag.setDouble("level", ((double)Integer.parseInt(lvl = (params.length == 0 ? "1" : params[0])) / 3D + 1D));
		this.setOwnedLine(stack, "&fMace lvl. " + lvl);
		return 0;
	}
	
	public Double update(EntityPlayer player, ItemStack stack) {
		return stack.getTagCompound().getCompoundTag(name).getDouble("level");
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag(name);
		this.removeOwnedLine(stack);
	}

}
