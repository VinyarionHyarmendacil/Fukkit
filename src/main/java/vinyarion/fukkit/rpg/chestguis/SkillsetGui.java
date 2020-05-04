package vinyarion.fukkit.rpg.chestguis;

import lotr.common.LOTRMod;
import net.minecraft.init.Items;
import vinyarion.fukkit.main.chestguis.CGButton;
import vinyarion.fukkit.main.chestguis.CGButtonBack;
import vinyarion.fukkit.main.chestguis.CGButtonChild;
import vinyarion.fukkit.main.chestguis.CGButtonInfo;
import vinyarion.fukkit.main.chestguis.ChestGuiPerm;
import vinyarion.fukkit.main.chestguis.FGuis;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.skill.FSkill;

public class SkillsetGui extends ChestGuiPerm {

	public SkillsetGui(String display, String parent, String base, int page, int pages, FSkill... skills) {
		super(display, parent, 6, convert(base, page, pages, skills));
		this.page = page;
		this.pages = pages;
	}

	private String base;
	private int page;
	private int pages;

	private static CGButton[] convert(String base, int page, int pages, FSkill[] skills) {
		CGButton[][] ret = new CGButton[6][9]; // y, x
		ret[0][0] = new CGButtonBack();
		ret[0][8] = new CGButtonInfo((player, gui) -> {
			return FGuis.meta(LOTRMod.silverCoin, 2, "&r&oSkill Points", "&r", "&rYou have &6" + String.valueOf(
				FPlayerDataRPG.of(player).skillPoints
			) + "&r Skill Points", "&r");
		});
		if(page != 0) {
			ret[5][0] = new CGButtonChild(base + String.valueOf(page), FGuis.size(Items.paper, page, "&rPrevious page"));
		}
		if(page != pages - 1) {
			ret[5][8] = new CGButtonChild(base + String.valueOf(page + 2), FGuis.size(Items.paper, page, "&rNext page"));
		}
		for(int i = 0; i < Math.min(5, skills.length); i++) {
			FSkill skill = skills[i];
			for(int j = 0; j < Math.min(5, skill.levels); j++) {
				ret[1+j][2+i] = new CGButtonSkill(skill, j);
			}
		}
		return Misc.concat(ret);
	}

}
