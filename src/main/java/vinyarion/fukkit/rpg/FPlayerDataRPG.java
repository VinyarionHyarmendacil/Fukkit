package vinyarion.fukkit.rpg;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.chestguis.CGButtonToggle.CGToggle;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.playerdata.IPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.collection.Box;
import vinyarion.fukkit.main.util.collection.Reason.Remove;
import vinyarion.fukkit.main.util.collection.VMap;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.chestguis.FRPGGuis;
import vinyarion.fukkit.rpg.skill.FSkill;
import vinyarion.fukkit.rpg.skill.FSkillsInstanceActive;
import vinyarion.fukkit.rpg.tileentity.FTileEntityData;
import vinyarion.fukkit.rpg.skill.FSkillPassiveDamage;
import vinyarion.fukkit.rpg.skill.FSkillsInstance;
import vinyarion.fukkit.rpg.skill.FSkills;

public class FPlayerDataRPG implements IPlayerData {
	
	private FPlayerData parent;

	public FPlayerData getData() {
		return this.parent;
	}
	
	public CGToggle useShortcuts;
	public CGToggle showScoreboard;
	public CGToggle useRingThing;
	public int skillPoints = 0;
	public VMap<FSkill, Integer> learnedSkills = VMap.newVHashMap();
	public VMap<FSkill, AtomicInteger> skillCooldowns = VMap.newVHashMap().populate(vmap->FSkills.skills.forEach(skill->vmap.put(skill, new AtomicInteger())));
	public int activeMax = 3;
	public int passiveMax = 3;
	public static final int selectedMin = 3, selectedMax = 5;
	public VMap<FSkill, FSkillsInstanceActive> runningActiveSkills = VMap.newVHashMap(selectedMax);
	public VMap<FSkill, Integer> selectedActiveSkills = VMap.newVHashMap(selectedMax);
	public VMap<FSkill, Integer> selectedPassiveSkills = VMap.newVHashMap(selectedMax);
	public int activePointer = 0;
	public VMap<StatusEffect, int[]> currentEffects = VMap.newVHashMap(StatusEffect.count);
	public FRPGStats stats = new FRPGStats(this);
	public TileEntity curentTileEntity = null;
	public FTileEntityData curentTileEntityData = null;
	
	public void moveActivePointer(int dir) {
		this.activePointer += dir;
		while(this.activePointer < 0) this.activePointer += this.activeMax;
		while(this.activePointer >= this.activeMax) this.activePointer -= this.activeMax;
		{
			FSkillsInstance si = this.getActiveSkill();
			if(si.isEmpty()) {
				this.parent.player().addChatMessage(Colors.MAKE("&6Selected an empty slot ("+this.activePointer+")"));
			} else {
				this.parent.player().addChatMessage(Colors.MAKE("&6Selected " + si.skill.name + " at level " + (si.level + 1) + " ("+this.activePointer+")"));
			}
		}
	}

	public void useActiveSkill() {
		FSkillsInstance si = this.getActiveSkill();
		if(si.isEmpty()) {
			this.parent.player().addChatMessage(Colors.MAKE("&6Tried to activate an empty skill slot"));
		} else {
			Box<Boolean> has = Box.of(false);
			this.runningActiveSkills.forEachConsumer((skill, integer) -> {
				if(skill == si.skill) has.setBoxed(Boolean.TRUE);
			});
			if(has.getBoxed().booleanValue()) {
				this.parent.player().addChatMessage(Colors.MAKE("&6Tried to use a currently active skill"));
			} else {
				AtomicInteger cooldown = this.skillCooldowns.get(si.skill);
				if(cooldown.get() > 0) {
					this.parent.player().addChatMessage(Colors.MAKE("&6"+si.skill.name+" is still on cooldown ("+(Time.readable(cooldown.get()/20))+", "+(cooldown.get()%20)+" ticks left)"));
				} else {
					this.parent.player().addChatMessage(Colors.MAKE("&6Activated " + si.skill.name + " (lvl " + (si.level + 1) + ")"));
					FSkillsInstanceActive active = new FSkillsInstanceActive(si.skill, si.level, 0);
					active.ticksLeft = si.skill.onActivated(this.parent.player(), active);
					if(active.ticksLeft > -1) {
						this.runningActiveSkills.put(si.skill, active);
						cooldown.set(si.skill.cooldowns[si.level]);
					}
					FNetHandlerPlayServer.of(this.parent.player()).data().swingsSinceWatched = 0;
				}
			}
		}
	}

	public void onTick() {
		this.stats.update();
		this.runningActiveSkills.forEachVoidReason((skill, active) -> {
			int decr = skill.onTick(this.parent.player(), active);
			if(decr == 0 ? true : (active.ticksLeft += decr) <= 0) {
				this.parent.player().addChatMessage(Colors.MAKE("&6"+skill.name+" timed out"));
				throw new Remove();
			} else {
				active.ticksSince++;
			}
		});
		this.currentEffects.forEachVoidReason((status, pair) -> {
			status.onTick(this, this.parent.player(), pair[1]++);
			if(--pair[0] <= 0) throw new Remove();
		});
		this.skillCooldowns.forEachConsumer((skill, ticks) -> {
			if(ticks.decrementAndGet() == 0) {
				this.parent.player().addChatMessage(Colors.MAKE("&6"+skill.name+" is now available"));
			}
		});
	}

	public FSkillsInstance getActiveSkill() {
		Entry<FSkill, Integer> entry = this.selectedActiveSkills.entrySet().size() > this.activePointer ? (Entry<FSkill, Integer>)this.selectedActiveSkills.entrySet().toArray()[this.activePointer] : null;
		return entry == null ? FSkillsInstance.empty() : FSkillsInstance.of(entry.getKey(), entry.getValue().intValue());
	}

	public boolean hasLearned(FSkill skill, int level) {
		return this.learnedSkills.getOrDefault(skill, -1) >= level;
	}

	public void inflictStatus(StatusEffect effect, int ticks) {
		this.currentEffects.put(effect, new int[]{ticks, 0});
		this.parent.player().addChatMessage(Colors.make(effect.verb()));
	}

	public void onLoad(FPlayerData data) {
		this.parent = data;
		this.useShortcuts = FRPGGuis.activationShortcut.asConfigElement(this.parent.player().getCommandSenderName());
		this.showScoreboard = FRPGGuis.showScoreboard.asConfigElement(this.parent.player().getCommandSenderName());
		this.useRingThing = FRPGGuis.inWorldNotify.asConfigElement(this.parent.player().getCommandSenderName());
	}
	
	public void fromNBT(NBTTagCompound nbt) {
//		System.out.println("Loading RPG data for "+this.parent.player().getCommandSenderName()+"@"+this.parent.player().getGameProfile().getId().toString());
		this.skillPoints = nbt.getInteger("skillPoints");
		this.learnedSkills.clear();
		NBT.iterateCompound(NBT.ensure(nbt, "learnedSkills"), (name, sub) -> {
			FSkill skill = FSkills.get(name);
			if(skill != null) {
				this.learnedSkills.put(skill, sub.getInteger("learnedLevel"));
			}
//			System.out.println("LoadL: "+name+": "+sub.toString());
		});
		this.learnedSkills.remove(null);
		this.activeMax = nbt.getInteger("activeMax");
		this.passiveMax = nbt.getInteger("passiveMax");
		this.selectedActiveSkills.clear();
		this.selectedPassiveSkills.clear();
		NBT.iterateCompound(NBT.ensure(nbt, "selectedActiveSkills"), (name, sub) -> {
			FSkill skill = FSkills.get(name);
			if(skill != null) {
				this.selectedActiveSkills.put(skill, sub.getInteger("activeLevel"));
			}
//			System.out.println("LoadA: "+name+": "+sub.toString());
		});
		NBT.iterateCompound(NBT.ensure(nbt, "selectedPassiveSkills"), (name, sub) -> {
			FSkill skill = FSkills.get(name);
			if(skill != null) {
				this.selectedPassiveSkills.put(skill, sub.getInteger("activeLevel"));
			}
//			System.out.println("LoadP: "+name+": "+sub.toString());
		});
		this.selectedActiveSkills.remove(null);
		this.selectedPassiveSkills.remove(null);
		this.stats.fromNBT(NBT.ensure(nbt, "stats"));
	}
	
	public void toNBT(NBTTagCompound nbt) {
		System.out.println("Saving ");
		nbt.setInteger("skillPoints", this.skillPoints);
		this.learnedSkills.forEachConsumer((skill, level) -> {
			NBTTagCompound sub = new NBTTagCompound();
			sub.setInteger("learnedLevel", level);
			NBT.ensure(nbt, "learnedSkills").setTag(skill.id, sub);
//			System.out.println("SaveL: "+skill.id+": "+sub.toString());
		});
		nbt.setInteger("activeMax", this.activeMax);
		nbt.setInteger("passiveMax", this.passiveMax);
		this.selectedActiveSkills.forEachConsumer((skill, level) -> {
			NBTTagCompound sub = new NBTTagCompound();
			sub.setInteger("activeLevel", level);
			NBT.ensure(nbt, "selectedActiveSkills").setTag(skill.id, sub);
//			System.out.println("SaveA: "+skill.id+": "+sub.toString());
		});
		this.selectedPassiveSkills.forEachConsumer((skill, level) -> {
			NBTTagCompound sub = new NBTTagCompound();
			sub.setInteger("activeLevel", level);
			NBT.ensure(nbt, "selectedPassiveSkills").setTag(skill.id, sub);
//			System.out.println("SaveP: "+skill.id+": "+sub.toString());
		});
		this.stats.toNBT(NBT.ensure(nbt, "stats"));
	}
	
	public void generateDefaults() {
		this.activeMax = 3;
		this.passiveMax = 3;
		this.stats.generateDefaults();
	}
	
	public String getID() {
		return "PlayerSkills";
	}
	
	public static FPlayerDataRPG of(EntityPlayer player) {
		return FPlayerData.forPlayer(player).getData(FPlayerDataRPG.class);
	}

	public FSkillsInstance getApplicablePassiveDamageFromHeld(EntityLivingBase livingEntity) {
		ItemStack held = livingEntity.getHeldItem();
		Box<FSkillsInstance> ret = Box.of(FSkillsInstance.empty());
		this.selectedPassiveSkills.forEachConsumer((skill, level) -> {
			if(skill instanceof FSkillPassiveDamage && skill.isApplicablePassive(held)) {
				ret.setBoxed(FSkillsInstance.of(skill, level));
			}
		});
		return ret.getBoxed();
	}

	public FSkillsInstance getInstanceForSelected(FSkill skill) {
		if(skill == null) 
			return FSkillsInstance.empty();
		for(Entry<FSkill, Integer> entry : this.selectedActiveSkills)
			if(entry.getKey() == skill)
				return FSkillsInstance.of(skill, entry.getValue().intValue());
		for(Entry<FSkill, Integer> entry : this.selectedPassiveSkills)
			if(entry.getKey() == skill)
				return FSkillsInstance.of(skill, entry.getValue().intValue());
		return FSkillsInstance.empty();
	}

}
