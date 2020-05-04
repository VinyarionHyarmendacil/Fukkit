package vinyarion.fukkit.rpg.skill;

import java.util.function.Function;

import lotr.common.LOTRMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import vinyarion.fukkit.main.game.FPlayer;
import vinyarion.fukkit.rpg.Attack;

public class FSkillPassiveDamage extends FSkillPassive {

	public FSkillPassiveDamage(String name, Type type, Category category, int levels, ItemStack display, String... info) {
		super(name, type, category, levels, display, info);
		this.setOnAttack((a, t, d) -> {
			FSkillsHandler handler = FSkillsHandler.current.get();
			if(percent) {
				handler.pct += ((100 + d.level) * handler.damage / 100);
			} else {
				handler.abs += ((float)d.level + 1);
			}
			ItemStack held = a.getHeldItem();
			if(held != null && (held.getItem() == Items.arrow || held.getItem() == LOTRMod.arrowPoisoned)) {
				if(a.getRNG().nextInt(4) >= d.level) {
					FPlayer.decrementHeld(a, 1);
				}
				if(held.getItem() == LOTRMod.arrowPoisoned) {
					t.addPotionEffect(new PotionEffect(Potion.poison.id, 45, 0));
				}
			}
			return false;
		});
	}

	public FSkillPassiveDamage setType(Attack attack) {
		this.attack = attack;
		return this;
	}

	public FSkillPassiveDamage setNonPercent() {
		this.percent = false;
		return this;
	}

	private boolean percent = true;
	private Attack attack = Attack.DIR_MELEE;

	public boolean isPercent() {
		return percent;
	}

	public boolean isMelee() {
		return this.attack == Attack.DIR_MELEE;
	}

	public boolean isRanged() {
		return this.attack == Attack.IDR_RANGED;
	}

	public boolean isMagic() {
		return this.attack == Attack.IDR_MAGIC;
	}

}
