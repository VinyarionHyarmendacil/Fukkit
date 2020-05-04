package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribEffectGeneric extends FAttribute<String[]> {
	
	public FAttribEffectGeneric(String hexid, String name, String desc) {
		super(hexid, name, desc);
		type = AttributeType.SPECIAL;
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, name);
		String[] cmds = FCommand.rest(params, 0).split(";");
		NBTTagList list = new NBTTagList();
		tag.setTag("effects", list);
		for(String cmd : cmds) {
			list.appendTag(new NBTTagString(cmd));
		}
		this.setOwnedLine(stack, "&fGrants " + list.tagCount() + " effects");
		return 0;
	}
	
	public String[] update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag(name);
		NBTTagList list = tag.getTagList("effects", NBT.STRING);
		String[] cmds = new String[list.tagCount()];
		for(int i = 0; i < list.tagCount(); i++) {
			cmds[i] = list.getStringTagAt(i);
		}
		return cmds;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag(name);
		this.removeOwnedLine(stack);
	}
	
}
