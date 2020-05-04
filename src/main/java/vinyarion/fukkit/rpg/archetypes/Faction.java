package vinyarion.fukkit.rpg.archetypes;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import lotr.common.fac.LOTRFaction;
import lotr.common.LOTRMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.FGuis;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.collection.VMap;
import vinyarion.fukkit.main.util.nbt.NBT;

public class Faction {

	private static final VMap<LOTRFaction, Faction> map = VMap.newVHashMap();
	public static final List<String> names = Lists.newArrayList();

	public static Faction def;

	public static void init() {
		for(LOTRFaction lotr : LOTRFaction.values()) {
			if(lotr == LOTRFaction.UTUMNO || lotr == LOTRFaction.UNALIGNED) continue;
			Faction faction = new Faction(lotr);
			names.add(faction.name);
			faction.loadData();
			map.put(lotr, faction);
		}
		def = new Faction(LOTRFaction.UNALIGNED);
		def.loadData();
		map.put(LOTRFaction.UNALIGNED, def);
		map.put(LOTRFaction.UTUMNO, def);
		names.add("Unaligned");
		names.add("Utumno");
	}

	public static void save() {
		map.forEachConsumer((lotr, fac)->fac.saveData());
	}

	public static Faction of(String name) {
		for(LOTRFaction lotr : LOTRFaction.values()) {
			if(lotr.codeName().equalsIgnoreCase(name)) {
				return of(lotr);
			}
		}
		return def;
	}

	public static Faction of(LOTRFaction lotr) {
		return map.get(lotr);
	}

	private Faction(LOTRFaction lotr) {
		parent = lotr;
		tag = FData.instance().tag("factiondata/"+lotr.codeName().toLowerCase());
		name = Misc.titleCase(lotr.codeName().replace('_', ' '));
	}

	public final LOTRFaction parent;
	private final NBTTagCompound tag;
	public final String name;

	public List<UUID> leaders = Lists.newArrayList();
	public List<UUID> officers = Lists.newArrayList();

	private void saveData() {
		NBTTagList l = NBT.overwriteList(tag, "leaders");
		leaders.forEach(uuid->l.appendTag(new NBTTagString(uuid.toString())));
		NBTTagList o = NBT.overwriteList(tag, "officers");
		officers.forEach(uuid->o.appendTag(new NBTTagString(uuid.toString())));
	}

	private void loadData() {
		leaders.clear();
		for(String uuid : NBT.toArrayString(NBT.ensureList(tag, "leaders"))) try {
			leaders.add(UUID.fromString(uuid));
		} catch (Exception e) {}
		officers.clear();
		for(String uuid : NBT.toArrayString(NBT.ensureList(tag, "officers"))) try {
			officers.add(UUID.fromString(uuid));
		} catch (Exception e) {}
	}

	public Faction withStats(int s, int d, int c, int w, int l) {
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
		ItemStack ret = FGuis.name(LOTRMod.scimitarNearHarad, "&r&l"+name, "", "&r  Current selected faction", "");
		FGuis.appendLore(ret,
			"&r    Str: "+Stat.coloredNum(mod_strength),
			"&r    Dex: "+Stat.coloredNum(mod_dexterity),
			"&r    Con: "+Stat.coloredNum(mod_constitution),
			"&r    Wis: "+Stat.coloredNum(mod_wisdom),
			"&r    Luk: "+Stat.coloredNum(mod_luck),
		"");
		return ret;
	}

}
