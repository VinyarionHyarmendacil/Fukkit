package vinyarion.fukkit.rpg.forging.rings;

import lotr.common.LOTRMod;
import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.recipes.FAnvilRecipe;
import vinyarion.fukkit.main.recipes.Permenant;

public class FRingForgingEffectCool extends FAnvilRecipe implements Permenant {
	
	public FRingForgingEffectCool(String name, boolean pub) {
		super(name, pub);
	}
	
	public boolean matches(ItemStack left, ItemStack top, ItemStack bottom) {
		return top != null && top.getItem() == LOTRMod.quenditeCrystal && 
				left != null && FRingForging.rings.getOrDefault(left.getItem(), FItem.DUMMY).equals(left) && 
				FRingForging.ringSmithHammer.equals(bottom);
	}
	
	public ItemStack getResult(ItemStack left, ItemStack top, ItemStack bottom) {
		ItemStack ret = FRingForging.powerrings.get(left.getItem()).stack(left.stackSize, left.getItemDamage(), (NBTTagCompound)left.getTagCompound().copy());
		FAttributes.ringWorn.addTo(ret);
		return ret;
	}
	
	public ItemStack clickResult(LOTRContainerAnvil anvil, ItemStack result) {
		anvil.playAnvilSound();
		Slot[] slots = FAnvilRecipe.getLeftTopBottom(anvil);
		slots[0].decrStackSize(1);
		slots[1].decrStackSize(1);
		if(slots[2].getStack().attemptDamageItem(10, FRingForging.rr)) {
			slots[2].putStack(null);
		}
		return result;
	}
	
}
