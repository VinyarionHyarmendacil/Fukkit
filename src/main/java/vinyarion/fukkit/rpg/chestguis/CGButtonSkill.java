package vinyarion.fukkit.rpg.chestguis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import vinyarion.fukkit.main.chestguis.CGButton;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.FGuis;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.skill.FSkill;
import vinyarion.fukkit.rpg.skill.FSkill.Type;

public class CGButtonSkill implements CGButton {

	public CGButtonSkill(FSkill skill, int level) {
		this.skill = skill;
		this.level = level;
	}

	private FSkill skill;
	private int level;

	public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
		FPlayerData pd = FPlayerData.forPlayer(player);
		FPlayerDataRPG ps = FPlayerDataRPG.of(player);
		if(!(right && isShift)) return;
		if(this.skill.isLearnedBy(player, this.level)) {
			if(this.skill.isSelected(player, this.level)) {
				this.skill.deselect(player, this.level);
				gui.refresh(player);
				FGuis.send(player, "&aDeslected " + this.skill.name + ".");
			} else if(this.skill.canSelect(player, this.level)) {
				this.skill.select(player, this.level);
				gui.refresh(player);
				FGuis.send(player, "&aSelected " + this.skill.name + " at level " + (this.level + 1) + ".");
			} else {
				FGuis.send(player, "&cYou can not select " + this.skill.name + ".");
			}
		} else if(this.skill.reqSkillPoints(player, this.level) <= ps.skillPoints) {
			if(this.skill.isPrevLearnedIfApplicable(player, this.level)) {
				if(this.skill.reqFulfilled(player, this.level)) {
					this.skill.learn(player, this.level);
					gui.refresh(player);
					FGuis.send(player, "&aYou learned " + this.skill.name + " at level " + (this.level + 1) + ".");
				} else {
					FGuis.send(player, "&cYou must fulfill the requirements for " + this.skill.name + " first.");
				}
			} else {
				FGuis.send(player, "&cYou must learn previous levels of " + this.skill.name + " first, level " + this.level + ".");
			}
		} else {
			FGuis.send(player, "&cYou do not have enough skill points to learn " + this.skill.name + ".");
		}
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		ItemStack ret = this.skill.display.copy();
		ret.stackSize = this.level + 1;
		ret.setStackDisplayName(Colors.RESET + Colors.BOLD + this.skill.name);
		NBTTagList list = NBT.ensureList(NBT.ensure(ret, "display"), "Lore");
		boolean learned = this.skill.isLearnedBy(player, this.level);
		boolean prevLearned = this.skill.isPrevLearnedIfApplicable(player, this.level);
		boolean reqFulfilled = this.skill.reqFulfilled(player, this.level);
		boolean selected = this.skill.isSelected(player, this.level);
		int reqpts = this.skill.reqSkillPoints(player, this.level);
		int haspts = FPlayerDataRPG.of(player).skillPoints;
		if(learned) {
			if(selected) {
				list.appendTag(new NBTTagString(Colors.color("&r&b&oShift right click to deselect")));
			} else {
				list.appendTag(new NBTTagString(Colors.color("&r&b&oShift right click to select")));
			}
		} else {
			if(prevLearned) {
				if(reqFulfilled) {
					if(haspts >= reqpts) {
						list.appendTag(new NBTTagString(Colors.color("&r&b&oShift right click to purchase")));
					} else {
						list.appendTag(new NBTTagString(Colors.color("&r&b&oYou don't have enough Skill Points")));
					}
				} else {
					list.appendTag(new NBTTagString(Colors.color("&r&b&oShift right click to purchase")));
				}
			} else {
				list.appendTag(new NBTTagString(Colors.color("&r&b&oYou must learn level " + this.level + " first")));
			}
			list.appendTag(new NBTTagString(Colors.color("&r&b&oRequires " + reqpts + " Skill Points to learn")));
		}
		list.appendTag(new NBTTagString(Colors.color("&r&lMax cooldown is "+Time.readableTicks(this.skill.cooldowns[this.level]))));
		for(String line : this.skill.allinfopre) {
			list.appendTag(new NBTTagString(Colors.color(line)));
		}
		for(String line : this.skill.info[this.level]) {
			list.appendTag(new NBTTagString((this.skill.indentInfo ? "    " : "") + Colors.color(line)));
		}
		for(String line : this.skill.allinfo) {
			list.appendTag(new NBTTagString(Colors.color(line)));
		}
		if(selected) {
			NBT.ensureList(ret.getTagCompound(), "ench");
		}
		if(ret.getItem() == null) {
			player.addChatMessage(Colors.make("Error! item for " + skill.name + " was null!"));
			ret.func_150996_a(Items.stick);
		}
		return ret;
	}

}
