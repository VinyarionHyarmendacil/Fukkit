package vinyarion.fukkit.rpg.skill;

import net.minecraft.entity.EntityLivingBase;

public class FSkillsInstanceActive {

	public FSkill skill;
	public int level;
	public int ticksLeft;
	public int ticksSince = 0;

	public FSkillsInstanceActive(FSkill skill, int level, int ticks) {
		this.skill = skill;
		this.level = level;
		this.ticksLeft = ticks;
	}

	public EntityLivingBase firstTarget = null;
	public EntityLivingBase lastTarget = null;
	public EntityLivingBase firstAgressor = null;
	public EntityLivingBase lastAgressor = null;

}
