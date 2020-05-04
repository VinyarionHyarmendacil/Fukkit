package vinyarion.fukkit.rpg.forging.rings;

import lotr.common.LOTRMod;
import lotr.common.inventory.LOTRContainerAnvil;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.cmd.FCommandAnnounce;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.game.FPlayer;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.recipes.FAnvilRecipe;
import vinyarion.fukkit.main.recipes.Permenant;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FRingForgingBook extends FAnvilRecipe implements Permenant {
	
	public FRingForgingBook(String name, boolean pub) {
		super(name, pub);
	}
	
	public boolean matches(ItemStack left, ItemStack top, ItemStack bottom) {
		return top != null && FRingForging.gems.containsKey(top.getItem()) && 
				left != null && left.getItem() == Items.book && 
				bottom == null;
	}
	
	public ItemStack getResult(ItemStack left, ItemStack top, ItemStack bottom) {
		ItemStack ret = left.copy();
		ret.stackSize = 1;
		NBTTagList imbued = NBT.ensureList(NBT.ensure(ret), "ImbuedGems");
		String gem = top.getItem().getUnlocalizedName();
		if(!NBT.hasValue(imbued, gem)) {
			NBT.append(imbued, gem);
			NBT.append(NBT.ensureList(NBT.ensure(ret, "display"), "Lore"), Colors.RESET+Colors.ITALIC+"The "+top.getItem().getItemStackDisplayName(top)+Colors.RESET+Colors.ITALIC+" gem is on the front cover.");
		}
		int gems = 0;
		for(String gemname : FRingForging.gemNames) {
			if(NBT.hasValue(imbued, gemname)) {
				gems++;
			}
		}
		if(gems >= 10) {
			ret = FRingForging.helpBook.stack();
		}
		return ret;
	}
	
	public ItemStack clickResult(LOTRContainerAnvil anvil, ItemStack result) {
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
		if(FRingForging.helpBook.equals(result)) {
			FPermissions.instance().addPermission("anvilrecipe.RingForgingRecipes_randomness", anvil.thePlayer);
			FCommandAnnounce.doAnnounce(anvil.thePlayer.getDisplayName()+" has crafted the text on how to forge rings of power!");
		}
		anvil.playAnvilSound();
		return result;
	}
	
}
