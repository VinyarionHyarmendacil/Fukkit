package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribLearnRecipe extends FAttribute<String> {
	
	public FAttribLearnRecipe(String hexid, String name, String desc) {
		super(hexid, name, desc);
		type = AttributeType.SPECIAL;
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, name);
		String recipe = params[0];
		tag.setString("recipe", recipe);
		this.setOwnedLine(stack, "&fLearn recipe " + recipe);
		return 0;
	}
	
	public String update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag(name);
		String recipe = tag.getString("recipe");
		return recipe;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag(name);
		this.removeOwnedLine(stack);
	}
	
}
