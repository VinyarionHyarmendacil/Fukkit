package vinyarion.fukkit.main.util;

import java.util.regex.Pattern;

import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.data.FServer;

public class Colors {
	
	public static final String CROSSED_SWORDS = String.valueOf('\u2694');
//	public static final String HANDSHAKE = String.valueOf('\u1F91D');
	public static final String DISSALLOWED = String.valueOf('\u26d4');
	public static final String MUCHGREATER = String.valueOf('\u00bb');
	public static final String TALK = " " + MUCHGREATER + " ";
	
    public static final String SECTION = String.valueOf('\u00a7');
    public static final String BLACK = SECTION + "0";
    public static final String DARKBLUE = SECTION + "1";
    public static final String DARKGREEN = SECTION + "2";
    public static final String DARKAQUA = SECTION + "3";
    public static final String DARKRED = SECTION + "4";
    public static final String DARKPURPLE = SECTION + "5";
    public static final String GOLD = SECTION + "6";
    public static final String GRAY = SECTION + "7";
    public static final String DARKGRAY = SECTION + "8";
    public static final String BLUE = SECTION + "9";
    public static final String GREEN = SECTION + "a";
    public static final String AQUA = SECTION + "b";
    public static final String RED = SECTION + "c";
    public static final String LIGHTPURPLE = SECTION + "d";
    public static final String YELLOW = SECTION + "e";
    public static final String WHITE = SECTION + "f";
    public static final String OBFUSCATED = SECTION + "k";
    public static final String BOLD = SECTION + "l";
    public static final String STRIKETHROUGH = SECTION + "m";
    public static final String UNDERLINE = SECTION + "n";
    public static final String ITALIC = SECTION + "o";
    public static final String RESET = SECTION + "r";
    
    public static final String TRUE = BLUE + "true";
    public static final String FALSE = GREEN + "false";
    
    public static final String FUKKITDEP = color("&r&6[&cValinor&6]&a:");
    public static final String FUKKIT_default = color("&r&f[&9Valinor&f]&a:");
    public static String FUKKIT = FServer.instance().tag.hasKey("header") ? FServer.instance().tag.getString("header") : color("&6Server &f>");
    
    public static String fukkit(String after) {
    	return FUKKIT + " " + after;
    }
    
    public static String FUKKIT(String after) {
    	return FUKKIT + " " + color(after);
    }
    
    public static ChatComponentText chat(String after) {
    	return new ChatComponentText(FUKKIT + " " + after);
    }
    
    public static ChatComponentText CHAT(String after) {
    	return new ChatComponentText(FUKKIT + " " + color(after));
    }
	
	public static ChatComponentText make(String string) {
		return new ChatComponentText(string);
	}
	
	public static ChatComponentText MAKE(String string) {
		return new ChatComponentText(color(string));
	}
	
	private static final Pattern formattingCodePattern = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
	private static final Pattern reformattingCodePattern = Pattern.compile("(?i)" + String.valueOf('\u0026') + "[0-9A-FK-OR]");
	public static String getTextWithoutFormattingCodes(String string) {
        return string == null ? null : formattingCodePattern.matcher(string).replaceAll("");
    }
	public static String getTextWithoutReFormattingCodes(String string) {
        return string == null ? null : reformattingCodePattern.matcher(string).replaceAll("");
    }
	public static String getTextWithoutAnyFormattingCodes(String string) {
        return getTextWithoutFormattingCodes(getTextWithoutReFormattingCodes(string));
    }
	
	public static String color(String in) {
		return in.replace("&0", BLACK)
				.replace("&1", DARKBLUE)
				.replace("&2", DARKGREEN)
				.replace("&3", DARKAQUA)
				.replace("&4", DARKRED)
				.replace("&5", DARKPURPLE)
				.replace("&6", GOLD)
				.replace("&7", GRAY)
				.replace("&8", DARKGRAY)
				.replace("&9", BLUE)
				.replace("&a", GREEN)
				.replace("&b", AQUA)
				.replace("&c", RED)
				.replace("&d", LIGHTPURPLE)
				.replace("&e", YELLOW)
				.replace("&f", WHITE)
				.replace("&k", OBFUSCATED)
				.replace("&l", BOLD)
				.replace("&m", STRIKETHROUGH)
				.replace("&n", UNDERLINE)
				.replace("&o", ITALIC)
				.replace("&r", RESET);
	}
    
	public static String uncolor(String in) {
		return in.replace(BLACK, "&0")
				.replace(DARKBLUE, "&1")
				.replace(DARKGREEN, "&2")
				.replace(DARKAQUA, "&3")
				.replace(DARKRED, "&4")
				.replace(DARKPURPLE, "&5")
				.replace(GOLD, "&6")
				.replace(GRAY, "&7")
				.replace(DARKGRAY, "&8")
				.replace(BLUE, "&9")
				.replace(GREEN, "&a")
				.replace(AQUA, "&b")
				.replace(RED, "&c")
				.replace(LIGHTPURPLE, "&d")
				.replace(YELLOW, "&e")
				.replace(WHITE, "&f")
				.replace(OBFUSCATED, "&k")
				.replace(BOLD, "&l")
				.replace(STRIKETHROUGH, "&m")
				.replace(UNDERLINE, "&n")
				.replace(ITALIC, "&o")
				.replace(RESET, "&r");
	}
    
	public static String clean(String in) {
		return uncolor(in).replace("&0", "")
				.replace("&1", "")
				.replace("&2", "")
				.replace("&3", "")
				.replace("&4", "")
				.replace("&5", "")
				.replace("&6", "")
				.replace("&7", "")
				.replace("&8", "")
				.replace("&9", "")
				.replace("&a", "")
				.replace("&b", "")
				.replace("&c", "")
				.replace("&d", "")
				.replace("&e", "")
				.replace("&f", "")
				.replace("&k", "")
				.replace("&l", "")
				.replace("&m", "")
				.replace("&n", "")
				.replace("&o", "")
				.replace("&r", "");
	}
	
}
