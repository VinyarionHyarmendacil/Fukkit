package vinyarion.fukkit.rpg.skill;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.FPlayerDataRPG;

public abstract class FSkill {
	
	public FSkill(String name, Type type, Category category, int levels, ItemStack display, String... info) {
		this.name = name;
		this.type = type;
		this.display = display;
		this.category = category;
		this.info = new String[info.length][];
		for(int i = 0; i < info.length; i++) {
			this.info[i] = info[i].split("\\R");
		}
		this.levels = levels;
		this.cooldowns = new int[levels];
		this.isLevelable = levels > 0;
	}
	
	public FSkill setShortName(String shortName) {
		this.shortName = shortName;
		return this;
	}
	
	public FSkill setSps(int... reqSPs) {
		this.reqSPs = reqSPs;
		return this;
	}
	
	public FSkill setAllCooldowns(int cooldownTicks) {
		Arrays.fill(this.cooldowns, cooldownTicks);
		return this;
	}
	
	public FSkill setCooldowns(int... cooldownsTicks) {
		System.arraycopy(cooldownsTicks, 0, this.cooldowns, 0, this.levels);
		return this;
	}
	
	public FSkill setAllinfopre(String... allinfopre) {
		this.allinfopre = allinfopre;
		return this;
	}
	
	public FSkill indentInfo() {
		this.indentInfo = true;
		return this;
	}
	
	public FSkill setAllinfo(String... allinfo) {
		this.allinfo = allinfo;
		return this;
	}
	
	public FSkill setOnNextAttack(INA ina) {
		this.ina = ina;
		return this;
	}
	
	public FSkill setOnAttack(IAA iaa) {
		this.iaa = iaa;
		return this;
	}
	
	public FSkill setOnNextAttacked(IND ind) {
		this.ind = ind;
		return this;
	}
	
	public FSkill setOnAttacked(IAD iad) {
		this.iad = iad;
		return this;
	}
	
	public FSkill setOnActivated(IA ia) {
		this.ia = ia;
		return this;
	}
	
	public FSkill setOnRightClick(IRC irc) {
		this.irc = irc;
		return this;
	}
	
	public FSkill setOnTick(IT it) {
		this.it = it;
		return this;
	}
	
	public final String name;
	public String shortName;
	public String id;
	public String[] allinfopre = new String[0];
	public boolean indentInfo = false;
	public final String[][] info;
	public String[] allinfo = new String[0];
	public final Type type;
	public final Category category;
	public boolean isLevelable;
	public boolean requiresPrevious = true;
	public final ItemStack display;
	public int levels;
	public int[] reqSPs;
	public int[] cooldowns; // in ticks
	private IND ind = null;
	private IAD iad = null;
	private INA ina = null;
	private IAA iaa = null;
	private IA ia = null;
	private IRC irc = null;
	private IT it = null;
	
	public boolean isLearnedBy(EntityPlayer player, int level) {
		return FPlayerDataRPG.of(player).hasLearned(this, level);
	}
	
	public boolean isPrevLearnedIfApplicable(EntityPlayer player, int level) {
		return !this.requiresPrevious ? true : level == 0 ? true : this.isLearnedBy(player, (level - 1));
	}
	
	public void learn(EntityPlayer player, int level) {
		FPlayerDataRPG rpg = FPlayerDataRPG.of(player);
		rpg.skillPoints -= this.reqSkillPoints(player, level);
		rpg.learnedSkills.put(this, Math.max(level, rpg.learnedSkills.getOrDefault(this, -1)));
	}
	
	public int getInstance(EntityPlayer player, int level) {
		return FPlayerDataRPG.of(player).learnedSkills.getOrDefault(this, -1);
	}
	
	public int reqSkillPoints(EntityPlayer player, int level) {
		return this.reqSPs[MathHelper.clamp_int(level, 0, this.reqSPs.length-1)];
	}
	
	public abstract boolean reqFulfilled(EntityPlayer player, int level);
	
	public boolean onNextAttack(EntityPlayer player, EntityLivingBase target, FSkillsInstanceActive active) {
		active.firstTarget = active.lastTarget = target;
		if(ina != null) return ina.onNextAttack(player, target, active);
		return true;
	}

	public static interface INA {
		public boolean onNextAttack(EntityPlayer player, EntityLivingBase target, FSkillsInstanceActive active);
	}

	public boolean onAttack(EntityPlayerMP attacker, EntityLivingBase target, FSkillsInstanceActive active) {
		active.lastTarget = target;
		if(iaa != null) return iaa.onAttack(attacker, target, active);
		return true;
	}

	public static interface IAA {
		public boolean onAttack(EntityPlayer player, EntityLivingBase target, FSkillsInstanceActive active);
	}
	
	public boolean onNextAttacked(EntityLivingBase attacker, EntityPlayer target, FSkillsInstanceActive active) {
		active.firstAgressor = active.lastAgressor = attacker;
		if(ind != null) return ind.onNextAttacked(attacker, target, active);
		return true;
	}

	public static interface IND {
		public boolean onNextAttacked(EntityLivingBase attacker, EntityPlayer target, FSkillsInstanceActive active);
	}

	public boolean onAttacked(EntityLivingBase attacker, EntityPlayerMP target, FSkillsInstanceActive active) {
		active.lastAgressor = attacker;
		if(iad != null) return iad.onAttacked(attacker, target, active);
		return true;
	}

	public static interface IAD {
		public boolean onAttacked(EntityLivingBase attacker, EntityPlayer target, FSkillsInstanceActive active);
	}
	
	public int onActivated(EntityPlayer player, FSkillsInstanceActive active) {
		if(ia != null) return ia.onActivated(player, active);
		return -1;
	}

	public static interface IRC {
		public boolean onRightClick(EntityPlayerMP user, FSkillsInstanceActive active, ItemStack held);
	}
	
	public boolean onRightClick(EntityPlayerMP user, FSkillsInstanceActive active, ItemStack held) {
		if(irc != null) return irc.onRightClick(user, active, held);
		return false;
	}

	public static interface IA {
		public int onActivated(EntityPlayer player, FSkillsInstanceActive active);
	}
	
	public int onTick(EntityPlayer player, FSkillsInstanceActive active) {
		if(it != null) return it.onTick(player, active);
		return -1;
	}

	public static interface IT {
		public int onTick(EntityPlayer player, FSkillsInstanceActive active);
	}
	
	public boolean isSelected(EntityPlayer player, int level) {
		if(this.type == Type.Active) {
			return FPlayerDataRPG.of(player).selectedActiveSkills.getOrDefault(this, -1).intValue() == level;
		} else if(this.type == Type.Passive) {
			return FPlayerDataRPG.of(player).selectedPassiveSkills.getOrDefault(this, -1).intValue() == level;
		} else if(this.type == Type.Permenant) {
			return true;
		}
		return false;
	}
	
	public boolean canSelect(EntityPlayer player, int level) {
		if(this.type == Type.Active) {
			FPlayerDataRPG ps = FPlayerDataRPG.of(player);
			return ps.selectedActiveSkills.size() < ps.activeMax && ps.selectedActiveSkills.getOrDefault(this, -1).intValue() != level;
		} else if(this.type == Type.Passive) {
			FPlayerDataRPG ps = FPlayerDataRPG.of(player);
			return ps.selectedPassiveSkills.size() < ps.passiveMax && ps.selectedPassiveSkills.getOrDefault(this, -1).intValue() != level;
		} else if(this.type == Type.Permenant) {
			return true;
		}
		return false;
	}
	
	public boolean select(EntityPlayer player, int level) {
		if(this.type == Type.Active) {
			FPlayerDataRPG.of(player).selectedActiveSkills.put(this, level);
			return true;
		} else if(this.type == Type.Passive) {
			FPlayerDataRPG.of(player).selectedPassiveSkills.put(this, level);
			return true;
		} else if(this.type == Type.Permenant) {
			return true;
		}
		return false;
	}
	
	public boolean deselect(EntityPlayer player, int level) {
		if(this.type == Type.Active) {
			return FPlayerDataRPG.of(player).selectedActiveSkills.remove(this) != null;
		} else if(this.type == Type.Passive) {
			return FPlayerDataRPG.of(player).selectedPassiveSkills.remove(this) != null;
		} else if(this.type == Type.Permenant) {
			return false;
		}
		return true;
	}
	
	public static enum Type {
		Passive, 
		Active, 
		Permenant, 
		Disposable, 
		Timed_cmd
	}
	
	public enum Category {
		Sword, 
		Dagger, 
		Axe, 
		Hammer, 
		Spear, 
		Pike, 
		Crossbow, 
		Bow, 
		AxeThrown, 
		Sling, 
		Makeshift, 
		Unarmed, 
		Armor;
	}

	public FSkill setApplicablePassive(ISF filter) {
		this.applicablePassive = filter;
		return this;
	}

	public FSkill setApplicableActive(ISF filter) {
		this.applicableActive = filter;
		return this;
	}

	private ISF applicablePassive = null;
	private ISF applicableActive = null;

	public boolean isApplicablePassive(ItemStack held) {
		return applicablePassive == null ? false : applicablePassive.test(held);
	}

	public boolean isApplicableActive(ItemStack held) {
		return applicableActive == null ? false : applicableActive.test(held);
	}

	public static interface ISF {
		public boolean test(ItemStack stack);
	}

	public FSkill setTickDuration(int i) {
		return this
			.setOnActivated((p, l)->i)
			.setOnTick((p, d)->{
				return -1;
			});
	}
	
	public int hashCode() {
		return this.id.hashCode();
	}
	
}
