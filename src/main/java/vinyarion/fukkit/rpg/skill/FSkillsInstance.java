package vinyarion.fukkit.rpg.skill;

public class FSkillsInstance {

	private FSkillsInstance(FSkill skill, int level) {
		this.skill = skill;
		this.level = level;
	}

	public final FSkill skill;
	public final int level;

	public boolean isEmpty() {
		return this.skill == null || this.level < 0;
	}

	public boolean isNonEmpty() {
		return this.skill != null && this.level >= 0;
	}

	public static FSkillsInstance empty() {
		return new FSkillsInstance(null, -1);
	}

	public static FSkillsInstance of(FSkill skill, int level) {
		return new FSkillsInstance(skill, level);
	}

	public boolean equals(Object object) {
		return object == null ? false : object instanceof FSkillsInstance ? this.equals((FSkillsInstance)object) : false;
	}

	public final boolean equals(FSkillsInstance other) {
		return other == this ? true : (other.skill == this.skill && other.level == this.level);
	}
	
	public int hashCode() {
		return this.skill.hashCode() ^ this.level;
	}

}
