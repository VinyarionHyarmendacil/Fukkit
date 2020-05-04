package vinyarion.fukkit.main.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import vinyarion.fukkit.main.data.FRecipes.IFFacRecipe;
import vinyarion.fukkit.main.util.Ores;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FShapedFacRecipe extends ShapedRecipes implements IFFacRecipe {
	
	private final String[] factions;
	private String name;
	private boolean isPublic;
	
	public FShapedFacRecipe(int i, int j, ItemStack[] stacks, ItemStack stack, String[] factions, String name, boolean isPublic) {
		super(i, j, stacks, stack);
		this.factions = factions;
		this.name = name;
		this.isPublic = isPublic;
	}
	
	public String[] getFactions() {
		return this.factions;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isPublic() {
		return isPublic;
	}
	
    public boolean matches(InventoryCrafting crafting, World world) {
        for (int i = 0; i <= 3 - this.recipeWidth; ++i) {
            for (int j = 0; j <= 3 - this.recipeHeight; ++j) {
                if (this.checkMatch(crafting, i, j, true)) {
                    return true;
                }
                if (this.checkMatch(crafting, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean checkMatch(InventoryCrafting crafting, int rcol, int rrow, boolean flipped) {
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                int col = k - rcol;
                int row = l - rrow;
                ItemStack recslot = null;
                if (col >= 0 && row >= 0 && col < this.recipeWidth && row < this.recipeHeight) {
                    if (flipped) {
                        recslot = this.recipeItems[this.recipeWidth - col - 1 + row * this.recipeWidth];
                    } else {
                        recslot = this.recipeItems[col + row * this.recipeWidth];
                    }
                }
                ItemStack slot = crafting.getStackInRowAndColumn(k, l);
                if (slot != null || recslot != null) {
                    if (slot == null && recslot != null || slot != null && recslot == null) {
                        return false;
                    }
                	if (recslot.hasTagCompound() && recslot.getTagCompound().hasKey("FItem", NBT.STRING)) {
                		if(slot.hasTagCompound() && slot.getTagCompound().hasKey("FItem", NBT.STRING)) {
                			String rn = recslot.getTagCompound().getString("FItem");
                			String sn = slot.getTagCompound().getString("FItem");
                			if (rn.equals(sn)) {
                				continue;
                			}
                		}
                	}
                    if (recslot.getItem() != slot.getItem()) {
                        if (!Ores.hasSameOreName(slot, recslot)) {
                        	return false;
                        }
                    }
                    if (recslot.getItemDamage() != 32767 && recslot.getItemDamage() != slot.getItemDamage()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
	
}
