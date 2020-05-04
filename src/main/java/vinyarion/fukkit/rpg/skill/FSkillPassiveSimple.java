package vinyarion.fukkit.rpg.skill;

import lotr.common.LOTRMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class FSkillPassiveSimple extends FSkillPassive {

	public FSkillPassiveSimple(String name, Type type, Category category, int levels, ItemStack display, String... info) {
		super(name, type, category, levels, display, info);
	}

}
