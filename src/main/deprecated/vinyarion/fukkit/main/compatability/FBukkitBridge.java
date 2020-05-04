package vinyarion.fukkit.main.compatability;

import java.lang.reflect.Field;
import java.util.List;

@Deprecated
public class FBukkitBridge {
	
	private static boolean bukkit = false;
	private static boolean gandalf = false;
	
	static Object bukkitServer = null;
	static Object bukkitPlugins = null;
	static Object gandalfPlugin = null;
	
	public static void bukkitINIT() {
		try {
			//bukkit
			Class.forName("org.bukkit.Bukkit");
			bukkit = true;
			bukkitServer = Class.forName("org.bukkit.Bukkit")
					.getDeclaredMethod("getServer", new Class[0])
					.invoke(null, new Object[0]);
			//pluginmanager
			bukkitPlugins = bukkitServer.getClass()
					.getDeclaredMethod("getPluginManager", new Class[0])
					.invoke(bukkitServer, new Object[0]);
			//gandalf
			Class.forName("com.LOTRChat.main.Main");
			gandalf = true;
			gandalfPlugin = getPlugin("Gandalf");
		} catch(Exception e) {
			e.printStackTrace();
		}
		FGandalfBridge.gandalfINIT();
	}
	
	public static Object getPlugin(String name) throws Exception {
//		Object[] plugins = (Object[])bukkitPlugins.getClass()
//				.getDeclaredMethod("getPlugins", new Class[0])
//				.invoke(bukkitPlugins, new Object[0]);
		return bukkitPlugins.getClass()
				.getDeclaredMethod("getPlugin", new Class[]{String.class})
				.invoke(bukkitPlugins, new Object[]{name});
	}
	
	public static boolean isBukkit() {
		return bukkit;
	}
	
	public static boolean isGandalf() {
		return gandalf;
	}
	
	
	
}
