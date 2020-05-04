package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribVanishing extends FAttribute<Void> {
	
	public FAttribVanishing(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public boolean isOn(ItemStack stack) {
		return this.isOnLore(stack);
	}
	
	public int addTo(ItemStack stack, String... params) {
		this.setOwnedLine(stack, "&cVanishing");
		return 0;
	}
	
	public Void update(EntityPlayer player, ItemStack stack) {
		return null;
	}
	
	public void removeFrom(ItemStack stack) {
		this.removeOwnedLine(stack);
	}
	
}
