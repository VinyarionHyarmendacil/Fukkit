package vinyarion.fukkit.main.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.item.Item;
import vinyarion.fukkit.main.FMod;

public class FBannedItems {
	
	private static FBannedItems instance;
	
	static {
		instance = new FBannedItems();
		FMod.log(Level.INFO, "FBannedItems initialized!");
	}
	
	public static FBannedItems instance() {
		return instance;
	}
	
	public List<Item> bannedItems = new ArrayList<Item>();
	
}
