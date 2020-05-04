package vinyarion.fukkit.main.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lotr.common.LOTRDimension;
import lotr.common.world.biome.LOTRBiome;
import lotr.common.world.spawning.LOTRSpawnEntry;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.cmd.FCommands;
import vinyarion.fukkit.main.util.Misc;

public class FCommandAliases {
	
	private static FCommandAliases instance = null;
	
	public static FCommandAliases instance() {
		return instance;
	}
	
	static {
		instance = new FCommandAliases();
		instance.file = FData.instance().file("command_aliases", "txt");
		if(!instance.file.exists()) {
			FData.writeRawText(instance.file, 
				  "#This file allows you to assign aliases for fukkit commands.\n"
				+ "#Lines starting with the character '#' are comments and wil be ignored.\n"
				+ "#Example: rename frpg_manageblock manageTileEntity    ## Renames the command.\n"
				+ "#Example: alias frpg_manageblock manageTileEntity manageBlockUsers    ## Adds alias to command.\n"
				+ "#Valid commands: "+StringUtils.join(FCommands.fukkitCommands,',')+"\n\n");
		}
		instance.entries = FData.readRawLines(instance.file);
		FMod.log(Level.INFO, "FCommandAliases initialized!");
	}
	
	private File file;
	
	private List<String> entries = Lists.newArrayList();
	private final Map<String, String> renames = Maps.newHashMap();
	private final Map<String, String[]> aliases = Maps.newHashMap();

	public void setup() {
		for(String line : entries) {
			if(line.startsWith("#") || line.length() == 0) continue;
			try {
				String[] args = line.split(" ");
				if(args[0].equalsIgnoreCase("rename")) {
					renames.put(args[1], args[2]);
				} else if(args[0].equalsIgnoreCase("alias")) {
					aliases.put(args[1], FCommand.rest(args, 2).split(" "));
				} else throw new Exception("Unknown arg "+args[0]);
				FMod.log(Level.INFO, "Parsed line '" + line + "'");
			} catch(Exception e) {
				FMod.log(Level.ERROR, "Error parsing banned mod entry : " + line);
				e.printStackTrace();
			}
		}
		
	}

	public String rename(String phiName) {
		return renames.getOrDefault(phiName, phiName);
	}

	public String[] aliases(String phiName) {
		return aliases.getOrDefault(phiName, new String[0]);
	}
	
}
