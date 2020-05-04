package vinyarion.fukkit.main.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.recipes.Permenant;

public class FChestGuis {
	
	private static FChestGuis instance = null;
	
	public static FChestGuis instance() {
		return instance;
	}
	
	static {
		instance = new FChestGuis();
		instance.dir = FData.instance().dir("chestguis");
		instance.dirs = "chestguis/";
		FMod.log(Level.INFO, "FChestGuis initialized!");
	}
	
	private String dirs;
	private File dir;
	
	private Map<String, ChestGui> guis = new HashMap<String, ChestGui>();
	
	public void register(String name, ChestGui gui) {
		if(name != null && !guis.containsKey(name) && gui != null && gui instanceof Permenant) {
			guis.put(name, gui);
			gui.id = name;
		}
	}
	
	public ChestGui get(String name) {
		if(!guis.containsKey(name)) {
			this.load(name);
		}
		return guis.get(name);
	}
	
	private void load(String name) {
		if(name == null) {
			FMod.log(Level.ERROR, "FChestGuis: name was null!");
			return;
		}
		NBTTagCompound data = FData.instance().tag(dirs + name + "/data", false);
		if(data == null) {
			FMod.log(Level.ERROR, "FChestGuis: data for '" + name + "' does not exist!");
			return;
		}
		List<String> items = FData.readRawLines(FData.instance().file(dirs + name + "/items", "txt"));
		List<String> commands = FData.readRawLines(FData.instance().file(dirs + name + "/commands", "txt"));
		String[] itema = items.toArray(new String[items.size()]);
		String[] commanda = commands.toArray(new String[commands.size()]);
		ChestGui cg = new ChestGui(data, itema, commanda);
		guis.put(name, cg);
	}
	
	public void reload(String name) {
		ChestGui cg = guis.remove(name);
		if(cg != null && cg instanceof Permenant) {
			guis.put(name, cg);
		} else {
			load(name);
		}
	}

	public List<String> names() {
		return new ArrayList<String>(guis.keySet());
	}
	
}
