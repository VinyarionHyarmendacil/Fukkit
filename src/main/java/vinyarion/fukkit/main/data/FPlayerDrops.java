package vinyarion.fukkit.main.data;

import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.recipes.FAnvilRecipe;
import vinyarion.fukkit.main.recipes.FRecipeCustom;
import vinyarion.fukkit.main.recipes.FShapedFacRecipe;

public class FPlayerDrops {

	private static FPlayerDrops instance = null;

	public static FPlayerDrops instance() {
		return instance;
	}

	static {
		instance = new FPlayerDrops();
		instance.config = FData.instance().config("player_drops");
		instance.init();
		FMod.log(Level.INFO, "FPlayerDrops initialized!");
	}

	private Configuration config;

	private List<ItemStack> keep = Lists.newArrayList();
	private List<ItemStack> drop = Lists.newArrayList();

	private void init() {
		String[] keeps = config.getStringList("Whitelist", "general", new String[0], 
			"These items will be kept on death\n"
			+ "Place item names in here, like 'minecraft:diamond'\n"
			+ "If you want only certain metadata values, append '@meta' to the item, like 'minecraft:planks@3'\n"
			+ "You can use the numerical item id if you desire"
		);
		for(String s : keeps) {
			try {
				String[] ss = s.split("@");
				ItemStack st = new ItemStack(CommandBase.getItemByText(MinecraftServer.getServer(), ss[0]));
				if(ss.length > 1) {
					st.stackSize = Integer.parseInt(ss[1]);
				}
				keep.add(st);
			} catch(Exception e) {
				FMod.log(Level.ERROR, "FPlayerDrops: bad whitelist line: " + s);
				e.printStackTrace();
			}
		}
		String[] drops = config.getStringList("Blacklist", "general", new String[0], 
			"These items will be dropped on death, even if they are soulbound\n"
			+ "Place item names in here, like 'minecraft:diamond'\n"
			+ "If you want only certain metadata values, append '@meta' to the item, like 'minecraft:planks@3'\n"
			+ "You can use the numerical item id if you desire"
		);
		for(String s : drops) {
			try {
				String[] ss = s.split("@");
				ItemStack st = new ItemStack(CommandBase.getItemByText(MinecraftServer.getServer(), ss[0]));
				if(ss.length > 1) {
					st.stackSize = Integer.parseInt(ss[1]);
				}
				drop.add(st);
			} catch(Exception e) {
				FMod.log(Level.ERROR, "FPlayerDrops: bad blacklist line: " + s);
				e.printStackTrace();
			}
		}
	}

	public boolean shouldDrop(EntityPlayer player, ItemStack stack) {
		if(!FAttributes.soulbound.isOn(stack))
			for(ItemStack s : keep)
				if(FAnvilRecipe.areEqual(s, stack))
					return true;
		for(ItemStack s : drop)
			if(FAnvilRecipe.areEqual(s, stack))
				return false;
		return true;
	}

}
