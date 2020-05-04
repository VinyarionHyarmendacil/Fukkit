package vinyarion.fukkit.rpg;

import net.minecraft.util.DamageSource;

public enum Attack {

	UNK_UNKNOWN, 
	DIR_MELEE, 
	IDR_RANGED, 
	IDR_MAGIC;

	public static Attack of(DamageSource source) {
		if(source.isProjectile()) return IDR_RANGED;
		if(source.getEntity() == source.getSourceOfDamage()) return DIR_MELEE;
		if(source.isMagicDamage()) return IDR_MAGIC;
		return UNK_UNKNOWN;
	}

	public static DamageSource damageBleed = new DamageSource("bleed")
		.setDamageBypassesArmor()
		.setDamageIsAbsolute();

}
