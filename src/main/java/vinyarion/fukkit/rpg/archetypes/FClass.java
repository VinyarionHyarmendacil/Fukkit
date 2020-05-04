package vinyarion.fukkit.rpg.archetypes;

import lotr.common.LOTRMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.FGuis;

public class FClass {

	public String id;
	public String name;
	public Type type;
	public Tier tier;

	public FClass(String name, Type type, Tier tier) {
		this(name.replace(" ", ""), name, type, tier);
	}

	public FClass(String id, String name, Type type, Tier tier) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.tier = tier;
	}

	public FClass withStats(int s, int d, int c, int w, int l) {
		mod_strength = s;
		mod_dexterity = d;
		mod_constitution = c;
		mod_wisdom = w;
		mod_luck = l;
		return this;
	}

	public int mod_strength;
	public int mod_dexterity;
	public int mod_constitution;
	public int mod_wisdom;
	public int mod_luck;

	public ItemStack stack(EntityPlayer player, ChestGui gui) {
		ItemStack ret = FGuis.name(LOTRMod.mithrilBook, "&r&l"+name, "", "&r  Current selected class", "");
		FGuis.appendLore(ret,
			"&r    Str: "+Stat.coloredNum(mod_strength),
			"&r    Dex: "+Stat.coloredNum(mod_dexterity),
			"&r    Con: "+Stat.coloredNum(mod_constitution),
			"&r    Wis: "+Stat.coloredNum(mod_wisdom),
			"&r    Luk: "+Stat.coloredNum(mod_luck),
		"");
		return ret;
	}

	public static enum Type {
		NONE,
		MELEE,
		RANGE,
		MAGIC;
	}

	public static enum Tier {
		NONE,
		BASIC,
		MEDIUM,
		ADVANCED,
		EXPERT;
	}

}
