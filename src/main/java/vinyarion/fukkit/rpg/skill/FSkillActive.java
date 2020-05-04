package vinyarion.fukkit.rpg.skill;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class FSkillActive<Return, Parameter> extends FSkill {

	public FSkillActive(String name, Type type, Category category, int levels, ItemStack display, String[] info) {
		super(name, type, category, levels, display, info);
	}

	public boolean reqFulfilled(EntityPlayer player, int level) {
		return true;
	}

}
