package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribArmorBreak extends FAttribute<Integer> {

	public FAttribArmorBreak(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, "ArmorBreak");
		int level = Integer.parseInt(params[0]);
		tag.setInteger("level", level);
		this.setOwnedLine(stack, "&8Armorbreak lvl. " + level);
		return 0;
	}
	
	public Integer update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag("ArmorBreak");
		int level = tag.getInteger("level");
		this.setOwnedLine(stack, "&8Armorbreak lvl. " + level);
		return level;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag("ArmorBreak");
		this.removeOwnedLine(stack);
	}
	
}
