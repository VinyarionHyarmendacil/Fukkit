package vinyarion.fukkit.main.recipes;

import lotr.common.LOTRMod;
import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.attrib.FAttributes;

public class FSpikedBootsRecipe extends FAnvilRecipe implements Permenant {
	
	public FSpikedBootsRecipe(String name, boolean pub) {
		super(name, pub);
	}
	
	public boolean matches(ItemStack left, ItemStack top, ItemStack bottom) {
		if(bottom == null) {
			if(top != null && top.getItem() == Item.getItemFromBlock(LOTRMod.stalactite)) {
				return true;
			}
		}
		return false;
	}
	
	public ItemStack getResult(ItemStack left, ItemStack top, ItemStack bottom) {
		ItemStack ret = left.copy();
		FAttributes.spikedBoots.addTo(ret, "10");
		return ret;
	}
	
	public ItemStack clickResult(LOTRContainerAnvil anvil, ItemStack result) {
		ItemStack stal = anvil.getSlot(1).getStack();
		stal.stackSize--;
		if(stal.stackSize <= 0) {
			anvil.getSlot(1).putStack(null);
		}
		return result;
	}
	
}
