package vinyarion.fukkit.main.cmd;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommand;
import vinyarion.fukkit.sm.cmd.FCommandBlockLogQuerey;
import vinyarion.fukkit.sm.cmd.FCommandChatEMOTE;
import vinyarion.fukkit.sm.cmd.FCommandChatMUTE;
import vinyarion.fukkit.sm.cmd.FCommandChatQUIET;
import vinyarion.fukkit.sm.cmd.FCommandChatRP;
import vinyarion.fukkit.sm.cmd.FCommandChatSHOUT;
import vinyarion.fukkit.sm.cmd.FCommandChatUNMUTE;
import vinyarion.fukkit.sm.cmd.FCommandDOP;
import vinyarion.fukkit.sm.cmd.FCommandDOPList;
import vinyarion.fukkit.sm.cmd.FCommandDeDOP;
import vinyarion.fukkit.sm.cmd.FCommandDiscordBot;
import vinyarion.fukkit.sm.cmd.FCommandDiscordMsg;
import vinyarion.fukkit.sm.cmd.FCommandEChest;
import vinyarion.fukkit.sm.cmd.FCommandFly;
import vinyarion.fukkit.sm.cmd.FCommandGM;
import vinyarion.fukkit.sm.cmd.FCommandGroups;
import vinyarion.fukkit.sm.cmd.FCommandHat;
import vinyarion.fukkit.sm.cmd.FCommandInvis;
import vinyarion.fukkit.sm.cmd.FCommandInvsee;
import vinyarion.fukkit.sm.cmd.FCommandKickall;
import vinyarion.fukkit.sm.cmd.FCommandManageKit;
import vinyarion.fukkit.sm.cmd.FCommandRealname;
import vinyarion.fukkit.sm.cmd.FCommandRespond;
import vinyarion.fukkit.sm.cmd.FCommandSignLine;
import vinyarion.fukkit.sm.cmd.FCommandSpawn;
import vinyarion.fukkit.sm.cmd.FCommandTPA;
import vinyarion.fukkit.sm.cmd.FCommandTPADeny;
import vinyarion.fukkit.sm.cmd.FCommandTPAHere;
import vinyarion.fukkit.sm.cmd.FCommandTPAccept;
import vinyarion.fukkit.sm.cmd.FCommandTempPerms;
import vinyarion.fukkit.sm.cmd.FCommandWarp;
import vinyarion.fukkit.sm.cmd.FCommandWarzone;

public class FCommands {
	
	public static List<ICommand> fukkitCommands = Lists.newArrayList();
	public static List<String> fukkitCommandNames = Lists.newArrayList();
	
	public static ICommand fukkit = new FukkitCommand("f_info");
	public static ICommand fukkit_reloadconfig = new FCommandReloadConfig("f_reloadconfig");
	public static ICommand fukkit_debug = new FCommandDebug("f_debug");
	public static ICommand fukkit_test = new FCommandTest("f_test");
	public static ICommand fukkit_header = new FCommandHeader("f_header");
	
	public static ICommand fukkit_fill = new FCommandFill("f_fill");
	public static ICommand fukkit_ifblockat = new FCommandIfblockat("f_ifblockat");
	public static ICommand fukkit_execute = new FCommandExecute("f_execute");
	public static ICommand fukkit_recipe = new FCommandRecipe("f_recipe");
	public static ICommand fukkit_anvilrecipe = new FCommandAnvilRecipe("f_anvilrecipe");
	public static ICommand fukkit_discordbot = new FCommandDiscordBot("f_discord");
	public static ICommand fukkit_startup = new FCommandStartup("f_startup");
	public static ICommand fukkit_attribute = new FCommandAttribute("f_attribute");
	public static ICommand fukkit_armorseteffects = new FCommandArmorSetEffects("f_armorseteffects");
	public static ICommand fukkit_wand = new FCommandWand("f_wand");
	public static ICommand fukkit_velocity = new FCommandVelocity("f_velocity");
	
	public static ICommand fukkit_tempperms = new FCommandTempPerms("f_tempperms");
	public static ICommand fukkit_perms = new FCommandPerms("f_perms");
	public static ICommand fukkit_discordop = new FCommandDOP("f_d_op");
	public static ICommand fukkit_discorddeop = new FCommandDeDOP("f_d_deop");
	public static ICommand fukkit_discordoplist = new FCommandDOPList("f_d_oplist");
	public static ICommand fukkit_playerdatareset = new FCommandPlayerDataReset("f_playerdata_reset");
	public static ICommand fukkit_playerdatasave = new FCommandPlayerDataSave("f_playerdata_save");
	public static ICommand fukkit_playerdataload = new FCommandPlayerDataLoad("f_playerdata_load");
	public static ICommand fukkit_titles = new FCommandTitles("f_titles");
	public static ICommand fukkit_groups = new FCommandGroups("f_groups");
	public static ICommand fukkit_managekit = new FCommandManageKit("f_kits");
	public static ICommand fukkit_region = new FCommandRegion("f_region");
	public static ICommand fukkit_regioncyl = new FCommandRegionCyl("f_regioncyl");
	public static ICommand fukkit_warzone = new FCommandWarzone("warzone");
	public static ICommand fukkit_blocklogquerey = new FCommandBlockLogQuerey("f_blocklogquerey");
	public static ICommand fukkit_nbt = new FCommandNBT("f_nbt");
	public static ICommand fukkit_script = new FCommandScript("f_script");
	public static ICommand fukkit_ifcondition = new FCommandIfCondition("f_ifcondition");
	public static ICommand fukkit_customitem = new FCommandCustomItem("f_customitem");
	public static ICommand fukkit_signline = new FCommandSignLine("f_signline");
	public static ICommand fukkit_particle = new FCommandParticle("f_particle");
	public static ICommand fukkit_loot = new FCommandLoot("f_loot");
	public static ICommand fukkit_gui = new FCommandGui("f_gui");
	public static ICommand fukkit_exchange = new FCommandExchange("f_exchange");
	public static ICommand fukkit_announce = new FCommandAnnounce("f_announce").withAliases("broadcast", "bc", "tellall");
	
//	public static ICommand fukkit_vanish = new FCommandVanish("vanish");
	public static ICommand fukkit_invis = new FCommandInvis("invis");
	public static ICommand fukkit_invsee = new FCommandInvsee("invsee");
	public static ICommand fukkit_tpo = new FCommandTPO("tpo");
	public static ICommand fukkit_tppos = new FCommandTPPos("tppos");
	public static ICommand fukkit_gm = new FCommandGM("gm");
	public static ICommand fukkit_setheanth = new FCommandHealth("health");
	public static ICommand fukkit_mute = new FCommandChatMUTE("mute");
	public static ICommand fukkit_unmute = new FCommandChatUNMUTE("unmute");
	public static ICommand fukkit_glitch = new FCommandGlitch("glitch");
	public static ICommand fukkit_unglitch = new FCommandUnglitch("unglitch");
	public static ICommand fukkit_kickall = new FCommandKickall("kickall");
	
	public static ICommand fukkit_spawn = new FCommandSpawn("spawn");
	public static ICommand fukkit_enderchest = new FCommandEChest("echest");
	public static ICommand fukkit_warp = new FCommandWarp("warp");
	public static ICommand fukkit_tpa = new FCommandTPA("tpa");
	public static ICommand fukkit_tpahere = new FCommandTPAHere("tpahere");
	public static ICommand fukkit_tpaccept = new FCommandTPAccept("tpaccept");
	public static ICommand fukkit_tpadeny = new FCommandTPADeny("tpadeny");

	public static ICommand fukkit_rp = new FCommandChatRP("roleplay").withAliases("rp");
	public static ICommand fukkit_quiet = new FCommandChatQUIET("quiet").withAliases("q");
	public static ICommand fukkit_shout = new FCommandChatSHOUT("shout").withAliases("s");
	public static ICommand fukkit_emote = new FCommandChatEMOTE("emote").withAliases("em");

	public static ICommand fukkit_respond = new FCommandRespond("respond").withAliases("r");
	public static ICommand fukkit_discordmsg = new FCommandDiscordMsg("dtell").withAliases("dmsg");
	public static ICommand fukkit_fly = new FCommandFly("fly");
	public static ICommand fukkit_nick = new FCommandNick("nick");
	public static ICommand fukkit_realname = new FCommandRealname("realname");
	public static ICommand fukkit_item = new FCommandHeldItem("item");
	public static ICommand fukkit_hat = new FCommandHat("hat");
	public static ICommand fukkit_kit = new FCommandKit("kit");
	public static ICommand fukkit_seen = new FCommandSeen("seen");
	//public static ICommand fukkit_mount = new FCommandMount("mount").withAliases("ride");
	
	public static ICommand rpg_skillpoints = new FCommandRPGSkillPoint("frpg_skillpoints");
	public static ICommand rpg_useskill1 = new FCommandRPGUseSkill("frpg_useskill1", 0);
	public static ICommand rpg_useskill2 = new FCommandRPGUseSkill("frpg_useskill2", 1);
	public static ICommand rpg_useskill3 = new FCommandRPGUseSkill("frpg_useskill3", 2);
	public static ICommand rpg_gui = new FCommandRPGGui("frpg_gui").withAliases("rpg", "menu");
	public static ICommand rpg_manageblock = new FCommandRPGManageBlock("frpg_manageblock");
	public static ICommand rpg_setrace = new FCommandRPGSetRace("frpg_setrace");
	public static ICommand rpg_setclass = new FCommandRPGSetClass("frpg_setclass");
	public static ICommand rpg_setfaction = new FCommandRPGSetFaction("frpg_setfaction");
	public static ICommand rpg_factionleaders = new FCommandRPGFactionLeaders("frpg_factionleaders");
	
	public static void registerServerCommands(FMLServerStartingEvent event) {
		for(Field field : FCommands.class.getDeclaredFields()) {
			try {
				field.setAccessible(true);
				ICommand command = (ICommand)field.get(null);
				fukkitCommands.add(command);
				event.registerServerCommand(command);
			} catch(Exception e) {
			}
		}
	}
	
}
