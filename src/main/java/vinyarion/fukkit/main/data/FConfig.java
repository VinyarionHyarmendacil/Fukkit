package vinyarion.fukkit.main.data;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class FConfig extends Configuration {
	
	public FConfig(File file) {
		super(file);
		this.load();
		this.set();
		this.save();
	}
	
	public void set() {
		this.doFirstWelcomeMessages = this.getBoolean("Do first welcome messages", "general", true, "Enables/disables first welcome messages in chat.");
		this.doWelcomeMessages = this.getBoolean("Do welcome messages", "general", true, "Enables/disables welcome messages in chat.");
		this.doNotifyStart = this.getBoolean("Do server start message", "general", true, "Enables/disables server start message in chat.");
		this.doConsoleJoin = this.getBoolean("Do join log in Discord console", "general", true, "Enables/disables the join messages in the Discord console.");
		this.doConsoleStartup = this.getBoolean("Do startup commands in Discord console", "general", true, "Enables/disables startup commands in the Discord console.\nCommands will execute regardles of thie setting.");
		this.doConsoleBotStart = this.getBoolean("Do the Bot's starting messages Discord console", "general", true, "Enables/disables the bot's startup output showing in the Discord console.");
		this.renameFiles = this.getBoolean("Rename only this mod's data files", "general", true, "Allows the mod to automatically rename its own files.");
		this.makeSystemScripts = this.getBoolean("Make empty system scripts", "general", true, "Allows the mod to automatically generate system scripts.");
		this.tpVoidPeople = this.getBoolean("Teleport people in the void to spawn", "general", true, "Detects if someone is falling in the void, then teleports such people to spawn.");
		this.isolateRPChat = this.getBoolean("Seperate global and RP chat", "general", true, "Filters out the clobal chat for players who are in RP chat.");
		this.ditchScoreboardColors = this.getBoolean("Ignore scoreboard team colors in chat", "general", true, "Stops player's names from being colored by Scoreboard teams.");
		this.doRPG_combo = this.getBoolean("Enable natural combo attacks", "general", false, "Measured as damage x 1.05^(combo-2), where combo is at least 3.");
		this.doTitles = this.getBoolean("Enable LotR Mod's Titles in chat", "general", true, "Players messages display the selected title.");
		this.doWebhooks = this.getBoolean("Use webhooks for Discord chat", "general", true, "Discord messages will seem more from the player.");
		this.onlyCustomWaypoints = this.getBoolean("Custom Waypoints only", "general", false, "Only allow players to use custom waypoints, not map waypoints.");
	}
	
	public boolean doFirstWelcomeMessages;
	public boolean doWelcomeMessages;
	public boolean doNotifyStart;
	public boolean doConsoleJoin;
	public boolean doConsoleStartup;
	public boolean doConsoleBotStart;
	public boolean renameFiles;
	public boolean makeSystemScripts;
	public boolean tpVoidPeople;
	public boolean isolateRPChat;
	public boolean ditchScoreboardColors;
	public boolean doRPG_combo;
	public boolean doTitles;
	public boolean doWebhooks;
	public boolean onlyCustomWaypoints;
	
}
