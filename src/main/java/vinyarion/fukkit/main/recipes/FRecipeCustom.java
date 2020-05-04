package vinyarion.fukkit.main.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import vinyarion.fukkit.main.data.FRecipes.IFFacRecipe;

public abstract class FRecipeCustom implements IRecipe,IFFacRecipe {
	
	protected String[] factions;
	protected String name;
	protected boolean isPublic;
	protected int size;
	
	public FRecipeCustom(String[] factions, String name, boolean isPublic, int size) {
		this.factions = factions;
		this.size = size;
		this.name = name;
		this.isPublic = isPublic;
	}
	
	public String[] getFactions() {
		return factions;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isPublic() {
		return isPublic;
	}
	
	public boolean matches(InventoryCrafting crafting, World world) {
		return getCraftingResult(crafting) != null;
	}
	
	public abstract ItemStack getCraftingResult(InventoryCrafting crafting);
	
	public int getRecipeSize() {
		return size;
	}
	
	public ItemStack getRecipeOutput() {
		return null;
	}
	
}
