package vinyarion.fukkit.rpg.archetypes;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import lotr.common.LOTRMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.FGuis;
import vinyarion.fukkit.main.util.Misc;

public enum Race {

	HUMAN
	(2, 2, 2, 2, 2),
	ELF
	(2, 2, 1, 4, 1),
	DWARF
	(1, 1, 4, 3, 1),
	HOBBIT
	(0, 3, 1, 2, 4),
	ENT
	(10, 0, 10, 10, 10),
	ORC
	(3, 2, 3, 1, 0),
	URUK
	(4, 2, 4, 0, 0),
	GOBLIN
	(2, 3, 2, 0, 3);

	private Race(int s, int d, int c, int w, int l) {
		mod_strength = s;
		mod_dexterity = d;
		mod_constitution = c;
		mod_wisdom = w;
		mod_luck = l;
		name = Misc.titleCase(name());
	}

	public final int mod_strength;
	public final int mod_dexterity;
	public final int mod_constitution;
	public final int mod_wisdom;
	public final int mod_luck;

	public final String name;

	public static Race of(String name) {
		for(Race race : values()) {
			if(race.name.equalsIgnoreCase(name)) {
				return race;
			}
		}
		return HUMAN;
	}

	public ItemStack stack(EntityPlayer player, ChestGui gui) {
		ItemStack ret = FGuis.name(LOTRMod.helmetGondorWinged, "&r&l"+name, "", "&r  Current selected race", "");
		FGuis.appendLore(ret,
			"&r    Str: "+Stat.coloredNum(mod_strength),
			"&r    Dex: "+Stat.coloredNum(mod_dexterity),
			"&r    Con: "+Stat.coloredNum(mod_constitution),
			"&r    Wis: "+Stat.coloredNum(mod_wisdom),
			"&r    Luk: "+Stat.coloredNum(mod_luck),
		"");
		return ret;
	}

	public static final List<String> names;

	static {
		List<String> n = Lists.newArrayList();
		for(Race r : values()) n.add(r.name);
		names = Collections.unmodifiableList(n);
	}

}
