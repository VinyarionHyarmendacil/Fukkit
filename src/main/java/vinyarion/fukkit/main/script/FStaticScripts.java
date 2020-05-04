package vinyarion.fukkit.main.script;

import java.io.File;

import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.data.FScripts;

public class FStaticScripts {

	private static final boolean make = FMod.config.makeSystemScripts;

	static {
		File f = FData.instance().dir("scripts/system/server");
		if(!f.exists()) f.mkdirs();
		f = FData.instance().dir("scripts/system/player");
		if(!f.exists()) f.mkdirs();
	}

	public static FScript serverStart() { return FScripts.instance().get("system/server/start", make); }
	public static FScript serverStop() { return FScripts.instance().get("system/server/stop", make); }
	public static FScript playerJoinFirst() { return FScripts.instance().get("system/player/new", make); }
	public static FScript playerJoin() { return FScripts.instance().get("system/player/join", make); }
	public static FScript playerLeave() { return FScripts.instance().get("system/player/leave", make); }

}
