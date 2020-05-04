package vinyarion.fukkit.main.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.potion.Potion;

public class Effects {
	
	public static int of(String name) {
		for(int i = 0; i < Potion.potionTypes.length; i++) {
			Potion p = Potion.potionTypes[i];
			if(p != null) {
				if(Potion.potionTypes[i].getName().substring(7).equals(name)) {
					return i;
				}
			}
		}
		try {
			return Integer.parseInt(name);
		} catch(NumberFormatException e) {
			return 1;
		}
	}
	
	public static List<String> list(String arg) {
		List<String> ret = new ArrayList<String>();
		for(int i = 0; i < Potion.potionTypes.length; i++) {
			Potion p = Potion.potionTypes[i];
			if(p != null) {
				ret.add((arg.length() == 0 ? "" : (arg + ";")) + p.getName().substring(7));
			}
		}
		return ret;
	}
	
}
