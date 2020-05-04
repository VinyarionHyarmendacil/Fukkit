package vinyarion.fukkit.main.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRBannerProtection;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.script.FScript;

public class FCustomProtectionBlocks {
	
	private static FCustomProtectionBlocks instance = null;
	
	public static FCustomProtectionBlocks instance() {
		return instance;
	}
	
	static {
		instance = new FCustomProtectionBlocks();
		instance.file = FData.instance().file("custom_banner_protection_blocks", "txt");
		instance.protectionBlocks = (Map<Pair, Integer>)ReflectionHelper.getPrivateValue(LOTRBannerProtection.class, null, "protectionBlocks");
		FMod.log(Level.INFO, "FCustomProtectionBlocks initialized!");
	}
	
	private File file;
	private List<String> data;
	private Map<Pair, Integer> protectionBlocks;
	
	public void init() {
		if(!file.exists()) {
			FData.writeRawText(file, 
				  "#Comments start with '#'\n"
				+ "#Syntax is as follows:\n"
				+ "#  \"<block> <meta> <range>\" where <block> is a numeric ID or the unlocalized name, <meta> is the metadata value for the desired block, and <range> is a positive integer."
			);
			return;
		}
		for(String line : data = FData.readRawLines(file)) {
			if(line.length() == 0 || line.startsWith("#")) continue;
			String[] args = line.split(" ");
			try {
				if(args.length < 3) throw new Exception("Not enough arguments!");
				Block block = CommandBase.getBlockByText(MinecraftServer.getServer(), args[0]);
				Integer meta = Integer.valueOf(Integer.parseInt(args[1]));
				Integer range = Integer.valueOf(Integer.parseInt(args[2]));
				if(range <= 0 || range > 1024) throw new Exception("The range must be between 1 and 1024, inclusive!");
				protectionBlocks.put(Pair.of(block, meta), range);
			} catch(Exception e) {
				FMod.log(Level.ERROR, "FCustomProtectionBlocks: error reading line '" + line + "': " + e.getMessage());
			}
		}
	}
	
}
