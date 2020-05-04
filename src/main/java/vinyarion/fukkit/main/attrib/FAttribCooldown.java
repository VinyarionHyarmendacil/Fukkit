package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribCooldown extends FAttribute<Boolean> {
	
	public FAttribCooldown(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, "Cooldown");
		String ticks = params[0];
		tag.setInteger("ticks", Integer.parseInt(ticks));
		this.setOwnedLine(stack, "Cooldown: " + ticks + " ticks");
		return 0;
	}
	
	public Boolean update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag("Cooldown");
		int now = player.ticksExisted;
		int cool = tag.getInteger("ticks");
		int last = tag.getInteger("last");
		this.setOwnedLine(stack, "Cooldown: " + cool + " ticks");
		if(now < last || last + cool < now) {
			tag.setInteger("last", now);
			return false;
		} else {
			return true;
		}
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag("Cooldown");
		this.removeOwnedLine(stack);
	}
	
}
