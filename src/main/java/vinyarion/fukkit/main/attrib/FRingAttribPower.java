package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FRingAttribPower extends FRingAttribBase<String[]> {
	
	public FRingAttribPower(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound mytag = NBT.ensure(stack, "RingPowers");
		NBTTagList list = NBT.ensureList(mytag, name);
		for(String s : params) {
			list.appendTag(new NBTTagString(s));
		}
		this.setOwnedLine(stack, "&5Has " + params.length + " miscellaneous powers");
		return 0;
	}
	
	public String[] update(EntityPlayer player, ItemStack stack) {
		return NBT.toArrayString(stack.getTagCompound().getCompoundTag("RingPowers").getTagList(name, NBT.STRING));
	}
	
	public void removeFrom(ItemStack stack) {
		this.removeOwnedLine(stack);
		stack.getTagCompound().getCompoundTag("RingPowers").removeTag(name);
	}
	
}
