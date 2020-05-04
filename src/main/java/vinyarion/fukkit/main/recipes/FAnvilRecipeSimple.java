package vinyarion.fukkit.main.recipes;

import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.util.Colors;

public class FAnvilRecipeSimple extends FAnvilRecipe {
	
	protected ItemStack leftItem;
	protected ItemStack topItem;
	protected ItemStack bottomItem;
	protected ItemStack result;
	
	public FAnvilRecipeSimple(String name, boolean pub, ItemStack left, ItemStack top, ItemStack bottom, ItemStack res) {
		super(name, pub);
		this.leftItem = left;
		this.topItem = top;
		this.bottomItem = bottom;
		this.result = res;
	}
	
	public boolean matches(ItemStack left, ItemStack top, ItemStack bottom) {
		boolean l = left == null ? this.leftItem == null : areEqual(left, this.leftItem);
		boolean t = top == null ? this.topItem == null : areEqual(top, this.topItem);
		boolean b = bottom == null ? this.bottomItem == null :areEqual( bottom, this.bottomItem);
		return l && t && b;
	}
	
	public ItemStack getResult(ItemStack left, ItemStack top, ItemStack bottom) {
		return this.result.copy();
	}
	
	public ItemStack clickResult(LOTRContainerAnvil anvil, ItemStack res) {
		for(int i = 0; i < 3; i++) {
			Slot as = anvil.getSlot(i);
			if(as.getHasStack()) {
				ItemStack ass = as.getStack();
				if(ass != null) {
					ass.stackSize--;
					if(ass.stackSize <= 0) {
						as.putStack(null);
					}
				}
			}
		}
		anvil.playAnvilSound();
		return res;
	}
	
}
