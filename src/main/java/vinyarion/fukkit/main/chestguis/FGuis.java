package vinyarion.fukkit.main.chestguis;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FGuis {

	public static CGButtonBack back() {
		return new CGButtonBack();
	}

	public static CGButtonBack.Exit exit() {
		return new CGButtonBack.Exit();
	}

	public static CGButtonChild child(String name, String stack) {
		return new CGButtonChild(name, from(Colors.color(stack)));
	}

	public static CGButtonChild child(String name, ItemStack stack) {
		return new CGButtonChild(name, stack);
	}

	public static CGButtonChild child(String name, Function<EntityPlayer, ItemStack> function) {
		return new CGButtonChild(name, function);
	}

	public static CGButtonToggle toggle(String name, String setting, ItemStack stack) {
		return new CGButtonToggle(name, setting, stack);
	}

	public static CGButtonToggle toggle(String name, String setting, Function<EntityPlayer, ItemStack> function) {
		return new CGButtonToggle(name, setting, function);
	}

	public static ItemStack from(String nbt) {
		try {
			return ItemStack.loadItemStackFromNBT((NBTTagCompound)JsonToNBT.func_150315_a(nbt));
		} catch (NBTException e) {
			return new ItemStack(Blocks.stone);
		}
	}

	public static ItemStack name(Object item, String name, String... lore) {
		return item(item, 1, 0, name, lore);
	}

	public static ItemStack size(Object item, int size, String name, String... lore) {
		return item(item, size, 0, name, lore);
	}

	public static ItemStack meta(Object item, int meta, String name, String... lore) {
		return item(item, 1, meta, name, lore);
	}

	public static ItemStack item(Object item, int size, int meta, String name, String... lore) {
		ItemStack ret = (item == null) ? new ItemStack(Blocks.stained_glass_pane, 1, 8) : (item instanceof Item) ? new ItemStack((Item)item, size, meta) : (item instanceof Block) ? new ItemStack((Block)item, size, meta) : new ItemStack(Blocks.stone);
		ret.setStackDisplayName(Colors.color(name));
		appendLore(ret, lore);
		return ret;
	}

	public static void appendLore(ItemStack stack, String... lore) {
		NBTTagList list = NBT.ensureList(NBT.ensure(stack, "display"), "Lore");
		for(String line : lore) {
			list.appendTag(new NBTTagString(Colors.color(line)));
		}
	}

	public static void send(EntityPlayer player, String text) {
		player.addChatMessage(Colors.MAKE(text));
	}

}
