package vinyarion.fukkit.rpg.chestguis;

import java.util.Map.Entry;

import lotr.common.LOTRMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import vinyarion.fukkit.main.chestguis.CGButton;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.FGuis;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.skill.FSkill;
import vinyarion.fukkit.rpg.skill.FSkill.Type;

public class CGButtonSelectedSkill implements CGButton {

	public CGButtonSelectedSkill(Type type, int slot) {
		this.type = type;
		this.slot = slot;
	}

	public Type type;
	public int slot;

	public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
//		FGuis.send(player, ("slot: " + slot + " ") + (left?"l ":"- ") + (right?"r ":"- ") + (middle?"m ":"- ") + (isShift?"s":"-"));
		FPlayerDataRPG ps = FPlayerDataRPG.of(player);
		int level = -1;
		FSkill skill = null;
		if(!(right && isShift)) return;
		if(this.type == Type.Active) {
			if(ps.activeMax > this.slot) {
				if(ps.selectedActiveSkills.size() > this.slot) {
					skill = ((Entry<FSkill, Integer>)ps.selectedActiveSkills.entrySet().toArray()[this.slot]).getKey();
					level = skill.getInstance(player, level);
					if(skill.isSelected(player, level)) {
						skill.deselect(player, level);
						FGuis.send(player, "&aDeslected " + skill.name + ".");
						gui.refresh(player);
					}
				} else {
					FGuis.send(player, "&cYou can not deselect an empty skill slot.");
				}
			} else {
				FGuis.send(player, "&cYou can not deselect an empty skill slot.");
			}
		} else if(this.type == Type.Passive) {
			if(ps.passiveMax > this.slot) {
				if(ps.selectedPassiveSkills.size() > this.slot) {
					skill = ((Entry<FSkill, Integer>)ps.selectedPassiveSkills.entrySet().toArray()[this.slot]).getKey();
					level = skill.getInstance(player, level);
					if(skill.isSelected(player, level)) {
						skill.deselect(player, level);
						FGuis.send(player, "&aDeslected " + skill.name + ".");
						gui.refresh(player);
					}
				} else {
					FGuis.send(player, "&cYou can not deselect an empty skill slot.");
				}
			} else {
				FGuis.send(player, "&cYou can not deselect an empty skill slot.");
			}
		}
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		FPlayerDataRPG ps = FPlayerDataRPG.of(player);
		ItemStack ret = FRPGGuis.name(Blocks.stone, "NULL");
		int level = -1;
		FSkill skill = null;
		if(this.type == Type.Active) {
			if(ps.activeMax > this.slot) {
				if(ps.selectedActiveSkills.size() > this.slot) {
					ret = (skill = (FSkill)((Entry<FSkill, Integer>)ps.selectedActiveSkills.entrySet().toArray()[this.slot]).getKey()).display.copy();
					level = skill.getInstance(player, level);
				} else {
					ret = FRPGGuis.name(Items.clay_ball, "&rEmpty active slot", "&r&7Go to the skill trees", "&r&7and select an active skill.");
				}
			} else {
				ret = FRPGGuis.name(LOTRMod.redClayBall, "&r&8Unavailable active slot", "&r&8You can unlock this slot later");
			}
		} else if(this.type == Type.Passive) {
			if(ps.passiveMax > this.slot) {
				if(ps.selectedPassiveSkills.size() > this.slot) {
					ret = (skill = (FSkill)((Entry<FSkill, Integer>)ps.selectedPassiveSkills.entrySet().toArray()[this.slot]).getKey()).display.copy();
					level = skill.getInstance(player, level);
				} else {
					ret = FRPGGuis.name(Items.clay_ball, "&rEmpty passive slot", "&r&7Go to the skill trees", "&r&7and select a passive skill.");
				}
			} else {
				ret = FRPGGuis.name(LOTRMod.redClayBall, "&r&8Unavailable passive slot", "&r&8You can unlock this slot later");
			}
		}
		if(skill == null || level == -1) {
			return ret;
		}
		ret.stackSize = level + 1;
		ret.setStackDisplayName(Colors.RESET + Colors.BOLD + skill.name);
		NBTTagList list = NBT.ensureList(NBT.ensure(ret, "display"), "Lore");
		list.appendTag(new NBTTagString(Colors.color("&r&oShift right click to deselect skill")));
		list.appendTag(new NBTTagString(Colors.color("&r&lMax cooldown is "+Time.readableTicks(skill.cooldowns[level]))));
		int cool = ps.skillCooldowns.get(skill).get();
		if(cool <= 0 && skill.type != Type.Active) {
			list.appendTag(new NBTTagString(Colors.color("&rThis skill is available!")));
		} else {
			list.appendTag(new NBTTagString(Colors.color("&rCurrent cooldown is "+Time.readableTicks(cool))));
		}
		for(String line : skill.allinfopre) {
			list.appendTag(new NBTTagString(Colors.color(line)));
		}
		for(String line : skill.info[level]) {
			list.appendTag(new NBTTagString((skill.indentInfo ? "    " : "") + Colors.color(line)));
		}
		for(String line : skill.allinfo) {
			list.appendTag(new NBTTagString(Colors.color(line)));
		}
		return ret;
	}

}
