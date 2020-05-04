package vinyarion.fukkit.main.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.util.Time;

public class FBlockLogger {
	
	private static File dir = FData.instance().dir("blocklogs");
	private static String last = Time.instantNow().replace(":", "_");
	private static List<String> list = new ArrayList<String>();
	private static int maxSize = 4096;
	
	static {
		FMod.log(Level.INFO, "Starting block log!");
	}
	
	public static synchronized void log(String string) {
		list.add(string);
		if(list.size() >= maxSize) {
			save();
		}
	}
	
	public static synchronized void save() {
		String last0 = last;
		last = Time.instantNow().replace(":", "_");
		long l = System.currentTimeMillis();
		List<String> write = new ArrayList<String>(list);
		list.clear();
		FData.writeRawLines(new File(dir, "from_" + last0 + "_to_" + last + ".block.log"), write);
		FMod.log(Level.INFO, "Saved FBlockLogger! Took " + (System.currentTimeMillis() - l) + " ms.");
	}
	
}
