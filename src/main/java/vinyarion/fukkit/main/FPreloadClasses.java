package vinyarion.fukkit.main;

import java.util.function.Supplier;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.inventory.Container;

public class FPreloadClasses {

	private static final String[][] classes = {
		s("net.minecraft.inventory.Container", "I don't care about this one."), 
	};

	private static final Object[][][] fields = {
		{
			s("org.spigotmc.SpigotConfig", "No Spigot. I love this life!", "Set fullMatchRate to 1. Fuck you, Spigot!"),
			s("fullMatchRate"),
			$(()->1)
		},
	};

	public static void load() {
		for(String[] name : classes) {
			try {
				Class.forName(name[0]);
				FMod.log("Preloaded class '"+name[0]+"'!");
			} catch(Exception e) {
				FMod.log("Failed to load class '"+name[0]+"'! "+name[1]);
			}
		}
		for(Object[][] data : fields) {
			String[] c = (String[])data[0];
			try {
				ReflectionHelper.findField(Class.forName(c[0]), (String[])data[1]).set(null, ((Supplier<?>)data[2][0]).get());
			} catch(Exception e) {
				FMod.log(c[1]);
				continue;
			}
			FMod.log(c[2]);
		}
	}

	private static Object[]$(Supplier<?>s){return new Object[]{s};}
	private static String[]s(String...s){return s;}

}
