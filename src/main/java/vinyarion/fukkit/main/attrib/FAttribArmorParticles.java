package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribArmorParticles extends FAttribute<String> {

	public FAttribArmorParticles(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, "ArmorParticles");
		String type = params[0];
		tag.setString("type", type);
		//this.setOwnedLine(stack, "&8ArmorParticles  " + level);
		return 0;
	}
	
	public String update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag("ArmorParticles");
		String type = tag.getString("type");
		//this.setOwnedLine(stack, "&8ArmorParticles  " + level);
		return type;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag("ArmorParticles");
		//this.removeOwnedLine(stack);
	}
	
}
