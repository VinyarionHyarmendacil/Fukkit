package vinyarion.fukkit.main.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import vinyarion.fukkit.main.data.FRecipes.IFFacRecipe;
import vinyarion.fukkit.main.game.FItem;

public class FShapelessFacRecipe extends ShapelessRecipes implements IFFacRecipe {
	
	private final String[] factions;
	private String name;
	private boolean isPublic;
	
	public FShapelessFacRecipe(String[] factions, String name, boolean isPublic, ItemStack stack, List list) {
		super(stack, list);
		this.factions = factions;
		this.name = name;
		this.isPublic = isPublic;
	}
	
	public String[] getFactions() {
		return factions;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isPublic() {
		return this.isPublic;
	}
	
	public boolean matches(InventoryCrafting matrix, World world) {
		ArrayList needed = new ArrayList(this.recipeItems);
		ArrayList had = new ArrayList();
        Object oo = null;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                ItemStack slot = matrix.getStackInRowAndColumn(j, i);
                if (slot != null) {
                    Object o = null;
                    for (Object ritem : needed) {
                        if(ritem instanceof Item && ritem.equals(slot.getItem())) {
                            had.add(ritem);
        				} else if(ritem instanceof FItem && ritem.equals(slot)) {
        					had.add(ritem);
        				} else {
        					o = ritem;
        				}
                    }
	        		if(o != null) {
	    				if(oo != null) {
	    					return false;
	    				} else {
	    					oo = o;
	    				}
	    			}
                }
            }
        }
        needed.removeAll(had);
        return needed.isEmpty() && oo != null;
	}
	
}
