package vinyarion.fukkit.main.recipes;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import vinyarion.fukkit.main.data.FRecipes.IFFacRecipe;

public abstract class FRecipeCustomShapeless implements IRecipe,IFFacRecipe {
	
	protected String[] factions;
	protected String name;
	protected boolean isPublic;
	protected int size;
	
	public FRecipeCustomShapeless(String[] factions, String name, boolean isPublic, int size) {
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
	
	public int getRecipeSize() {
		return size;
	}
	
	public ItemStack getRecipeOutput() {
		return null;
	}
	
}
