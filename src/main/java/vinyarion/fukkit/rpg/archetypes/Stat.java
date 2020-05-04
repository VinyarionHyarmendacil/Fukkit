package vinyarion.fukkit.rpg.archetypes;

import java.util.function.Function;

import lotr.common.LOTRMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.FGuis;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.rpg.FPlayerDataRPG;

public class Stat {

	public Stat(Type type) {
		this.type = type;
	}

	public Type type;
	public int base;
	public int mod_race = 0;
	public int mod_fac = 0;
	public int mod_class = 0;
	public int mod_misc = 0;

	public int value;

	public int calc() {
		return value = type.base + mod_race + mod_fac + mod_class + mod_misc;
	}

	public static String colored(int mod) {
		return 
			mod < -5 ? Colors.LIGHTPURPLE :
			mod < -2 ? Colors.RED :
			mod < 0 ? Colors.GOLD :
			mod > 5 ? Colors.BLUE :
			mod > 2 ? Colors.AQUA :
			mod > 0 ? Colors.GREEN :
			Colors.YELLOW;
	}

	public static String coloredNum(int mod) {
		return colored(mod) + (mod > 0 ? "+" : mod < 0 ? "-" : "±") + mod + Colors.RESET;
	}

	public static enum Type {
		STRENGTH
		(10, LOTRMod.nauriteGem, 0, "&r&lStrength", "&r  A base stat"),
		DEXTERITY
		(10, LOTRMod.hithlain, 0, "&r&lDexterity", "&r  A base stat"),
		CONSTITUTION
		(10, LOTRMod.rhinoHorn, 0, "&r&lConstitution", "&r  A base stat"),
		WISDOM
		(10, LOTRMod.modTemplate, 0, "&r&lWisdom", "&r  A base stat"),
		LUCK
		(0, LOTRMod.clover, 1, "&r&lLuck", "&r  A base stat");
		private Type(int b, Object item, int meta, String display, String... lore) {
			base = b;
			this.item = item;
			this.meta = meta;
			this.display = display;
			this.lore = lore;
		}
		public final int base;
		private Object item;
		private int meta;
		private String display;
		private String[] lore;
		private Function<EntityPlayer, Stat> stat;
		public Stat stat(EntityPlayer player) {
			return stat.apply(player);
		}
		public ItemStack stack(EntityPlayer player, ChestGui gui) {
			ItemStack ret = FGuis.meta(item, meta, display, lore);
			int val = stat.apply(player).value;
			FGuis.appendLore(ret, "", "&rCurrent value: &l"+val, "");
			return ret;
		}
		static {
			STRENGTH.stat = p -> FPlayerDataRPG.of(p).stats.strength;
			DEXTERITY.stat = p -> FPlayerDataRPG.of(p).stats.dexterity;
			CONSTITUTION.stat = p -> FPlayerDataRPG.of(p).stats.constitution;
			WISDOM.stat = p -> FPlayerDataRPG.of(p).stats.wisdom;
			LUCK.stat = p -> FPlayerDataRPG.of(p).stats.luck;
		}
	}

}
