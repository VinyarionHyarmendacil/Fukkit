package vinyarion.fukkit.rpg.skill;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lotr.common.LOTRMod;
import lotr.common.entity.projectile.LOTREntityPebble;
import lotr.common.item.LOTRItemCrossbow;
import lotr.common.item.LOTRItemDagger;
import lotr.common.item.LOTRItemHammer;
import lotr.common.item.LOTRItemPike;
import lotr.common.item.LOTRItemPlate;
import lotr.common.item.LOTRItemSling;
import lotr.common.item.LOTRItemSpear;
import lotr.common.item.LOTRItemThrowingAxe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.FNetHandlerPlayServer.FNetData;
import vinyarion.fukkit.main.game.FAesthetics;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.StatusEffect;
import vinyarion.fukkit.rpg.skill.FSkill.Category;
import vinyarion.fukkit.rpg.skill.FSkill.Type;

public class FSkills {

	// // Combat

	// Sword

	public static final FSkill swordPassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Sword)", Type.Passive, Category.Sword, 5, new ItemStack(Items.wooden_sword), 
			"&a+1% Attack Damage", "&a+2% Attack Damage", "&a+3% Attack Damage", "&a+4% Attack Damage", "&a+5% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof ItemSword && !(stack.getItem() instanceof LOTRItemDagger))
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful melee attacks:")
			.indentInfo()
			.setShortName("Damage (Sw)");

	public static final FSkill swordPassiveParry = new FSkillPassiveSimple(
			"Parry", Type.Passive, Category.Sword, 5, new ItemStack(LOTRMod.swordBronze), 
			"&a+10% Defense Guard", "&a+20% Defense Guard", "&a+30% Defense Guard", "&a+40% Defense Guard", "&a+50% Defense Guard")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof ItemSword && !(stack.getItem() instanceof LOTRItemDagger))
			.setSps(2500, 5000, 10000, 17500, 30000)
			.setAllinfopre("&dOn all sucessful melee blocks:")
			.indentInfo()
			.setShortName("Parry")
			.setOnAttacked((a, t, d)->{
				FSkillsHandler handler = FSkillsHandler.current.get();
				handler.pct += Math.max((d.level + 1) * 10.0F, 50.0F);
				return true;
			});

	public static final FSkill swordPassiveRiposte = new FSkillPassiveSimple(
			"Riposte", Type.Passive, Category.Sword, 5, new ItemStack(Items.iron_sword), 
			"&a+10% Attack Damage", "&a+20% Attack Damage", "&a+30% Attack Damage", "&a+40% Attack Damage", "&a+50% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof ItemSword && !(stack.getItem() instanceof LOTRItemDagger))
			.setSps(2500, 5000, 10000, 17500, 30000)
			.setAllinfopre("&dAfter all sucessful melee blocks, on next attack attempt:")
			.setAllinfo("&aTarget must be your last attacker")
			.indentInfo()
			.setShortName("Riposte")
			.setOnAttack((a, t, d)->{
				FNetHandlerPlayServer fnhps = FNetHandlerPlayServer.of(a);
				FNetData netdata = fnhps.data();
				if(netdata.swingsSinceLastBlock <= 1 && netdata.lastAttacker == t.getEntityId()) {
					FSkillsHandler handler = FSkillsHandler.current.get();
					a.addChatMessage(Colors.chat("You stuck your foe in retribution!"));
					handler.pct += Math.min((d.level + 1) * 10.0F, 50.0F);
				}
				return true;
			});

	public static final FSkill swordActiveDisarm = new FSkillActiveSimple(
			"Disarm", Type.Active, Category.Sword, 3, new ItemStack(LOTRMod.swordGulfHarad), 
			"&a10% Disarm Chance", "&a20% Disarm Chance", "&a30% Disarm Chance")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemSword && !(stack.getItem() instanceof LOTRItemDagger))
			.setSps(125000, 500000, 750000)
			.setAllCooldowns(20*60*2)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.setAllinfo("&aTarget drops weapon")
			.indentInfo()
			.setShortName("Disarm")
			.setOnNextAttack((a, t, d)->{
				int g = a.getRNG().nextInt(100);
				int h = FSkillsConstants.chanceDisarmOrSeize[d.level];
//				a.addChatMessage(Colors.make(g+" , "+h));
				if(g < h) {
					ItemStack held = t.getEquipmentInSlot(0);
					t.setCurrentItemOrArmor(0, null);
					EntityItem ei = t.entityDropItem(held, 0.0F);
					ei.motionY += 2.0D;
					a.addChatMessage(Colors.make("You have disarmed "+t.getCommandSenderName()));
				}
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill swordActivePiercelunge = new FSkillActiveSimple(
			"Piercing Lunge", Type.Active, Category.Sword, 3, new ItemStack(LOTRMod.swordWoodElven), 
			"&aInflicts 1 second of Bleed", "&aInflicts 3 seconds of Bleed", "&aInflicts 5 seconds of Bleed")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemSword && !(stack.getItem() instanceof LOTRItemDagger))
			.setSps(2500, 5000, 10000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Pierce.Lunge")
			.setOnNextAttack((a, t, d)->{
				if(t instanceof EntityPlayer) {
					FPlayerDataRPG rpg = FPlayerDataRPG.of((EntityPlayer)t);
					int time = FSkillsConstants.piercelungeTime[d.level];
					rpg.inflictStatus(StatusEffect.BLEED, time);
				} else {
					int time = FSkillsConstants.piercelungeTime[d.level];
					t.addPotionEffect(new PotionEffect(Potion.wither.id, time, 0, false));
				}
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill swordActiveFinalblow = new FSkillActiveSimple(
			"Final Blow", Type.Active, Category.Sword, 3, new ItemStack(LOTRMod.swordMithril), 
			"&a125% Attack Damage", "&a150% Attack Damage", "&a175% Attack Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemSword && !(stack.getItem() instanceof LOTRItemDagger))
			.setSps(2500, 7500, 12500)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Final Blow")
			.setOnNextAttack((a, t, d)->{
				FSkillsHandler handler = FSkillsHandler.current.get();
				handler.pct += FSkillsConstants.finalblowPct[d.level];
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill swordActiveStunningflat = new FSkillActiveSimple(
			"Stunning Flat", Type.Active, Category.Sword, 5, new ItemStack(LOTRMod.swordTauredain), 
			"&a+0.25 Attack Damage\n&a0.1 seconds of Stun", "&a+0.5 Attack Damage\n&a0.25 seconds of Stun", "&a+0.75 Attack Damage\n&a0.5 seconds of Stun", "&a+1 Attack Damage\n&a0.75 seconds of Stun", "&a+1.25 Attack Damage\n&a1 second of Stun")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemSword && !(stack.getItem() instanceof LOTRItemDagger))
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Stun.Flat")
			.setOnNextAttack((a, t, d)->{
				FSkillsHandler handler = FSkillsHandler.current.get();
				handler.abs += FSkillsConstants.stunningflatAbs[d.level];
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill swordActiveQuickstrike = new FSkillActiveSimple(
			"Quick Strikes", Type.Active, Category.Sword, 3, new ItemStack(LOTRMod.swordCorsair), 
			"&a+10% Attack Damage per combo hit\n&aMaximum 3 hits", "&a+10% Attack Damage per combo hit\n&aMaximum 5 hits", "&a+10% Attack Damage per combo hit\n&aMaximum 7 hits")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemSword && !(stack.getItem() instanceof LOTRItemDagger))
			.setSps(2500, 7500, 15000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Quick Strike")
			.setOnNextAttack((a, t, d)->{
				return false;
			})
			.setOnAttack((a, t, d)->{
				FNetHandlerPlayServer fnhps = FNetHandlerPlayServer.of(a);
				FNetData netdata = fnhps.data();
				if(netdata.swingsSinceLastHit >= 1) return true;
				int max = FSkillsConstants.quickstrikeMaxes[d.level];
				if(max < netdata.combo) return true;
				FSkillsHandler handler = FSkillsHandler.current.get();
				handler.pct *= (float)Math.pow(1.10, netdata.combo);
				return false;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill swordActiveSplinterguard = new FSkillActiveSimple(
			"Splintering Guard", Type.Active, Category.Sword, 5, new ItemStack(LOTRMod.ancientItem), 
			"&a100% Defence Absorbtion\n&cWeapon durability drops by (15 x damage)", "&a100% Defence Absorbtion\n&cWeapon durability drops by (10 x damage)", "&a100% Defence Absorbtion\n&cWeapon durability drops by (7 x damage)", "&a100% Defence Absorbtion\n&cWeapon durability drops by (5 x damage)", "&a100% Defence Absorbtion\n&cWeapon durability drops by (3 x damage)")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemSword && !(stack.getItem() instanceof LOTRItemDagger))
			.setSps(2500, 5000, 10000, 17500, 30000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next sucessful melee block:")
			.indentInfo()
			.setShortName("Splint.Guard")
			.setOnNextAttacked((a, t, d)->{
				FSkillsHandler handler = FSkillsHandler.current.get();
				handler.pct = -100000.0F;
				handler.abs = -100000.0F;
				int dmg = (int)handler.damage * 15;
				ItemStack slot = t.getEquipmentInSlot(0);
				if(slot.attemptDamageItem(dmg, t.getRNG())) {
					t.setCurrentItemOrArmor(0, null);
				}
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	// Axe

	public static final FSkill axePassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Axe)", Type.Passive, Category.Axe, 5, new ItemStack(Items.wooden_axe), 
			"&a+1% Attack Damage", "&a+2% Attack Damage", "&a+3% Attack Damage", "&a+4% Attack Damage", "&a+5% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof ItemAxe)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful melee attacks:")
			.indentInfo()
			.setShortName("Damage (Ax)");

	public static final FSkill axeActiveArmorbreaker = new FSkillActiveSimple(
			"Armor Breaker", Type.Passive, Category.Axe, 5, new ItemStack(LOTRMod.axeMithril), 
			"&a200% Durability Damage\n&a1% chance of 800% Durability Damage", "&a200% Durability Damage\n&a2% chance of 1600% Durability Damage", "&a200% Durability Damage\n&a3% chance of 3200% Durability Damage", "&a200% Durability Damage\n&a4% chance of 4800% Durability Damage", "&a300% Durability Damage\n&a5% chance of 6400% Durability Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemAxe)
			.setSps(1000, 2500, 5000, 10000, 12500)
			.setAllCooldowns(20*60*2)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Armor Break")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill axeActiveGougingcut = new FSkillActiveSimple(
			"Gouging Cut", Type.Active, Category.Axe, 5, new ItemStack(LOTRMod.axeBronze), 
			"&aInflicts 1 second of Bleed", "&aInflicts 3 seconds of Bleed", "&aInflicts 5 seconds of Bleed", "&aInflicts 7 seconds of Bleed", "&aInflicts 10 seconds of Bleed")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemAxe)
			.setSps(2500, 5000, 10000, 25000, 50000)
			.setAllCooldowns(20*60*2)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Gouging Cut")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill axeActiveStunningblow = new FSkillActiveSimple(
			"Stunning Blow", Type.Active, Category.Axe, 5, new ItemStack(LOTRMod.battleaxeLossarnach), 
			"&c-75% Attack Damage\n&a1 second of Stun", "&c-50% Attack Damage\n&a1.25 seconds of Stun", "&c-25% Attack Damage\n&a1.5 seconds of Stun", "&e+0% Attack Damage\n&a1.75 seconds of Stun", "&a+25% Attack Damage\n&a2 seconds of Stun")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemAxe)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllCooldowns(20*60*2)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Stun.Blow")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill axeActiveHeadsplitter = new FSkillActiveSimple(
			"Head Splitter", Type.Active, Category.Axe, 3, new ItemStack(Items.skull), 
			"&a125% Attack Damage", "&a150% Attack Damage", "&a175% Attack Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemAxe)
			.setSps(2500, 7500, 15000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Headsplitter")
			.setTickDuration(FSkillsConstants.standard_duration);

	// Dagger

	public static final FSkill daggerPassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Dagger)", Type.Passive, Category.Dagger, 5, new ItemStack(LOTRMod.daggerBronze), 
			"&a+1% Attack Damage", "&a+2% Attack Damage", "&a+3% Attack Damage", "&a+4% Attack Damage", "&a+5% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof LOTRItemDagger)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful melee attacks:")
			.indentInfo()
			.setShortName("Damage (Dg)");

	public static final FSkill daggerActiveSupriseattack = new FSkillActiveSimple(
			"Coup de Main", Type.Active, Category.Dagger, 5, new ItemStack(LOTRMod.daggerUtumno), 
			"&a150% Arrack Damage\n10% chance of 1 second of Bleed", "&a2000% Arrack Damage\n20% chance of 1 second of Bleed", "&a250% Arrack Damage\n30% chance of 2 seconds of Bleed", "&a300% Arrack Damage\n40% chance of 2 seconds of Bleed", "&a350% Arrack Damage\n50% chance of 3 seconds of Bleed")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemDagger)
			.setSps(2500, 5000, 10000, 25000, 50000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Surpr.Attk.")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill daggerActiveQuickslash = new FSkillActiveSimple(
			"Quick Slash", Type.Active, Category.Dagger, 5, new ItemStack(LOTRMod.daggerBlackUruk), 
			"&a+10% Attack Damage per combo hit\n&aMaximum 3 hits", "&a+10% Attack Damage per combo hit\n&aMaximum 5 hits", "&a+10% Attack Damage per combo hit\n&aMaximum 7 hits", "&a+10% Attack Damage per combo hit\n&aMaximum 10 hits", "&a+10% Attack Damage per combo hit\n&aMaximum 15 hits")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemDagger)
			.setSps(2500, 5000, 10000, 25000, 50000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Quick Slash")
			.setTickDuration(FSkillsConstants.standard_duration);

	// Spear

	public static final FSkill spearPassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Spear)", Type.Passive, Category.Spear, 5, new ItemStack(LOTRMod.spearBronze), 
			"&a+1% Attack Damage", "&a+2% Attack Damage", "&a+3% Attack Damage", "&a+4% Attack Damage", "&a+5% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof LOTRItemSpear)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful melee attacks:")
			.indentInfo()
			.setShortName("Damage (Sp)");

	// Pike

	public static final FSkill pikePassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Pike)", Type.Passive, Category.Pike, 5, new ItemStack(LOTRMod.pikeNearHarad), 
			"&a+1% Attack Damage", "&a+2% Attack Damage", "&a+3% Attack Damage", "&a+4% Attack Damage", "&a+5% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof LOTRItemPike)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful melee attacks:")
			.indentInfo()
			.setShortName("Damage (Pk)");

	// Hammer

	public static final FSkill hammerPassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Hammer)", Type.Passive, Category.Hammer, 5, new ItemStack(LOTRMod.hammerUtumno), 
			"&a+1% Attack Damage", "&a+2% Attack Damage", "&a+3% Attack Damage", "&a+4% Attack Damage", "&a+5% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof LOTRItemHammer)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful melee attacks:")
			.indentInfo()
			.setShortName("Damage (Hm)");

	public static final FSkill hammerPassiveArmorobliterator = new FSkillActiveSimple(
			"Armor Obliterator", Type.Passive, Category.Hammer, 5, new ItemStack(LOTRMod.hammerUruk), 
			"&a200% Durability Damage\n&a1% chance of 1600% Durability Damage", "&a300% Durability Damage\n&a2% chance of 3200% Durability Damage", "&a400% Durability Damage\n&a4% chance of 4800% Durability Damage", "&a500% Durability Damage\n&a8% chance of 6400% Durability Damage", "&a500% Durability Damage\n&a16% chance of 12800% Durability Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemHammer)
			.setSps(1000, 2500, 7500, 25000, 50000)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Armor Obl.");

	public static final FSkill hammerActiveWideswing = new FSkillActiveSimple(
			"Wide Swing", Type.Active, Category.Hammer, 5, new ItemStack(LOTRMod.maceBlackNumenorean), 
			"&a1m Area Attack", "&a1.5m Area Attack", "&a2m Area Attack", "&a2.5m Area Attack", "&a3m Area Attack")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemHammer)
			.setSps(1000, 2500, 5000, 10000, 15000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Wide Swing")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill hammerActiveHeavyblow = new FSkillActiveSimple(
			"Heavy Blow", Type.Active, Category.Hammer, 5, new ItemStack(LOTRMod.maceHalfTroll), 
			"&a+1 Knockback\n&a10% chance of 1 second of Stun", "&a+1 Knockback\n&a20% chance of 1.5 seconds of Stun", "&a+1 Knockback\n&a30% chance of 2 seconds of Stun", "&a+1 Knockback\n&a40% chance of 2.5 seconds of Stun", "&a+2 Knockback\n&a50% chance of 3 seconds of Stun")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemHammer)
			.setSps(1000, 2500, 7500, 15000, 25000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Heavy Blow")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill hammerActiveDebilitatingblow = new FSkillActiveSimple(
			"Debilitating Blow", Type.Active, Category.Hammer, 5, new ItemStack(LOTRMod.hammerUtumno), 
			"&c-75% Attack Damage\n&c1 second of Stun", "&c-50% Attack Damage\n&c1.5 seconds of Stun", "&c-25% Attack Damage\n&c2 seconds of Stun", "&a0% Attack Damage\n&c3 seconds of Stun", "&a+25% Attack Damage\n&c5 seconds of Stun")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemHammer)
			.setSps(2500, 5000, 10000, 25000, 50000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Debil.Blow")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill hammerActiveFallinganvil = new FSkillActiveSimple(
			"Falling Anvil", Type.Active, Category.Hammer, 3, new ItemStack(LOTRMod.hammerMithril), 
			"&a125% Attack Damage", "&a150% Attack Damage", "&a175% Attack Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemHammer)
			.setSps(2500, 7500, 12500)
			.setAllCooldowns(20*60*2)
			.setAllinfopre("&dOn next meelee attack attempt:")
			.indentInfo()
			.setShortName("Fall.Anvil")
			.setTickDuration(FSkillsConstants.standard_duration);

	// Crossbow

	public static final FSkill crossbowPassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Crossbow)", Type.Passive, Category.Crossbow, 5, new ItemStack(LOTRMod.bronzeCrossbow), 
			"&a+1% Attack Damage", "&a+2% Attack Damage", "&a+3% Attack Damage", "&a+4% Attack Damage", "&a+5% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof LOTRItemCrossbow)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful ranged attacks:")
			.indentInfo()
			.setShortName("Damage (Cb)");

	public static final FSkill crossbowActiveArmorpiercer = new FSkillActiveSimple(
			"Armor Piercer", Type.Active, Category.Crossbow, 5, new ItemStack(LOTRMod.crossbowBolt), 
			"&a300% Durability Damage\n&a1 second of Bleed\n&c-75% Ranged Damage", "&a500% Durability Damage\n&a2 seconds of Bleed\n&c-75% Ranged Damage", "&a700% Durability Damage\n&a3 seconds of Bleed\n&c-75% Ranged Damage", "&a1000% Durability Damage\n&a4 seconds of Bleed\n&c-75% Ranged Damage", "&a1500% Durability Damage\n&a5 seconds of Bleed\n&c-75% Ranged Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemCrossbow)
			.setSps(5000, 10000, 25000, 50000, 100000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next ranged attack attempt:")
			.setAllinfo("&aResulting attack damage ignores target's armor")
			.indentInfo()
			.setShortName("Armor Pierc.")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill crossbowActiveBluntshot = new FSkillActiveSimple(
			"Blunt Shot", Type.Active, Category.Crossbow, 5, new ItemStack(LOTRMod.orcSkullStaff), 
			"&a+1 Knockback\n&a1 second of Stun\n&c-75% Ranged Damage", "&a+1 Knockback\n&a1.25 seconds of Stun\n&c-50% Ranged Damage", "&a+1 Knockback\n&a1.5 seconds of Stun\n&c-25% Ranged Damage", "&a+2 Knockback\n&a1.75 seconds of Stun\n&e+0% Ranged Damage", "&a+3 Knockback\n&a2 seconds of Stun\n&a+25% Ranged Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemCrossbow)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next ranged attack attempt:")
			.indentInfo()
			.setShortName("Blunt Shot")
			.setTickDuration(FSkillsConstants.standard_duration);

	// Bow

	public static final FSkill bowPassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Bow)", Type.Passive, Category.Bow, 5, new ItemStack(Items.bow), 
			"&a+1% Attack Damage", "&a+2% Attack Damage", "&a+3% Attack Damage", "&a+4% Attack Damage", "&a+5% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof ItemBow && !(stack.getItem() instanceof LOTRItemCrossbow))
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful ranged attacks:")
			.indentInfo()
			.setShortName("Damage (Bw)");

	public static final FSkill bowPassiveDeadshot = new FSkillActiveSimple(
			"Dead Shot", Type.Passive, Category.Bow, 3, new ItemStack(LOTRMod.daleBow), 
			"&a+50% Accuracy", "&a+75% Accuracy", "&a+100% Accuracy")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemBow && !(stack.getItem() instanceof LOTRItemCrossbow))
			.setSps(1000, 2500, 5000)
			.setAllinfopre("&dOn next ranged attack attempt:")
			.indentInfo()
			.setShortName("Dead Shot");

	public static final FSkill bowPassiveLongshot = new FSkillActiveSimple(
			"Long Shot", Type.Passive, Category.Bow, 3, new ItemStack(LOTRMod.highElvenBow), 
			"&a+(Distance x 0.75)% Ranged Damage", "&a+(Distance x 1.25)% Ranged Damage", "&a+(Distance x 1.75)% Ranged Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemBow && !(stack.getItem() instanceof LOTRItemCrossbow))
			.setSps(2500, 5000, 15000)
			.setAllinfopre("&dOn next ranged attack attempt:")
			.setAllinfo("&aDistance is the range to the target in blocks")
			.indentInfo()
			.setOnAttack((a, t, d)->{
				FSkillsHandler handler = FSkillsHandler.current.get();
				float mul = FSkillsConstants.bowLongshotPct[d.level];
				handler.pct += (mul * a.getDistanceToEntity(t));
				return false;
			})
			.setShortName("Long Shot");

	public static final FSkill bowActiveUnsteadyfingers = new FSkillActiveSimple(
			"Unsteady Fingers", Type.Active, Category.Bow, 5, new ItemStack(LOTRMod.helmetUrukBerserker), 
			"&cBow randomly fires arrow between 10 and 25 seconds of being drawn\n&a300% Ranged Damage", "&cBow randomly fires arrow between 7 and 20 seconds of being drawn\n&a350% Ranged Damage", "&cBow randomly fires arrow between 5 and 15 seconds of being drawn\n&a400% Ranged Damage", "&cBow randomly fires arrow between 3 and 12 seconds of being drawn\n&a450% Ranged Damage", "&cBow randomly fires arrow between 1 and 7 seconds of being drawn\n&a500% Ranged Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemBow && !(stack.getItem() instanceof LOTRItemCrossbow))
			.setSps(2500, 5000, 10000, 25000, 50000)
			.setAllCooldowns(20*60*2)
			.setAllinfopre("&dOn next ranged attack attempt:")
			.indentInfo()
			.setShortName("Unst.Fingers")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill bowActiveStunningshot = new FSkillActiveSimple(
			"Stunning Shot", Type.Active, Category.Bow, 5, new ItemStack(LOTRMod.gundabadUrukBow), 
			"&a1 second of Stun\n&c-75% Ranged Damage", "&a1.25 seconds of Stun\n&c-50% Ranged Damage", "&a1.5 seconds of Stun\n&c-25% Ranged Damage", "&a1.75 seconds of Stun\n&e+0% Ranged Damage", "&a2 seconds of Stun\n&a+25% Ranged Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemBow && !(stack.getItem() instanceof LOTRItemCrossbow))
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next ranged attack attempt:")
			.indentInfo()
			.setShortName("Stunn.Shot")
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill bowActiveKnee = new FSkillActiveSimple(
			"Arrow to the Knee", Type.Active, Category.Bow, 3, new ItemStack(LOTRMod.helmetRhunWarlord), 
			"&aInflicts 1 second of Bleed\n&a25% chance of Leg Injury", "&aInflicts 2 seconds of Bleed\n&a50% chance of Leg Injury", "&aInflicts 3 seconds of Bleed\n&a75% chance of Leg Injury")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof ItemBow && !(stack.getItem() instanceof LOTRItemCrossbow))
			.setSps(2500, 5000, 15000)
			.setAllCooldowns(20*60*3)
			.setAllinfopre("&dOn next ranged attack attempt:")
			.indentInfo()
			.setShortName("Knee Arrow")
			.setTickDuration(FSkillsConstants.standard_duration);

	// Throwing axe

	public static final FSkill throwingaxePassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Throwing Axe)", Type.Passive, Category.AxeThrown, 5, new ItemStack(LOTRMod.throwingAxeBronze), 
			"&a+1% Attack Damage", "&a+2% Attack Damage", "&a+3% Attack Damage", "&a+4% Attack Damage", "&a+5% Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof LOTRItemThrowingAxe)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful ranged attacks:")
			.indentInfo()
			.setShortName("Damage (TA)");

	public static final FSkill throwingaxePassiveMeelee = new FSkillPassiveDamage(
			"Meelee Damage", Type.Passive, Category.AxeThrown, 4, new ItemStack(LOTRMod.throwingAxeIron), 
			"&a+2 Attack Damage", "&a+4 Attack Damage", "&a+5.5 Attack Damage", "&a+7 Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof LOTRItemThrowingAxe)
			.setSps(1000, 2500, 5000, 7500)
			.setAllinfopre("&dOn all sucessful melee attacks:")
			.indentInfo()
			.setShortName("TA Melee");

	// Sling

	public static final FSkill slingPassiveDamage = new FSkillPassiveDamage(
			"Pure Damage (Sling)", Type.Passive, Category.Sling, 5, new ItemStack(LOTRMod.sling), 
			"&a+1 Attack Damage", "&a+2 Attack Damage", "&a+3 Attack Damage", "&a+4 Attack Damage", "&a+5 Attack Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof LOTRItemSling)
			.setSps(1000, 8000, 20000, 50000, 100000)
			.setAllinfopre("&dOn all sucessful ranged attacks:")
			.indentInfo()
			.setShortName("Damage (Sl)");

	public static final FSkill slingActiveBetweeneyes = new FSkillActiveSimple(
			"Between the Eyes", Type.Active, Category.Sling, 5, new ItemStack(LOTRMod.silverNugget), 
			"&a+1 Attack Damage\n&a5% chance of +5 Attack Damage", "&a+2 Attack Damage\n&a10% chance of +10 Attack Damage", "&a+3 Attack Damage\n&a15% chance of +15 Attack Damage", "&a+4 Attack Damage\n&a20% chance of +20 Attack Damage", "&a+5 Attack Damage\n&a25% chance of +25 Attack Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemSling)
			.setSps(5000, 10000, 25000, 75000, 150000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next ranged attack attempt:")
			.indentInfo()
			.setShortName("Betw.Eyes")
			.setOnNextAttack((a, t, d)->{
				FSkillsHandler handler = FSkillsHandler.current.get();
				boolean crit = a.getRNG().nextInt(100) < FSkillsConstants.slingeyesChance[d.level];
				int dmg = crit ? FSkillsConstants.slingeyesCrit[d.level] : FSkillsConstants.slingeyesDmg[d.level];
				handler.abs += dmg;
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill slingActiveKnockoutshot = new FSkillActiveSimple(
			"Knockout Shot", Type.Active, Category.Sling, 5, new ItemStack(Blocks.cobblestone), 
			"&a0.5 seconds of Stun", "&a0.75 seconds of Stun", "&a1 second of Stun", "&a1.25 seconds of Stun", "&a1.5 seconds of Stun")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemSling)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn next ranged attack attempt:")
			.indentInfo()
			.setShortName("Knock.Shot")
			.setOnNextAttack((a, t, d)->{
				if(t instanceof EntityPlayer) {
					FPlayerDataRPG rpg = FPlayerDataRPG.of((EntityPlayer)t);
					int dur = FSkillsConstants.slingKoshotTime[d.level];
					rpg.inflictStatus(StatusEffect.STUN, dur);
				} else {
					int dur = FSkillsConstants.slingKoshotTime[d.level];
					t.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, dur, 0, false));
				}
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill slingActiveVolley = new FSkillActiveSimple(
			"Volley", Type.Active, Category.Sling, 5, new ItemStack(LOTRMod.pebble), 
			"&aFires 3 pebbles", "&aFires 5 pebbles", "&aFires 7 pebbles", "&aFires 10 pebbles", "&aFires 15 pebbles")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemSling)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllCooldowns(20*60*1)
			.setAllinfopre("&dOn next several ranged attack attempts:")
			.indentInfo()
			.setShortName("Volley")
			.setOnRightClick((u, a, h)->{
				World world = u.worldObj;
				int total = FSkillsConstants.slingVolley[a.level];
				float pitch = u.rotationPitch;
				float yaw = u.rotationYaw;
				for(int i = 1; i < total; i++) {
					u.rotationPitch = pitch + (u.getRNG().nextFloat() * Math.min(i, 20) - 10);
					u.rotationYaw = yaw + (u.getRNG().nextFloat() * Math.min(i, 20) - 10);
					if(u.inventory.hasItem(LOTRMod.pebble) || u.capabilities.isCreativeMode) {
						h.damageItem(1, u);
						if(u.capabilities.isCreativeMode ? !NBT.hasEnchantment(h, Enchantment.infinity) : false) u.inventory.consumeInventoryItem(LOTRMod.pebble);
						world.playSoundAtEntity(u, "random.bow", 0.5f, 0.4f / (u.getRNG().nextFloat() * 0.4f + 0.8f));
						world.spawnEntityInWorld(new LOTREntityPebble(world, u).setSling());
					}
				}
				u.rotationPitch = pitch;
				u.rotationYaw = yaw;
				return false;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	// Improv

	public static final FSkill improvPassiveBakerbludgeon = new FSkillPassiveSimple(
			"Baker's Bludgeon", Type.Passive, Category.Makeshift, 5, new ItemStack(LOTRMod.rollingPin), 
			"&a1 second of Stun", "&a1.5 seconds of Stun", "&a2 seconds of Stun", "&a2.5 seconds of Stun", "&a3 seconds of Stun")
			.setApplicablePassive(stack -> stack != null && stack.getItem() == LOTRMod.rollingPin)
			.setSps(1000, 2500, 5000, 7500, 10000)
			.setAllinfopre("&dOn all sucessful melee attacks with rolling pins:")
			.indentInfo()
			.setOnAttack((a, t, d)->{
				if(t instanceof EntityPlayer) {
					FPlayerDataRPG rpg = FPlayerDataRPG.of((EntityPlayer)t);
					int dur = FSkillsConstants.rollingpinKoTime[d.level];
					rpg.inflictStatus(StatusEffect.STUN, dur);
				} else {
					int dur = FSkillsConstants.rollingpinKoTime[d.level];
					t.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, dur, 0, false));
				}
				return true;
			})
			.setShortName("Damage (Rp)");

	public static final FSkill improvPassiveArrowmeelee = new FSkillPassiveDamage(
			"Arrow Meelee", Type.Passive, Category.Makeshift, 3, new ItemStack(Items.arrow), 
			"&a+2 Attack Damage\n&c75% chance of consuming arrow", "&a+3.5 Attack Damage\n&c50% chance of consuming arrow", "&a+5 Attack Damage\n&c25% chance of consuming arrow")
			.setNonPercent()
			.setApplicablePassive(stack -> stack.getItem() == Items.arrow || stack.getItem() == LOTRMod.arrowPoisoned)
			.setSps(1000, 2500, 5000)
			.setAllinfopre("&dOn all sucessful melee attacks with arrows:")
			.indentInfo()
			.setShortName("Damage (Ar)");

	public static final FSkill improvPassiveThrowplate = new FSkillPassiveSimple(
			"Bilbo's Nightmare", Type.Passive, Category.Makeshift, 3, new ItemStack(LOTRMod.plate), 
			"&a+2 Ranged Damage", "&a+3.5 Ranged Damage", "&a+5 Ranged Damage")
			.setApplicablePassive(stack -> stack != null && stack.getItem() instanceof LOTRItemPlate)
			.setSps(5000, 15000, 50000)
			.setAllinfopre("&dOn all sucessful ranged attacks with plates:")
			.indentInfo()
			.setShortName("Damage (Pl)");

	public static final FSkill improvActiveElementalplate = new FSkillActiveSimple(
			"Elemental Plates", Type.Active, Category.Makeshift, 3, new ItemStack(LOTRMod.woodPlate), 
			"&aWooden Plates set target aflame for 3 seconds\n&aStoneware Plates do +1 Knockback, and 10% chance of 1 second of Stun\n&aCeramic Plates do +2 Ranged Damage", "&aWooden Plates set target aflame for 5 seconds\n&aStoneware Plates do +2 Knockback, and 20% chance of 2 second of Stun\n&aCeramic Plates do +3 Ranged Damage", "&aWooden Plates set target aflame for 7 seconds\n&aStoneware Plates do +3 Knockback, and 30% chance of 3 second of Stun\n&aCeramic Plates do +4 Ranged Damage")
			.setApplicableActive(stack -> stack != null && stack.getItem() instanceof LOTRItemPlate)
			.setSps(5000, 15000, 25000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn all sucessful ranged attacks with plates:")
			.setAllinfo("&aFlint and Steel durability, Fire Charge, or Match is consumed per Wooden Plate")
			.indentInfo()
			.setShortName("Elem.Plates")
//			.set
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill improvActivePiercingpick = new FSkillActiveSimple(
			"Piercing Pick", Type.Active, Category.Makeshift, 5, new ItemStack(LOTRMod.pickaxeBronze), 
			"&aInflicts 1 second of Bleed", "&aInflicts 2 seconds of Bleed", "&aInflicts 3 seconds of Bleed", "&aInflicts 4 seconds of Bleed", "&aInflicts 5 seconds of Bleed")
			.setApplicableActive(stack -> stack != null && stack.getItem().getToolClasses(stack).contains("pickaxe"))
			.setSps(2500, 5000, 7500, 15000, 25000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn all sucessful melee attacks with pickaxes:")
			.indentInfo()
			.setShortName("Pierc.Pick")
			.setOnNextAttack((a, t, d)->{
				if(t instanceof EntityPlayer) {
					FPlayerDataRPG rpg = FPlayerDataRPG.of((EntityPlayer)t);
					int time = FSkillsConstants.piercepickTime[d.level];
					rpg.inflictStatus(StatusEffect.BLEED, time);
				} else {
					int time = FSkillsConstants.piercepickTime[d.level];
					t.addPotionEffect(new PotionEffect(Potion.wither.id, time, 0, false));
				}
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill improvActiveArmorshatter = new FSkillActiveSimple(
			"Armor Shatterer", Type.Active, Category.Makeshift, 5, new ItemStack(LOTRMod.pickaxeMithril), 
			"&a400% Durability Damage\n&a2% chance of 1600% Durability Damage", "&a600% Durability Damage\n&a4% chance of 3200% Durability Damage", "&a800% Durability Damage\n&a8% chance of 4800% Durability Damage", "&a1000% Durability Damage\n&a16% chance of 6400% Durability Damage", "&a1000% Durability Damage\n&a32% chance of 12800% Durability Damage")
			.setSps(5000, 10000, 25000, 50000, 100000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn all sucessful melee attacks pickaxes:")
			.indentInfo()
			.setShortName("Atmor Shat.")
			.setApplicableActive(stack -> stack != null && stack.getItem().getToolClasses(stack).contains("pickaxe"))
			.setOnNextAttack((a, t, d)->{
				FSkillsHandler handler = FSkillsHandler.current.get();
				int m = (a.getRNG().nextInt(100) < FSkillsConstants.chanceArmorshatter[d.level] ? FSkillsConstants.armorshatterDurBig : FSkillsConstants.armorshatterDur)[d.level];
				float dmg = handler.damage*m-1;
				if(t instanceof EntityPlayer) {
					((EntityPlayer)t).inventory.damageArmor(dmg);
				} else {
					dmg /= 4.0F;
			        if(dmg < 1.0F) dmg = 1.0F;
					for(int i = 1; i < 5; i++) {
						ItemStack slot = t.getEquipmentInSlot(i);
						if(slot.attemptDamageItem((int)dmg, t.getRNG())) {
							t.setCurrentItemOrArmor(i, null);
						}
					}
				}
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill improvActiveFirebrand = new FSkillActiveSimple(
			"Firebrand", Type.Active, Category.Makeshift, 5, new ItemStack(Blocks.torch), 
			"&a10% of setting target aflame for 1 second\n&c25% chance of consuming torch", "&a20% of setting target aflame for 2 seconds\n&c20% chance of consuming torch", "&a30% of setting target aflame for 3 seconds\n&c15% chance of consuming torch", "&a40% of setting target aflame for 4 seconds\n&c10% chance of consuming torch", "&a50% of setting target aflame for 5 seconds\n&c5% chance of consuming torch")
			.setSps(2500, 5000, 7500, 15000, 25000)
			.setAllCooldowns(20*30)
			.setAllinfopre("&dOn all sucessful melee attacks with torches:")
			.indentInfo()
			.setShortName("Firebrand")
			.setApplicableActive(stack -> stack != null && stack.getItem().getUnlocalizedName().toLowerCase().contains("torch"))
			.setOnNextAttack((a, t, d)->{
				if(a.getRNG().nextInt(100) < FSkillsConstants.chanceFirebrandAflame[d.level]) {
					t.setFire(FSkillsConstants.firebrandTime[d.level]);
				}
				if(a.getRNG().nextInt(100) < FSkillsConstants.chanceFirebrandConsume[d.level]) {
					a.inventory.decrStackSize(a.inventory.currentItem, 1);
					FAesthetics.soundPlayer((EntityPlayerMP)a, "random.break", 1, 1);
				}
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	// Unarmed

	public static final FSkill unarmedPassiveDamage = new FSkillPassiveDamage(
			"Martial Arts", Type.Passive, Category.Unarmed, 3, new ItemStack(LOTRMod.saltedFlesh), 
			"&a+1 Attack Damage", "&a+2 Attack Damage", "&a+3 Attack Damage")
			.setNonPercent()
			.setApplicablePassive(stack -> stack == null)
			.setSps(2500, 7500, 15000)
			.setAllinfopre("&dOn all sucessful melee attacks:")
			.indentInfo()
			.setShortName("Damage (--)");

	public static final FSkill unarmedPassivePunch = new FSkillActiveSimple(
			"Knockout Punch", Type.Passive, Category.Unarmed, 3, new ItemStack(LOTRMod.nauriteGem), 
			"&a+1 Knockback\n25% for 1 second of Stun", "&a+2 Knockback\n50% for 1 second of Stun", "&a+3 Knockback\n75% for 1 second of Stun")
			.setSps(2500, 10000, 25000)
			.setAllinfopre("&dOn next sucessful melee attack:")
			.indentInfo()
			.setShortName("Knock.Punch")
			.setApplicableActive(stack -> stack == null)
			.setOnNextAttack((a, t, d)->{
				if(a.getRNG().nextInt(100) < FSkillsConstants.chancePunchStun[d.level]) {
					t.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 0, true));
					double dmg = (1 + d.level) / 5;
					Vec3 v = Misc.interpolate(a.rotationPitch, a.rotationYaw);
					v.xCoord *= dmg;
					v.yCoord *= dmg;
					v.zCoord *= dmg;
					t.addVelocity(v.xCoord, v.yCoord, v.zCoord);
				}
				return true;
			});

	public static final FSkill unarmedActiveSeizure = new FSkillActiveSimple(
			"Seizure", Type.Active, Category.Unarmed, 3, new ItemStack(LOTRMod.rope), 
			"&a10% Disarm Chance", "&a20% Disarm Chance", "&a30% Disarm Chance")
			.setSps(5000, 15000, 50000)
			.setAllCooldowns(20*60*2)
			.setAllinfo("&aUser takes weapon")
			.setAllinfopre("&dOn next sucessful melee attack:")
			.indentInfo()
			.setShortName("Seizure")
			.setApplicableActive(stack -> stack == null)
			.setOnNextAttack((a, t, d)->{
				int g = a.getRNG().nextInt(100);
				int h = FSkillsConstants.chanceDisarmOrSeize[d.level];
//				a.addChatMessage(Colors.make(g+" , "+h));
				if(g < h) {
					ItemStack held = t.getEquipmentInSlot(0);
					t.setCurrentItemOrArmor(0, null);
					a.inventory.addItemStackToInventory(held);
					a.addChatMessage(Colors.make("You seized "+t.getCommandSenderName()+"'s weapon!"));
				}
				return true;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	public static final FSkill unarmedActiveChokehold = new FSkillActiveSimple(
			"Chokehold", Type.Active, Category.Unarmed, 3, new ItemStack(LOTRMod.hithlain), 
			"&a1 Attack Damage per second held\n+10 Maintain Hold", "&a1 Attack Damage per second held\n+15 Maintain Hold", "&a1 Attack Damage per second held\n+20 Maintain Hold")
			.setSps(5000, 15000, 50000)
			.setAllCooldowns(20*60*2)
			.setAllinfopre("&dOn next sucessful melee attack:")
			.setAllinfo("&aDamage bypasses target's armor")
			.indentInfo()
			.setShortName("Chokehold")
			.setApplicableActive(stack -> stack == null)
			.setTickDuration(FSkillsConstants.standard_duration);

	// Armor

	public static final FSkill armorPassiveDamagedeflect = new FSkillPassiveSimple(
			"Damage Deflection", Type.Passive, Category.Armor, 5, new ItemStack(Items.iron_helmet), 
			"&a+10% Defense Absorbtion\n&c+200% Durability Damage", "&a+20% Defense Absorbtion\n&c+300% Durability Damage", "&a+30% Defense Absorbtion\n&c+450% Durability Damage", "&a+40% Defense Absorbtion\n&c+700% Durability Damage", "&a+50% Defense Absorbtion\n&c+1000% Durability Damage")
			.setSps(2500, 5000, 7500, 15000, 25000)
			.setAllinfopre("&dOn all melee or ranged hits:")
			.indentInfo()
			.setShortName("Dmg.Dflct.")
			.setApplicableActive(stack -> true)
			.setOnAttacked((a, t, d)->{
				FSkillsHandler handler = FSkillsHandler.current.get();
				handler.pct -= d.level * 10F + 10F;
				float dur = handler.damage * FSkillsConstants.damagedeflectDur[d.level];
				for(int i = 1; i <= 4; i++) {
					ItemStack stack = a.getEquipmentInSlot(i);
					if(stack.isItemStackDamageable()) {
						stack.damageItem(Math.round(dur), t);
					}
				}
				return true;
			});

	public static final FSkill armorActiveRigidstance = new FSkillActiveSimple(
			"Rigid Stance", Type.Active, Category.Armor, 3, new ItemStack(LOTRMod.ancientItem, 1, 3), 
			"&a+100 Defense Absorbtion\n&c+10000% Durability Damage", "&a+100 Defense Absorbtion\n&c+7500% Durability Damage", "&a+100 Defense Absorbtion\n&c+5000% Durability Damage")
			.setSps(5000, 15000, 50000)
			.setAllCooldowns(20*60*1)
			.setAllinfopre("&dOn all melee or ranged hits:")
			.indentInfo()
			.setShortName("Rigid Stance")
			.setApplicableActive(stack -> true)
			.setOnAttacked((a, t, d)->{
				FSkillsHandler handler = FSkillsHandler.current.get();
				handler.pct = -100000.0F;
				handler.abs = -100000.0F;
				float dur = handler.damage * FSkillsConstants.rigidstanceDur[d.level];
				for(int i = 1; i <= 4; i++) {
					ItemStack stack = a.getEquipmentInSlot(i);
					if(stack.isItemStackDamageable()) {
						stack.damageItem(Math.round(dur), t);
					}
				}
				return false;
			})
			.setTickDuration(FSkillsConstants.standard_duration);

	// // Non-combat

	// Movement

	// Mining

	// Cooking

	// Item Forging

	// Ring Forging

	// Misc

	// // End

	public static final List<String> names = Lists.newArrayList();
	public static final List<FSkill> skills = Lists.newArrayList();
	public static final Map<String, FSkill> nameMap = Maps.newHashMap();
	public static final Map<Category, List<FSkill>> cat2skill = Maps.newHashMap();
	
	static {
		for(Field f : FSkills.class.getDeclaredFields()) {
			if(FSkill.class.isAssignableFrom(f.getType())) {
				try {
					f.setAccessible(true);
					FSkill s = (FSkill)f.get(null);
					s.id = f.getName();
					skills.add(s);
					names.add(s.id);
					nameMap.put(s.id, s);
					if(!cat2skill.containsKey(s.category)) {
						cat2skill.put(s.category, Lists.newArrayList());
					}
					cat2skill.get(s.category).add(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static FSkill get(String name) {
		return nameMap.get(name);
	}
	
	public static List<FSkill> get(Category category) {
		return cat2skill.getOrDefault(category, Collections.EMPTY_LIST);
	}
	
	public static List<String> names() {
		return names;
	}
	
}
