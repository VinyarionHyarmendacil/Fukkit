package vinyarion.fukkit.rpg.chestguis;

import java.util.function.Function;
import java.util.stream.Collectors;

import lotr.common.LOTRMod;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import vinyarion.fukkit.main.chestguis.CGButton;
import vinyarion.fukkit.main.chestguis.CGButtonAction;
import vinyarion.fukkit.main.chestguis.CGButtonBack;
import vinyarion.fukkit.main.chestguis.CGButtonChild;
import vinyarion.fukkit.main.chestguis.CGButtonInfo;
import vinyarion.fukkit.main.chestguis.CGButtonSelect;
import vinyarion.fukkit.main.chestguis.CGButtonToggle;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.ChestGuiPerm;
import vinyarion.fukkit.main.chestguis.FGuis;
import vinyarion.fukkit.main.chestguis.CGButtonBack.Exit;
import vinyarion.fukkit.main.data.FChestGuis;
import vinyarion.fukkit.main.game.FPlayer;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.archetypes.Stat;
import vinyarion.fukkit.rpg.skill.FSkill;
import vinyarion.fukkit.rpg.skill.FSkills;
import vinyarion.fukkit.rpg.tileentity.FTileEntityData;

public class FRPGGuis extends FGuis {

	public static final CGButton stats = child("system/rpg/stats", name(Items.filled_map, "&r&lStatistics"));
	public static final CGButton clazz = child("system/rpg/class", name(LOTRMod.modTemplate, "&r&lClasses"));
	public static final CGButton skills = child("system/rpg/skills", name(Items.golden_sword, "&r&lSkills"));
	public static final CGButton options = child("system/rpg/options", name(LOTRMod.gateGear, "&r&lOptions"));

	public static final CGButton swords = child("system/rpg/skills/sword1", name(Items.iron_sword, "&r&lSword skills"));
	public static final CGButton axes = child("system/rpg/skills/axe1", name(Items.iron_axe, "&r&lAxe skills"));
	public static final CGButton daggers = child("system/rpg/skills/dagger1", name(LOTRMod.daggerIron, "&r&lDagger skills"));
	public static final CGButton spears = child("system/rpg/skills/spear1", name(LOTRMod.spearIron, "&r&lSpear skills"));
	public static final CGButton pikes = child("system/rpg/skills/pike1", name(LOTRMod.pikeIron, "&r&lPike skills"));
	public static final CGButton hammers = child("system/rpg/skills/hammer1", name(LOTRMod.hammerGondor, "&r&lHammer skills"));
	public static final CGButton crossbows = child("system/rpg/skills/crossbow1", name(LOTRMod.ironCrossbow, "&r&lCrossbow skills"));
	public static final CGButton bows = child("system/rpg/skills/bow1", name(Items.bow, "&r&lBow skills"));
	public static final CGButton throwingaxes = child("system/rpg/skills/throwingaxe1", name(LOTRMod.throwingAxeIron, "&r&lThrowing Axe skills"));
	public static final CGButton slings = child("system/rpg/skills/sling1", name(LOTRMod.sling, "&r&lSlingshot skills"));
	public static final CGButton improvs = child("system/rpg/skills/improv1", name(Items.iron_hoe, "&r&lImprovised weapon skills"));
	public static final CGButton unarmed = child("system/rpg/skills/unarmed1", meta(LOTRMod.dye, 5, "&r&lUnarmed skills"));
	public static final CGButton armors = child("system/rpg/skills/armor1", name(Items.iron_chestplate, "&r&lArmor skills"));

	public static final CGButton logins = new CGButtonInfo((player, gui) -> name(Blocks.log, "&r&lLogins:", "&r&l"+FPlayerData.forPlayer(player).tag().getInteger("loginCount")+" logins"));
	public static final CGButton ticks = new CGButtonInfo((player, gui) -> name(Items.clock, "&r&lTime played:", "&r&l"+Time.readable(FPlayerData.forPlayer(player).ticksTotal()/20)));

	public static final CGButtonToggle activationShortcut = new CGButtonToggle("&r&lUse shortcuts", "rpg_skills_activationshortcut", name(Items.fishing_rod, "&r&lUse shortcuts", "&r&b&oShift + Scroll to select active skill", "&r&b&oShift + Left click to use active skill"));
	public static final CGButtonToggle showScoreboard = new CGButtonToggle("&r&lShow Scoreboard", "rpg_skills_showscoreboard", name(Items.sign, "&r&lShow Scoreboard", "&r&b&oShows active skills in the scoreboard"));
	public static final CGButtonToggle inWorldNotify = new CGButtonToggle("&r&lUse in-world notifications", "rpg_skills_inworldnotify", name(Items.sign, "&r&lUse in-world notifications", "&r&b&oUses pop-up entities to display information"));

	public static final CGButton addPlayer = child("system/rpg/teowner/add", name(Items.emerald, "&r&lAdd a player"));
	public static final CGButton removePlayer = child("system/rpg/teowner/remove", name(LOTRMod.ruby, "&r&lRemove a player"));
	public static final CGButton listPlayer = new CGButtonInfo((player, gui) -> name(LOTRMod.modTemplate, "&r&lListed players:", Misc.make(() -> {
		FTileEntityData ted = FPlayerDataRPG.of(player).curentTileEntityData;
		if(ted != null) {
			return ted.users.stream().map(s -> Colors.RESET+"  "+s).collect(Collectors.toList()).toArray(new String[0]);
		} else {
			return new String[0];
		}
	})));

	public static final CGButton confirmAdd = new CGButtonAction(name(Items.book, "&r&lConfirm")).with(state -> {
		if(state.left) {
			FTileEntityData ted = FPlayerDataRPG.of(state.player).curentTileEntityData;
			String pt = state.gui.getPouchText(state.player);
			if(ted != null) {
				boolean contains = false;
				for(String n : ted.users) {
					if(n.equalsIgnoreCase(pt)) {
						contains = true;
						break;
					}
				}
				if(contains) {
					state.player.addChatMessage(Colors.chat("Player already added: "+pt));
				} else {
					ted.users.add(pt);
					state.player.addChatMessage(Colors.chat("Added player: "+pt));
				}
			} else {
				state.player.addChatMessage(Colors.chat("Not editing a tile entity!"));
			}
		}
	});
	public static final CGButton confirmRemove = new CGButtonAction(name(Items.book, "&r&lConfirm")).with(state -> {
		if(state.left) {
			FTileEntityData ted = FPlayerDataRPG.of(state.player).curentTileEntityData;
			String pt = state.gui.getPouchText(state.player);
			if(ted != null) {
				String val = null;
				boolean contains = false;
				for(String n : ted.users) {
					if(n.equalsIgnoreCase(pt)) {
						contains = true;
						val = n;
						break;
					}
				}
				if(!contains) {
					state.player.addChatMessage(Colors.chat("Player not present: "+pt));
				} else {
					ted.users.remove(val);
					state.player.addChatMessage(Colors.chat("Removed player: "+pt));
				}
			} else {
				state.player.addChatMessage(Colors.chat("Not editing a tile entity!"));
			}
		}
	});
	public static final CGButton whiteOrBlackList = new CGButtonSelect("&r&lBlacklist mode", player -> name(Items.coal, "&r&lBlacklist mode", "If selected, the supplied","playerlist acts as a blacklist.")) {
		public ItemStack display(EntityPlayer player, ChestGui gui) {
			ItemStack ret = super.display(player, gui);
			if(this.isSelected(player, gui)) {
				NBT.ensureList(ret.getTagCompound(), "ench");
			}
			return ret;
		}
		public boolean isSelected(EntityPlayer player, ChestGui gui) {
			FTileEntityData ted = FPlayerDataRPG.of(player).curentTileEntityData;
			return ted == null ? false : ted.isBlacklist;
		}
		public boolean canSelect(EntityPlayer player, ChestGui gui) {
			FTileEntityData ted = FPlayerDataRPG.of(player).curentTileEntityData;
			return ted == null ? false : !ted.isBlacklist;
		}
		public boolean canDeselect(EntityPlayer player, ChestGui gui) {
			FTileEntityData ted = FPlayerDataRPG.of(player).curentTileEntityData;
			return ted == null ? false : ted.isBlacklist;
		}
		public void onSelect(EntityPlayer player, ChestGui gui) {
			FTileEntityData ted = FPlayerDataRPG.of(player).curentTileEntityData;
			if(ted != null) {
				ted.isBlacklist = true;
				gui.refresh(player);
			}
		}
		public void onDeselect(EntityPlayer player, ChestGui gui) {
			FTileEntityData ted = FPlayerDataRPG.of(player).curentTileEntityData;
			if(ted != null) {
				ted.isBlacklist = false;
				gui.refresh(player);
			}
		}
	};

	public static final CGButton statInfoRace = new CGButtonInfo((player, gui)->FPlayerDataRPG.of(player).stats.race.stack(player, gui));
	public static final CGButton statInfoClass = new CGButtonInfo((player, gui)->FPlayerDataRPG.of(player).stats.fclass.stack(player, gui));
	public static final CGButton statInfoFaction = new CGButtonInfo((player, gui)->FPlayerDataRPG.of(player).stats.faction.stack(player, gui));

	public static void init() {
		FChestGuis guis = FChestGuis.instance();
		guis.register("system/rpg/main", new ChestGuiPerm("&8&lRPG Main menu", 6, 
			exit(), null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, stats, clazz, skills, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, options
		));
		guis.register("system/rpg/stats", new ChestGuiPerm("&8&lStatistics", "system/rpg/main", 6, 
			back(), null, null, null, null, null, null, null, logins, 
			null, ss(FSkill.Type.Active, 0), ss(FSkill.Type.Passive, 0), null, st(Stat.Type.STRENGTH), null, null, null, ticks, 
			null, ss(FSkill.Type.Active, 1), ss(FSkill.Type.Passive, 1), null, st(Stat.Type.DEXTERITY), null, statInfoRace, null, null, 
			null, ss(FSkill.Type.Active, 2), ss(FSkill.Type.Passive, 2), null, st(Stat.Type.CONSTITUTION), null, statInfoClass, null, null, 
			null, ss(FSkill.Type.Active, 3), ss(FSkill.Type.Passive, 3), null, st(Stat.Type.WISDOM), null, statInfoFaction, null, null, 
			null, ss(FSkill.Type.Active, 4), ss(FSkill.Type.Passive, 4), null, st(Stat.Type.LUCK), null, null, null, null
		));
		guis.register("system/rpg/class", new ChestGuiPerm("&8&lClasses", "system/rpg/main", 6, 
			back(), null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null
		));
		guis.register("system/rpg/skills", new ChestGuiPerm("&8&lSkill Trees", "system/rpg/main", 6, 
			back(), null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, swords, axes, daggers, spears, pikes, null, null, 
			null, null, hammers, crossbows, bows, throwingaxes, slings, null, null, 
			null, null, improvs, unarmed, armors, null, null, null, null, 
			null, null, null, null, null, null, null, null, null
		));
		guis.register("system/rpg/options", new ChestGuiPerm("&8&lOptions", "system/rpg/main", 6, 
			back(), null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, activationShortcut, null, null, null, null, null, null, 
			null, null, showScoreboard, null, null, null, null, null, null, 
			null, null, inWorldNotify, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null
		));
		guis.register("system/rpg/skills/sword1", new SkillsetGui("&8&lSword Skill Tree", "system/rpg/skills", "system/rpg/skills/sword", 0, 2, 
			FSkills.swordPassiveDamage, FSkills.swordPassiveParry, FSkills.swordActiveDisarm, FSkills.swordActivePiercelunge, FSkills.swordActiveFinalblow
		));
		guis.register("system/rpg/skills/sword2", new SkillsetGui("&8&lSword Skill Tree", "system/rpg/skills", "system/rpg/skills/sword", 1, 2, 
			FSkills.swordActiveStunningflat, FSkills.swordActiveQuickstrike, FSkills.swordActiveSplinterguard
		));
		guis.register("system/rpg/skills/axe1", new SkillsetGui("&8&lAxe Skill Tree", "system/rpg/skills", "system/rpg/skills/axe", 0, 1, 
			FSkills.axePassiveDamage, FSkills.axeActiveGougingcut, FSkills.axeActiveStunningblow, FSkills.axeActiveArmorbreaker, FSkills.axeActiveHeadsplitter
		));
		guis.register("system/rpg/skills/dagger1", new SkillsetGui("&8&lDagger Skill Tree", "system/rpg/skills", "system/rpg/skills/dagger", 0, 1, 
			FSkills.daggerPassiveDamage, FSkills.daggerActiveSupriseattack, FSkills.daggerActiveQuickslash
		));
		guis.register("system/rpg/skills/spear1", new SkillsetGui("&8&lSpear Skill Tree", "system/rpg/skills", "system/rpg/skills/spear", 0, 1, 
			FSkills.spearPassiveDamage
		));
		guis.register("system/rpg/skills/pike1", new SkillsetGui("&8&lPike Skill Tree", "system/rpg/skills", "system/rpg/skills/pike", 0, 1, 
			FSkills.pikePassiveDamage
		));
		guis.register("system/rpg/skills/hammer1", new SkillsetGui("&8&lHammer and Mace Skill Tree", "system/rpg/skills", "system/rpg/skills/hammer", 0, 2, 
			FSkills.hammerPassiveDamage, FSkills.hammerPassiveArmorobliterator, FSkills.hammerActiveWideswing, FSkills.hammerActiveHeavyblow, FSkills.hammerActiveDebilitatingblow
		));
		guis.register("system/rpg/skills/hammer2", new SkillsetGui("&8&lHammer and Mace Skill Tree", "system/rpg/skills", "system/rpg/skills/hammer", 1, 2, 
			FSkills.hammerActiveFallinganvil
		));
		guis.register("system/rpg/skills/crossbow1", new SkillsetGui("&8&lCrossbow Skill Tree", "system/rpg/skills", "system/rpg/skills/crossbow", 0, 1, 
			FSkills.crossbowPassiveDamage, FSkills.crossbowActiveArmorpiercer, FSkills.crossbowActiveBluntshot
		));
		guis.register("system/rpg/skills/bow1", new SkillsetGui("&8&lBow Skill Tree", "system/rpg/skills", "system/rpg/skills/bow", 0, 2, 
			FSkills.bowPassiveDamage, FSkills.bowPassiveDeadshot, FSkills.bowPassiveLongshot, FSkills.bowActiveUnsteadyfingers, FSkills.bowActiveStunningshot
		));
		guis.register("system/rpg/skills/bow2", new SkillsetGui("&8&lBow Skill Tree", "system/rpg/skills", "system/rpg/skills/bow", 1, 2, 
			FSkills.bowActiveKnee
		));
		guis.register("system/rpg/skills/throwingaxe1", new SkillsetGui("&8&lThrowing Axe Skill Tree", "system/rpg/skills", "system/rpg/skills/throwingaxe", 0, 1, 
			FSkills.throwingaxePassiveDamage, FSkills.throwingaxePassiveMeelee
		));
		guis.register("system/rpg/skills/sling1", new SkillsetGui("&8&lSling Skill Tree", "system/rpg/skills", "system/rpg/skills/sling", 0, 1, 
			FSkills.slingPassiveDamage, FSkills.slingActiveBetweeneyes, FSkills.slingActiveKnockoutshot, FSkills.slingActiveVolley
		));
		guis.register("system/rpg/skills/improv1", new SkillsetGui("&8&lImprovised Weapon Skill Tree", "system/rpg/skills", "system/rpg/skills/improv", 0, 2, 
			FSkills.improvPassiveBakerbludgeon, FSkills.improvPassiveArrowmeelee, FSkills.improvPassiveThrowplate, FSkills.improvActiveElementalplate, FSkills.improvActivePiercingpick
		));
		guis.register("system/rpg/skills/improv2", new SkillsetGui("&8&lImprovised Weapon Skill Tree", "system/rpg/skills", "system/rpg/skills/improv", 1, 2, 
			FSkills.improvActiveArmorshatter, FSkills.improvActiveFirebrand
		));
		guis.register("system/rpg/skills/unarmed1", new SkillsetGui("&8&lUnarmed Combat Skill Tree", "system/rpg/skills", "system/rpg/skills/unarmed", 0, 1, 
			FSkills.unarmedPassiveDamage, FSkills.unarmedPassivePunch, FSkills.unarmedActiveSeizure, FSkills.unarmedActiveChokehold
		));
		guis.register("system/rpg/skills/armor1", new SkillsetGui("&8&lArmor Skill Tree", "system/rpg/skills", "system/rpg/skills/armor", 0, 1, 
			FSkills.armorPassiveDamagedeflect, FSkills.armorActiveRigidstance
		));
		guis.register("system/rpg/teowner", new ChestGuiPerm("&8&lManage Tile Entity", 6, 
			exit(), null, null, null, null, null, null, null, null, 
			null, null, addPlayer, null, listPlayer, null, removePlayer, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, null, null, whiteOrBlackList, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null
		));
		guis.register("system/rpg/teowner/add", new PouchGuiPerm("&8&lAdd Player", "system/rpg/teowner", 
			back(), null, null, null, null, null, null, null, null, 
			null, null, confirmAdd, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null
		).setDefaultText("Name"));
		guis.register("system/rpg/teowner/remove", new PouchGuiPerm("&8&lRemove Player", "system/rpg/teowner", 
			back(), null, null, null, null, null, null, null, null, 
			null, null, confirmRemove, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null
		).setDefaultText("Name"));
	}

	private static CGButton ss(FSkill.Type type, int slot) {
		return new CGButtonSelectedSkill(type, slot);
	}

	private static CGButton st(Stat.Type type) {
		return new CGButtonInfo((player, gui)->type.stack(player, gui));
	}

}
