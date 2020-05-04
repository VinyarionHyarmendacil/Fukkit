package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import vinyarion.fukkit.main.util.nbt.NBT;

public abstract class FRingAttribBase<T> extends FAttribute<T> {
	
	public FRingAttribBase(String hexid, String name, String desc) {
		super(hexid, name, desc);
		type = AttributeType.RING;
	}
	
	public boolean isOn(ItemStack stack) {
		if(stack != null && stack.hasTagCompound() 
				&& stack.getTagCompound().hasKey("RingAttributes", NBT.COMPOUND)
				&& stack.getTagCompound().getCompoundTag("RingAttributes").hasKey(name)) {
			return true;
		}
		return false;
	}
	
	public abstract int addTo(ItemStack stack, String... params);
	
	public abstract T update(EntityPlayer player, ItemStack stack);
	
	public abstract void removeFrom(ItemStack stack);
	
	public static ItemStack getRingAttributeStack(EntityPlayer player, FRingAttribBase attribute) {
		if(attribute == null) {
			return null;
		}
		for(int i = 0; i < 9; i++) {
			ItemStack bar = player.inventory.getStackInSlot(i);
			if(FAttributes.ringWorn.isOn(bar)) {
				if((FAttributes.ringWorn == attribute) || (FAttributes.ringWorn.isActive(player, bar) && attribute.isOn(bar))) {
					return bar;
				}
			}
		}
		return null;
	}
	
}
