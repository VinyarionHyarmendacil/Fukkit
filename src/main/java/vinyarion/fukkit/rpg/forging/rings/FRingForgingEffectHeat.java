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

public class FRingForgingEffectHeat extends FAnvilRecipe implements Permenant {
	
	public FRingForgingEffectHeat(String name, boolean pub) {
		super(name, pub);
	}
	
	public boolean matches(ItemStack left, ItemStack top, ItemStack bottom) {
		return top != null && top.getItem() == LOTRMod.nauriteGem && 
				left != null && !FRingForging.rings.getOrDefault(left.getItem(), FItem.DUMMY).equals(left) && 
				FRingForging.ringSmithHammer.equals(bottom);
	}
	
	public ItemStack getResult(ItemStack left, ItemStack top, ItemStack bottom) {
		ItemStack ret = FRingForging.rings.get(left.getItem()).stack(left.stackSize, left.getItemDamage(), left.hasTagCompound() ? (NBTTagCompound)left.getTagCompound().copy() : new NBTTagCompound());
		if(FAttributes.ringWorn.isOn(ret)) {
			FAttributes.ringWorn.removeFrom(ret);
		}
		return ret;
	}
	
	public ItemStack clickResult(LOTRContainerAnvil anvil, ItemStack result) {
		Slot[] slots = FAnvilRecipe.getLeftTopBottom(anvil);
		slots[0].decrStackSize(1);
		slots[1].decrStackSize(1);
		if(slots[2].getStack().attemptDamageItem(10, FRingForging.rr)) {
			slots[2].putStack(null);
		}
		anvil.playAnvilSound();
		return result;
	}
	
}
