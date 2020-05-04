package vinyarion.fukkit.rpg.forging;

import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRMod;
import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.cmd.FCommandTitles;
import vinyarion.fukkit.main.data.FAnvilRecipes;
import vinyarion.fukkit.main.recipes.FAnvilRecipe;
import vinyarion.fukkit.main.recipes.Permenant;
import vinyarion.fukkit.main.util.FieldHandle;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.forging.rings.FRingForging;

public class FBoundSkills extends FAnvilRecipe implements Permenant {

	public static void init() {
		FAnvilRecipes.instance().addPerm(new FBoundSkills("BoundSkills_enchant", false));
	}

	public FBoundSkills(String name, boolean pub) {
		super(name, pub);
	}

	public boolean matches(ItemStack left, ItemStack top, ItemStack bottom) {
		return left != null && (top != null && top.getItem() == LOTRMod.blacksmithHammer) && (bottom != null && bottom.getItem() == LOTRMod.silverNugget);
	}

	public ItemStack getResult(ItemStack left, ItemStack top, ItemStack bottom) {
		return left;
	}

	private static final FieldHandle<LOTRContainerAnvil, String> repairedItemName = FieldHandle.of(ReflectionHelper.findField(LOTRContainerAnvil.class, "repairedItemName"));
	public ItemStack clickResult(LOTRContainerAnvil anvil, ItemStack result) {
		Slot[] slots = FAnvilRecipe.getLeftTopBottom(anvil);
		slots[0].decrStackSize(1);
		slots[1].decrStackSize(1);
		slots[2].getStack().attemptDamageItem(5, FRingForging.rr);
		String field = repairedItemName.get(anvil);
		String title = FCommandTitles.getClickableFormattedTitle((EntityPlayerMP)anvil.thePlayer).getFormattedText();
		if(field.length() > 0) {
			title = title + ", " + field;
		}
		NBT.ensure(result).setString("LOTROwner", title);
		return result;
	}

}
