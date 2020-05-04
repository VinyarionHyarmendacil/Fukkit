package vinyarion.fukkit.rpg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public enum StatusEffect {

	STUN, 
	BLEED, 
	LIMB;

	static {
		STUN.verb = "You have been stunned!";
		STUN.ot = (rpg, player, tick) -> {
			if((tick % 20) == 0) {
				player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 63));
				player.addPotionEffect(new PotionEffect(Potion.blindness.id, 20, 63));
			}
		};
		BLEED.verb = "You are bleeding!";
		BLEED.ot = (rpg, player, tick) -> {
			if((tick % 20) == 0) {
				player.attackEntityFrom(Attack.damageBleed, 0.5F);
			}
		};
		LIMB.verb = "Your legs are severely injured!";
		LIMB.ot = (rpg, player, tick) -> {
			if((tick % 20) == 0) {
				player.attackEntityFrom(Attack.damageBleed, 0.5F);
				player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 63));
			}
		};
	}

	private OT ot = null;
	private String verb;

	public String verb() {
		return this.verb;
	}

	private static interface OT {
		public void onTick(FPlayerDataRPG rpg, EntityPlayer player, int tick);
	}

	public void onTick(FPlayerDataRPG rpg, EntityPlayer player, int tick) {
		if(this.ot != null) this.ot.onTick(rpg, player, tick);
	}

	public static final int count = values().length;

}
