package vinyarion.fukkit.rpg.forging.rings;

import java.util.List;

import lotr.common.LOTRMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.recipes.FAnvilRecipeSimple;
import vinyarion.fukkit.main.recipes.FShapelessFacRecipe;
import vinyarion.fukkit.main.recipes.Permenant;

public class FRingForgingRingSmithHammer extends FAnvilRecipeSimple implements Permenant {
	
	public FRingForgingRingSmithHammer(String name, boolean isPublic, ItemStack stack) {
		super(name, isPublic, new ItemStack(LOTRMod.blacksmithHammer), new ItemStack(LOTRMod.mithril), new ItemStack(LOTRMod.mithril), stack);
		FAttributes.forgingGrade.addTo(stack, Grade.NORMAL.name());
	}
	
}
