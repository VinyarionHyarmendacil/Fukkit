package vinyarion.fukkit.rpg.skill;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class FSkillActiveSimple extends FSkillActive {

	public FSkillActiveSimple(String name, Type type, Category category, int levels, ItemStack display, String... info) {
		super(name, type, category, levels, display, info);
	}

	public Void applyFunction(EntityPlayer player, int level, Void parameter) {
		return null;
	}

}
