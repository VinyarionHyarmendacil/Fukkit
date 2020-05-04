package vinyarion.fukkit.main.recipes;

import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.util.nbt.NBT;

public abstract class FAnvilRecipe {
	
	public final String name;
	public final boolean pub;
	
	public FAnvilRecipe(String name, boolean pub) {
		this.name = name;
		this.pub = pub;
	}
	
	public abstract boolean matches(ItemStack left, ItemStack top, ItemStack bottom);
	
	public abstract ItemStack getResult(ItemStack left, ItemStack top, ItemStack bottom);
	
	public abstract ItemStack clickResult(LOTRContainerAnvil anvil, ItemStack result);
	
	public final String getName() {
		return this.name;
	}
	
	public final boolean isPublic() {
		return this.pub;
	}
	
	public static boolean areEqual(ItemStack slot, ItemStack recslot) {
        if (slot != null || recslot != null) {
            if (slot == null && recslot != null || slot != null && recslot == null) {
                return false;
            }
        	if (recslot.hasTagCompound() && recslot.getTagCompound().hasKey("FItem", NBT.STRING)) {
        		if(slot.hasTagCompound() && slot.getTagCompound().hasKey("FItem", NBT.STRING)) {
        			String rn = recslot.getTagCompound().getString("FItem");
        			String sn = slot.getTagCompound().getString("FItem");
        			if (rn.equals(sn)) {
        				return true;
        			}
        		}
        	}
            if (recslot.getItem() != slot.getItem()) {
                return false;
            }
            if (recslot.getItemDamage() != 32767 && recslot.getItemDamage() != slot.getItemDamage()) {
                return false;
            }
        }
        return true;
	}
	
	public static Slot[] getLeftTopBottom(LOTRContainerAnvil anvil) {
		return new Slot[]{anvil.getSlot(0), anvil.getSlot(1), anvil.getSlot(2)};
	}
	
}
