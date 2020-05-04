package vinyarion.fukkit.main.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.attrib.FAttribute;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.util.Colors;

public class FAttributeRecipeShapeless extends FRecipeCustomShapeless {
	
	public final List items = new ArrayList();
	public final FAttribute attribute;
	
	public FAttributeRecipeShapeless(String[] factions, String name, boolean isPublic, List items, FAttribute attribute) {
		super(factions, name, isPublic, 2);
		this.items.addAll(items);
		this.attribute = attribute;
	}
	
	public boolean matches(InventoryCrafting matrix, World world) {
		ArrayList needed = new ArrayList(this.items);
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
	
	public ItemStack getCraftingResult(InventoryCrafting matrix) {
		ArrayList needed = new ArrayList(this.items);
		ArrayList had = new ArrayList();
        ItemStack oo = null;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                ItemStack slot = matrix.getStackInRowAndColumn(j, i);
                if (slot != null) {
                    ItemStack o = null;
                    for (Object ritem : needed) {
                        if(ritem instanceof Item && ritem.equals(slot.getItem())) {
                            had.add(ritem);
        				} else if(ritem instanceof FItem && ritem.equals(slot)) {
        					had.add(ritem);
        				} else {
        					o = slot;
        				}
                    }
	        		if(o != null) {
	    				if(oo != null) {
	    					return null;
	    				} else {
	    					oo = o.copy();
	    				}
	    			}
                }
            }
        }
        needed.removeAll(had);
        this.attribute.addTo(oo);
        return (needed.isEmpty() && oo != null) ? oo : null;
	}
	
}
