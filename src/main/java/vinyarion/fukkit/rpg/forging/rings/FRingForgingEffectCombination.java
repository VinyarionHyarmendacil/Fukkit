package vinyarion.fukkit.rpg.forging.rings;

import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.attrib.FRingAttribEffect;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.recipes.FAnvilRecipe;
import vinyarion.fukkit.main.recipes.Permenant;
import vinyarion.fukkit.main.util.Colors;

public class FRingForgingEffectCombination extends FAnvilRecipe implements Permenant {
	
	public FRingForgingEffectCombination(String name, boolean pub) {
		super(name, pub);
	}
	
	public boolean matches(ItemStack left, ItemStack top, ItemStack bottom) {
		return left != null && FRingForging.rings.getOrDefault(left.getItem(), FItem.DUMMY).equals(left) && left.stackSize == 1 && FAttributes.forgingGrade.isOn(left) && 
				top != null && FRingForging.gems.getOrDefault(top.getItem(), FItem.DUMMY).equals(top) && top.stackSize == 1 && 
				FRingForging.ringSmithHammer.equals(bottom);
	}
	
	public ItemStack getResult(ItemStack left, ItemStack top, ItemStack bottom) {
		Grade grade = getRingGrade(left);
		ForgingAttribute fa = ForgingAttribute.of(top);
		int slots = grade.index+1;
		int occupied = 0;
		for(FRingAttribEffect rae : FAttributes.ringPotions) if(rae.isOn(left)) occupied+=(rae.potion==fa.data.effect.id?slots:1);
		if(occupied >= slots) return null;
		ItemStack result = left.copy();
		int diff = slots - occupied - 1;
		String say = Colors.GRAY + Colors.ITALIC + (diff == 0 ? "No" : String.valueOf(diff)) + " open effect slot" + (diff == 1 ? "" : "s");
		FAttributes.forgingGrade.setOwnedLineAlternates(result, say, "0");
		FRingAttribEffect rae = FRingForging.ringEffects.get(top.getItem());
		String amp = rae.potion == Potion.invisibility.id ? "1" : "0";
		rae.addTo(result, amp, String.valueOf(fa.data.tiertimes[grade.index]));
		return result;
	}
	
	public ItemStack clickResult(LOTRContainerAnvil anvil, ItemStack result) {
		Slot[] slots = FAnvilRecipe.getLeftTopBottom(anvil);
		slots[0].decrStackSize(1);
		slots[1].decrStackSize(1);
		slots[2].getStack().attemptDamageItem(5, FRingForging.rr);
		anvil.playAnvilSound();
		return result;
	}
	
	private Grade getRingGrade(ItemStack ring) {
		return FAttributes.forgingGrade.isOn(ring) ? FAttributes.forgingGrade.update(null, ring) : Grade.NORMAL;
	}
	
}
