package vinyarion.fukkit.main.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lotr.common.LOTRDimension;
import lotr.common.world.biome.LOTRBiome;
import lotr.common.world.spawning.LOTRSpawnEntry;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.FMod;

public class FBannedMods {
	
	private static FBannedMods instance = null;
	
	public static FBannedMods instance() {
		return instance;
	}
	
	static {
		instance = new FBannedMods();
		instance.file = FData.instance().file("banned_mods", "txt");
		if(!instance.file.exists()) {
			FData.writeRawText(instance.file, 
				  "#This file allows you to ban clients from joining with certain mods.\n"
				+ "#Lines starting with the character '#' are comments and wil be ignored.\n"
				+ "#To ban a mod, simply type the ModID of that mod on a line, one ModID per line.\n\n");
		}
		instance.entries = FData.readRawLines(instance.file);
		FMod.log(Level.INFO, "FBannedMods initialized!");
	}
	
	private File file;
	
	private List<String> entries = Lists.newArrayList();
	private final List<String> bannedimpl = Lists.newArrayList();
	public final List<String> banned() {
		return ImmutableList.copyOf(bannedimpl);
	}

	public void setup() {
		for(String line : entries) {
			if(line.startsWith("#") || line.length() == 0) continue;
			try {
				bannedimpl.add(line);
				FMod.log(Level.INFO, "Parsed line '" + line + "'");
			} catch(Exception e) {
				FMod.log(Level.ERROR, "Error parsing banned mod entry : " + line);
				e.printStackTrace();
			}
		}
		
	}
	
}
