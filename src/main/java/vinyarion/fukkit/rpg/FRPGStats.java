package vinyarion.fukkit.rpg;

import lotr.common.fac.LOTRFaction;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.playerdata.IPlayerData;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.archetypes.FClass;
import vinyarion.fukkit.rpg.archetypes.FClasses;
import vinyarion.fukkit.rpg.archetypes.Faction;
import vinyarion.fukkit.rpg.archetypes.Race;
import vinyarion.fukkit.rpg.archetypes.Stat;

public class FRPGStats implements IPlayerData.Sub {

	public FRPGStats(FPlayerDataRPG rpg) {
		this.rpg = rpg;
	}

	private final FPlayerDataRPG rpg;

	public String getID() {
		return "RPGStats";
	}

	public IPlayerData getParent() {
		return rpg;
	}

	public Race race = Race.HUMAN;
	public FClass fclass = FClasses.beginner;
	public Faction faction = Faction.def;

	public Stat strength = new Stat(Stat.Type.STRENGTH);
	public Stat dexterity = new Stat(Stat.Type.DEXTERITY);
	public Stat constitution = new Stat(Stat.Type.CONSTITUTION);
	public Stat wisdom = new Stat(Stat.Type.WISDOM);
	public Stat luck = new Stat(Stat.Type.LUCK);

	public int level = 0;
	public int xp = 0;

	public void generateDefaults() {
		race = Race.HUMAN;
		fclass = FClasses.beginner;
		faction = Faction.def;
		level = 0;
		xp = 0;
	}

	public void fromNBT(NBTTagCompound nbt) {
		race = Race.of(nbt.getString("race"));
		fclass = FClasses.get(nbt.getString("class"));
		faction = Faction.of(nbt.getString("faction"));
		level = nbt.getInteger("level");
		xp = nbt.getInteger("xp");
		NBTTagCompound misc = NBT.ensure(nbt, "misc_modifiers");
		strength.mod_misc = misc.getInteger("strength");
		dexterity.mod_misc = misc.getInteger("dexterity");
		constitution.mod_misc = misc.getInteger("constitution");
		wisdom.mod_misc = misc.getInteger("wisdom");
		luck.mod_misc = misc.getInteger("luck");
	}

	public void toNBT(NBTTagCompound nbt) {
		nbt.setString("race", race.name());
		nbt.setString("class", fclass.name);
		nbt.setString("faction", faction.parent.codeName());
		nbt.setInteger("level", level);
		nbt.setInteger("xp", xp);
		NBTTagCompound misc = NBT.ensure(nbt, "misc_modifiers");
		misc.setInteger("strength", strength.mod_misc);
		misc.setInteger("dexterity", dexterity.mod_misc);
		misc.setInteger("constitution", constitution.mod_misc);
		misc.setInteger("wisdom", wisdom.mod_misc);
		misc.setInteger("luck", luck.mod_misc);
	}

	public void onLoad(FPlayerData data) {
		this.update();
	}

	public void update() {
		strength.mod_race = race.mod_strength;
		strength.mod_class = fclass.mod_strength;
		strength.mod_fac = faction.mod_strength;
		strength.calc();
		dexterity.mod_race = race.mod_dexterity;
		dexterity.mod_class = fclass.mod_dexterity;
		dexterity.mod_fac = faction.mod_dexterity;
		dexterity.calc();
		constitution.mod_race = race.mod_constitution;
		constitution.mod_class = fclass.mod_constitution;
		constitution.mod_fac = faction.mod_constitution;
		constitution.calc();
		wisdom.mod_race = race.mod_wisdom;
		wisdom.mod_class = fclass.mod_wisdom;
		wisdom.mod_fac = faction.mod_wisdom;
		wisdom.calc();
		luck.mod_race = race.mod_luck;
		luck.mod_class = fclass.mod_luck;
		luck.mod_fac = faction.mod_luck;
		luck.calc();
	}

}
