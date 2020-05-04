package vinyarion.fukkit.main.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Serialization {
	
	public static String serialize(Object o) {
		String ret = o.getClass().getSimpleName() + ":\n";
		for(Field f : o.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			if((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC) continue;
			try {
				ret = ret + "   " + f.getName() + ":" + String.valueOf(f.get(o)) + "\n";
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
}
