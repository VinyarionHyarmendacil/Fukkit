package vinyarion.fukkit.main.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.FMod;

public enum Updates {

	CHECKED, 
	PENDING, 
	NO;

	private static final String path = "https://www.dropbox.com/s/uu8wg40430vsxyt/fukkit.txt?dl=0";

	private static Updates state = NO;

	public static String result = null;

	public static synchronized void check() {
		if(state == NO) new Thread(() -> checkImpl()).start();
	}

	private static void checkImpl() {
		state = PENDING;
		try {
			URL url = new URL(path);
			Properties props = new Properties();
			try {
				InputStream stream = url.openStream();
				props.load(stream);
				stream.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			String version = props.getProperty("version", FMod.version);
			int increment = Integer.parseInt(props.getProperty("increment", String.valueOf(FMod.increment)));
			String thisversion = FMod.version;
			int thisincrement = FMod.increment;
			if(increment > thisincrement) {
				result = "There is an update available for Vinyarion's server-side mod, version " + version + ". This server is running " + thisversion + ".";
				MinecraftServer.getServer().getConfigurationManager().sendChatMsgImpl(Colors.MAKE(result), true);
			}
			MinecraftServer.getServer().addChatMessage(Colors.make("Update checker finished with result '" + result + "'."));
		} catch(Exception e) {
			MinecraftServer.getServer().addChatMessage(Colors.make("Update checker failed with result '" + result + "'!"));
			e.printStackTrace();
		}
		state = CHECKED;
	}

}
