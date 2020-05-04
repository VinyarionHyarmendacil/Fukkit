package vinyarion.fukkit.main.script;

import java.util.Collections;
import java.util.List;

public class FScriptImprov extends FScript {

	public FScriptImprov(String data) {
		super(Collections.singletonList(untick(data)));
	}

	public static String untick(String data) {
		StringBuffer sb = new StringBuffer(data);
		while(sb.charAt(0) == '`') {
			sb.delete(0, 1);
		}
		while(sb.charAt(sb.length() - 1) == '`') {
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}

	public static boolean isImprov(String name) {
		return name == null ? false : 
			name.length() < 2 ? false : 
			name.startsWith("`") ? name.endsWith("`") : false;
	}

}
