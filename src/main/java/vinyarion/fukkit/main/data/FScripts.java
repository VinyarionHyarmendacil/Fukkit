package vinyarion.fukkit.main.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.script.FScriptImprov;
import vinyarion.fukkit.main.script.js.FScriptNashorn;
import vinyarion.fukkit.main.script.FScript;
import vinyarion.fukkit.main.script.FScriptEmpty;

public class FScripts {
	
	private static FScripts instance = null;
	
	public static FScripts instance() {
		return instance;
	}
	
	static {
		instance = new FScripts();
		instance.dir = FData.instance().dir("scripts");
		FMod.log(Level.INFO, "FScripts initialized!");
	}

	private static final String[] exts = {
		"txt", "js"
	};
	
	private File dir;
	
	private Map<String, FScript> scripts = new HashMap<String, FScript>();
	
	public FScript get(String name) {
		return get(name, true);
	}
	
	public FScript get(String name, boolean load) {
		if(FScriptNashorn.Improv.isImprov(name)) {
			return FScriptNashorn.Improv.of(name);
		}
		if(FScriptImprov.isImprov(name)) {
			return new FScriptImprov(name);
		}
		if(!scripts.containsKey(name)) {
			if(load) {
				this.load(name, name.contains("system"));
			} else {
				return new FScriptEmpty();
			}
		}
		return scripts.get(name);
	}
	
	private void load(String name, boolean writedef) {
		File scfile = null;
		String ext = "";
		for(String triedext : exts) {
			ext = triedext;
			File scfile0 = FData.instance().file(dir, name, ext);
			if(scfile0.exists()) {
				scfile = scfile0;
				break;
			}
		}
		if(scfile == null) {
			FMod.log(Level.ERROR, "FScripts: text and code for '" + name + "' does not exist!");
			FMod.log(Level.ERROR, "FScripts: writing default empty file for '" + name + "'!");
			FData.writeRawText(scfile = FData.instance().file(dir, name, "txt"), "\n");
		}
		System.out.println(scfile.getAbsolutePath());
		String data = FData.readRawText(scfile);
		if(data == null ||data.length() == 0) {
			FMod.log(Level.ERROR, "FScripts: data for '" + name + "' is nonexistant!");
			return;
		}
		FScript sc;
		if(ext.equals("js")) {
			sc = FScriptNashorn.of(data);
		} else {
			sc = new FScript(data);
		}
		scripts.put(name, sc);
	}
	
	public void reload(String name) {
		scripts.remove(name);
		load(name, false);
	}

	public List<String> names() {
		return new ArrayList<String>(scripts.keySet());
	}
	
}
