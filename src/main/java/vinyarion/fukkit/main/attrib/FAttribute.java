package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public abstract class FAttribute<T> {
	
	public AttributeType type = AttributeType.STANDARD;
	
	public final String id;
	public final String name;
	public final String desc;
	
	public FAttribute(String hexid, String name, String desc) {
		this.id = Colors.RESET + Colors.WHITE + Colors.RESET + toHexIdString(hexid) + Colors.RESET;
		this.name = name;
		this.desc = desc;
	}
	
	public boolean isOn(ItemStack stack) {
		return stack != null
			&& stack.hasTagCompound() 
			&& stack.getTagCompound().hasKey(name, NBT.COMPOUND);
	}
	
	public boolean isOnLore(ItemStack stack) {
		if(stack != null 
				&& stack.hasTagCompound() 
				&& stack.getTagCompound().hasKey("display", NBT.COMPOUND) 
				&& stack.getTagCompound().getCompoundTag("display").hasKey("Lore", NBT.LIST)) {
			NBTTagList list = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", NBT.STRING);
			for(int i = 0; i < list.tagCount(); i++) {
				if(list.getStringTagAt(i).startsWith(this.id)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public abstract int addTo(ItemStack stack, String... params);
	
	public int addTo(ItemStack stack) {
		return addTo(stack, new String[]{"1"});
	}
	
	public abstract T update(EntityPlayer player, ItemStack stack);
	
	public abstract void removeFrom(ItemStack stack);
	
	public void setOwnedLine(ItemStack stack, String string) {
		String toput = id + Colors.color(string);
		NBTTagCompound disp = NBT.ensure(stack, "display");
		NBTTagList lore = NBT.ensureList(disp, "Lore");
		boolean flag = true;
		for(int i = 0; i < lore.tagCount(); i++) {
			String line = lore.getStringTagAt(i);
			if(line.startsWith(id)) {
				lore.func_150304_a(i, new NBTTagString(toput));
				flag = false;
				break;
			}
		}
		if(flag) {
			lore.appendTag(new NBTTagString(toput));
		}
	}
	
	public void setOwnedLineAlternates(ItemStack stack, String string, String alternate) {
		String idalt = id + toHexIdString(alternate) + Colors.RESET;
		String toput = idalt + Colors.color(string);
		NBTTagCompound disp = NBT.ensure(stack, "display");
		NBTTagList lore = NBT.ensureList(disp, "Lore");
		boolean flag = true;
		for(int i = 0; i < lore.tagCount(); i++) {
			String line = lore.getStringTagAt(i);
			if(line.startsWith(idalt)) {
				lore.func_150304_a(i, new NBTTagString(toput));
				flag = false;
				break;
			}
		}
		if(flag) {
			lore.appendTag(new NBTTagString(toput));
		}
	}
	
	public void removeOwnedLine(ItemStack stack) {
		NBTTagCompound disp = NBT.ensure(stack.getTagCompound(), "display");
		NBTTagList lore = NBT.ensureList(disp, "Lore");
		for(int i = 0; i < lore.tagCount(); i++) {
			String line = lore.getStringTagAt(i);
			if(line.startsWith(id)) {
				lore.removeTag(i);
			}
		}
	}
	
	public static String toHexIdString(String string) {
		String ids = "";
		char[] ca = string.toCharArray();
		for(int i = 0; i < ca.length; i++) {
			ids = ids + Colors.SECTION + ca[i];
		}
		return ids;
	}
	
}
