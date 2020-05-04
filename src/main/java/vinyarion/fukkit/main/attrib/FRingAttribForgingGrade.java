package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.forging.rings.Grade;

public class FRingAttribForgingGrade extends FAttribute<Grade> {
	
	public FRingAttribForgingGrade(String hexid, String name, String desc) {
		super(hexid, name, desc);
		type = AttributeType.SPECIAL;
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, name);
		Grade grade = Misc.isInt(params[0]) ? Grade.of(Integer.parseInt(params[0])) : Grade.valueOf(params[0]);
		tag.setInteger("recipe", grade.index);
		this.setOwnedLine(stack, "&fForging grade: " + grade.name);
		return 0;
	}
	
	public Grade update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag(name);
		int grade = tag.getInteger("recipe");
		return Grade.of(grade);
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag(name);
		this.removeOwnedLine(stack);
	}
	
}
