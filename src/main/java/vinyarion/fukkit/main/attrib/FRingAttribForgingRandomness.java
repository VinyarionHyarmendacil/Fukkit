package vinyarion.fukkit.main.attrib;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FRingAttribForgingRandomness extends FAttribute<String[]> {
	
	public FRingAttribForgingRandomness(String hexid, String name, String desc) {
		super(hexid, name, desc);
		type = AttributeType.SPECIAL;
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, name);
		tag.setString("value", StringUtils.join(params, ";"));
		this.setOwnedLine(stack, "&fUnknown grade");
		return 0;
	}
	
	public String[] update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag(name);
		String[] params = tag.getString("value").split(";");
		return params;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag(name);
		this.removeOwnedLine(stack);
	}
	
}
