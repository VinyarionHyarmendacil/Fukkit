package vinyarion.fukkit.main.compatability;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@Deprecated
public class FGandalfBridge extends FBukkitBridge {
	
	static Field f_ooc;
	static Field f_rp;
	static Field f_muted;
	static Method m_playerListName;
	static List rp;
	static List ooc;
	static List muted;
	
	static void gandalfINIT() {
		try {
			f_ooc = gandalfPlugin.getClass().getField("ooc");
			f_rp = gandalfPlugin.getClass().getField("rp");
			f_muted = gandalfPlugin.getClass().getField("muted");
			m_playerListName = Class.forName("org.bukkit.entity.Player").getMethod("getPlayerListName", new Class[0]);
			f_ooc.setAccessible(true);
			f_rp.setAccessible(true);
			f_muted.setAccessible(true);
			ooc = (List)f_ooc.get(gandalfPlugin);
			rp = (List)f_rp.get(gandalfPlugin);
			muted = (List)f_muted.get(gandalfPlugin);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static boolean isNameChatable(String name) {
		if(FBukkitBridge.isGandalf()) {
			try {
				for(Object o : ooc) {
					String s = (String)m_playerListName.invoke(o, new Object[0]);
					if(s.equals(name)) {
						return false;
					}
				}
				for(Object o : rp) {
					String s = (String)m_playerListName.invoke(o, new Object[0]);
					if(s.equals(name)) {
						return false;
					}
				}
				for(Object o : muted) {
					String s = (String)m_playerListName.invoke(o, new Object[0]);
					if(s.equals(name)) {
						return false;
					}
				}
				return true;
			} catch(Exception e) {
				return true;
			}
		} else {
			return true;
		}
	}
	
}
