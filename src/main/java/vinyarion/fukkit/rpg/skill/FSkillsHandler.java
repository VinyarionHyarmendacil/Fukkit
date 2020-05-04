package vinyarion.fukkit.rpg.skill;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Sets;

import lotr.common.LOTRMod;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.FNetHandlerPlayServer.FNetData;
import vinyarion.fukkit.main.game.FPlayer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.collection.Reason.Remove;
import vinyarion.fukkit.rpg.Attack;
import vinyarion.fukkit.rpg.FPlayerDataRPG;

public class FSkillsHandler {

	public static final ThreadLocal<FSkillsHandler> current = new ThreadLocal<FSkillsHandler>();

	private FSkillsHandler(float damage, LivingHurtEvent event) {
		this.damage = damage;
		this.event = event;
		this.attack = Attack.of(event.source);
	}

	public boolean justActivated = false;
	public float pct = 100.0F;
	public float abs = 0.0F;
	public final float damage;
	public final LivingHurtEvent event;
	public final Attack attack;
	public boolean isattacker;

	public static void handleLivingHurt(EntityPlayerMP attacker, EntityLivingBase target, ItemStack held, LivingHurtEvent event) {
		FNetHandlerPlayServer fnhps = FNetHandlerPlayServer.of(attacker);
		FNetData netdata = fnhps.data();
		FSkillsHandler handler = new FSkillsHandler(event.ammount, event);
		handler.isattacker = true;
		handler.justActivated = netdata.swingsSinceWatched <= 1;
		current.set(handler);
		FPlayerDataRPG rpg = FPlayerDataRPG.of(attacker);
		handler.pct += rpg.stats.strength.value;
		rpg.runningActiveSkills.forEachVoidReason((skill, active) -> {
			if(!skill.isApplicableActive(held)) {
				throw new Remove();
			} else if(handler.justActivated && skill.onNextAttack(attacker, target, active)) {
				notifyUsed(attacker, skill, active);
				throw new Remove();
			} else if(skill.onAttack(attacker, target, active)) {
				notifyUsed(attacker, skill, active);
				throw new Remove();
			}
		});
		rpg.selectedPassiveSkills.forEachConsumer((skill, level) -> {
			FSkillsInstanceActive active = new FSkillsInstanceActive(skill, level, 0);
			if(skill.onAttack(attacker, target, active)) {
				notifyUsed(attacker, skill, active);
			}
		});
		if(FMod.config.doRPG_combo && netdata.combo >= 3) {
			handler.pct += (float)Math.pow(1.05, (netdata.combo - 2));
		}
		current.remove();
		if(Misc.debug) {
			attacker.addChatMessage(Colors.make("A @: "+handler.event.ammount));
		}
		event.ammount *= handler.pct / 100.0F;
		event.ammount += handler.abs;
		if(event.ammount < 0) event.ammount = 0;
		if(Misc.debug) {
			attacker.addChatMessage(Colors.make("A %: "+handler.pct));
			attacker.addChatMessage(Colors.make("A +: "+handler.abs));
			attacker.addChatMessage(Colors.make("A @: "+handler.event.ammount));
		}
	}

	public static void handleLivingDamage(EntityLivingBase attacker, EntityPlayerMP target, ItemStack held, LivingHurtEvent event) {
		FSkillsHandler handler = new FSkillsHandler(event.ammount, event);
		handler.isattacker = false;
		current.set(handler);
		FPlayerDataRPG rpg = FPlayerDataRPG.of(target);
		rpg.selectedPassiveSkills.forEachConsumer((skill, level) -> {
			FSkillsInstanceActive active = new FSkillsInstanceActive(skill, level, 0);
			if(skill.onAttacked(attacker, target, active)) {
				notifyUsed(target, skill, active);
			}
		});
		current.remove();
		if(Misc.debug) {
			target.addChatMessage(Colors.make("D @:"+handler.event.ammount));
		}
		event.ammount /= handler.pct / 100.0F;
		event.ammount -= handler.abs;
		if(event.ammount < 0) event.ammount = 0;
		if(Misc.debug) {
			target.addChatMessage(Colors.make("D %:"+handler.pct));
			target.addChatMessage(Colors.make("D +:"+handler.abs));
			target.addChatMessage(Colors.make("D @:"+handler.event.ammount));
		}
	}

	public static void handleRightClickItem(EntityPlayerMP user, ItemStack held) {
		FPlayerDataRPG rpg = FPlayerDataRPG.of(user);
		rpg.runningActiveSkills.forEachVoidReason((skill, active) -> {
			if(!skill.isApplicableActive(held)) {
				throw new Remove();
			} else if(skill.onRightClick(user, active, held)) {
				notifyUsed(user, skill, active);
				throw new Remove();
			}
		});
		rpg.selectedPassiveSkills.forEachConsumer((skill, level) -> {
			if(skill.isApplicablePassive(held)) {
				FSkillsInstanceActive active = new FSkillsInstanceActive(skill, level, 0);
				if(skill.onRightClick(user, active, held)) {
					notifyUsed(user, skill, active);
				}
			}
		});
	}

	private static void notifyUsed(EntityPlayerMP user, FSkill skill, FSkillsInstanceActive active) {
		FPlayer.notify(user, "Used "+skill.name, FNetHandlerPlayServer.of(user).data().lookingTarget);
	}

}
