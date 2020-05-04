package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FRingAttribWorn extends FRingAttribBase<Boolean> {
	
	public FRingAttribWorn(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBT.ensure(stack, "RingAttributes").setBoolean(name, false);
		return 0;
	}
	
	public Boolean update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound mytag = NBT.ensure(stack, "RingAttributes");
		NBTTagCompound tag = stack.getTagCompound();
		boolean now = !mytag.getBoolean(name);
		mytag.setBoolean(name, now);
		if(now) {
			if(tag.hasKey("remap_ench")) {
				tag.setTag("ench", tag.getTag("remap_ench"));
				tag.removeTag("remap_ench");
			} else {
				tag.setTag("ench", new NBTTagList());
			}
			this.setOwnedLine(stack, "&9Currently equipped");
		} else {
			if(tag.hasKey("ench")) {
				tag.setTag("remap_ench", tag.getTag("ench"));
				tag.removeTag("ench");
			}
			this.removeOwnedLine(stack);
		}
		return now;
	}
	
	public boolean isActive(EntityPlayer player, ItemStack stack) {
		return stack.getTagCompound().getCompoundTag("RingAttributes").getBoolean(name);
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().getCompoundTag("RingAttributes").removeTag(name);
		this.removeOwnedLine(stack);
	}
	
}
