package vinyarion.fukkit.main.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class Ores {

	public static boolean hasSameOreName(ItemStack stack, ItemStack other) {
		if(stack == null || other == null) return false;
		for(int idStack : OreDictionary.getOreIDs(stack))
			for(int idOther : OreDictionary.getOreIDs(other))
				if(idStack == idOther) return true;
		return false;
	}

}
