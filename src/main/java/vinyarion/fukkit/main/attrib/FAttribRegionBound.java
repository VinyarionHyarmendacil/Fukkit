package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribRegionBound extends FAttribute<String> {
	
	public FAttribRegionBound(String hexid, String name, String desc) {
		super(hexid, name, desc);
		type = AttributeType.SPECIAL;
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, name);
		tag.setString("region", params[0]);
		this.setOwnedLine(stack, "&3Bound to a region");
		return 0;
	}
	
	public String update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag(name);
		String region = tag.getString("region");
		return region;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag(name);
		this.removeOwnedLine(stack);
	}
	
}
