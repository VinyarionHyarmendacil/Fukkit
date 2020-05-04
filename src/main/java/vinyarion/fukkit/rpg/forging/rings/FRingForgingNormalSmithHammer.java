package vinyarion.fukkit.rpg.forging.rings;

import lotr.common.LOTRMod;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.recipes.FAnvilRecipeSimple;

public class FRingForgingNormalSmithHammer extends FAnvilRecipeSimple {

	public FRingForgingNormalSmithHammer(String name, boolean isPublic) {
		super(name, isPublic, new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(LOTRMod.blacksmithHammer));
	}

}
