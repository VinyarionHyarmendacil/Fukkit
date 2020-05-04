package vinyarion.fukkit.main.remotescript;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.remotescript.FPHIContainer.ContainerPlayer;
import vinyarion.fukkit.main.remotescript.FPHIExecutor.Sender;

public class FPHIScript {
	
	private final String[] lines;
	
	public FPHIScript(String[] lines) {
		this.lines = lines;
	}
	
	public void execute(ICommandSender sender, EntityPlayerMP player) {
		int max = 256;
		int at = -1;
		for(int i = 0; i < lines.length;) {
			at++;
			if(at >= max) {
				player.addChatComponentMessage(
						new ChatComponentText("[PHI] Maximum reached!"));
				break;
			}
			try {
				player.addChatComponentMessage(
						new ChatComponentText("[PHI] Executing line: " + lines[i]));
				String[] args = value(lines[i]).split(" ");
				if(args[0].equals("line")) {
					//nothing
				} else if(args[0].equals("max")) {
					max = Integer.parseInt(args[1]);
				} else if(args[0].equals("ifgoto")) {
					if((Boolean)parse(args[1])) {
						try {
							i = Integer.parseInt(args[2]);
						} catch(Exception e) {
							for(int j = 0; j < lines.length; j++) {
								if(lines[j].startsWith("label") && lines[j].startsWith(args[2], 6)) {
									i = j;
								}
							}
						}
						continue;
					}
				} else if(args[0].startsWith(">")) {
					FPHIExecutor.execute(player, lines[i].substring(1));
				} else if(args[0].equals("container")) {
					if(args[1].contains("@")) {
						for(EntityPlayerMP p : PlayerSelector.matchPlayers(sender, args[1])) {
							ContainerPlayer c = new ContainerPlayer(p);
							if(args[2].equals("set")) {
								c.set(args[3], parse(rest(4, args)));
							} else if(args[2].equals("get")) {
								set(rest(4, args), c.get(args[3]));
							}
						}
					} else {
						EntityPlayerMP p = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[1]);
						ContainerPlayer c = new ContainerPlayer(p);
						if(args[2].equals("set")) {
							c.set(args[3], parse(rest(4, args)));
						} else if(args[2].equals("get")) {
							set(rest(4, args), c.get(args[3]));
						}
					}
				} else if(args[0].equals("asplayer")) {
					if(args[1].contains("@")) {
						for(EntityPlayerMP p : PlayerSelector.matchPlayers(sender, args[1])) {
							FPHIExecutor.execute(p, rest(2, args));
						}
					} else {
						EntityPlayerMP p = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[1]);
						FPHIExecutor.execute(p, rest(2, args));
					}
				} else if(args[0].equals("var")) {
					set(args[1], parse(args[2]));
				} else if(args[0].equals("math")) {
					String target = args[1];
					String op = args[3];
					Number n = null;
					{
						Number o1 = (Number)parse(args[4]);
						Number o2 = (Number)parse(args[5]);
						if(op.equals("add")) {
							n = FPHIPrimitiveOperations.add(args[2], o1, o2);
						} else if(op.equals("sub")) {
							n = FPHIPrimitiveOperations.sub(args[2], o1, o2);
						} else if(op.equals("mul")) {
							n = FPHIPrimitiveOperations.mul(args[2], o1, o2);
						} else if(op.equals("div")) {
							n = FPHIPrimitiveOperations.div(args[2], o1, o2);
						} else if(op.equals("mod")) {
							n = FPHIPrimitiveOperations.mod(args[2], o1, o2);
						}
					}
					set(args[1], n);
				} else if(args[0].equals("compare")) {
					String op = args[2];
					String var = args[1];
					BigDecimal d1 = new BigDecimal(String.valueOf(parse(args[3])));
					BigDecimal d2 = new BigDecimal(String.valueOf(parse(args[4])));
					if(op.equals("more")) {
						set(var, d1.subtract(d2).signum() == 1);
					} else if(op.equals("less")) {
						set(var, d1.subtract(d2).signum() == -1);
					} else if(op.equals("least")) {
						set(var, d1.subtract(d2).signum() != -1);
					} else if(op.equals("most")) {
						set(var, d1.subtract(d2).signum() != 1);
					}
				} else if(args[0].equals("out")) {
					player.addChatComponentMessage(
							new ChatComponentText("[PHI] Value of '" + args[1] + "' is " + parse(args[1])));
				} else if(args[0].equals("print")) {
					player.addChatComponentMessage(
							new ChatComponentText("[PHI] " + parse(args[1])));
				}
			} catch(Exception e) {
				player.addChatComponentMessage(
						new ChatComponentText("[PHI] Caught exception whilst executing line " + i + "! (" + e.toString() + ")"));
				for(StackTraceElement ste : e.getStackTrace()) {
					player.addChatComponentMessage(
							new ChatComponentText("[PHI]  " + ste.toString()));
				}
			}
			i++;
		}
	}
	
	private String value(String line) {
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == '#') {
				for(int j = 0; j < line.length(); j++) {
					if(line.charAt(j) == '#') {
						line = line.substring(0, i) + parse(line.substring(i + 1, j)) + line.substring(j + 1);
					}
				}
			}
		}
		return line;
	}
	
	private static String rest(int from, String[] args) {
		String ret = args[from];
		for(int i = from + 1; i < args.length; i++) {
			ret = " " + args[i];
		}
		return ret;
	}
	
	private static Map<String, Object> globalPool = new HashMap<String, Object>();
	
	private Map<String, Object> realPool = new HashMap<String, Object>();
	
	private Object get(String s) {
		return s.startsWith("$") ? globalPool.get(s) : realPool.get(s);
	}
	
	private void set(String s, Object o) {
		(s.startsWith("$") ? globalPool : realPool).put(s, o);
	}
	
	private Object parse(String s) {
		if(s.equals("null")) {
			return null;
		} else if(s.contains("@")) {
			String t = s.substring(0, s.indexOf("@"));
			String v = s.substring(s.indexOf("@") + 1, s.length());
			if(t.equals("z")) {
				return Boolean.valueOf(v);
			} else if(t.equals("b")) {
				return Byte.parseByte(v);
			} else if(t.equals("s")) {
				return Short.parseShort(v);
			} else if(t.equals("i")) {
				return Integer.parseInt(v);
			} else if(t.equals("l")) {
				return Long.parseLong(v);
			} else if(t.equals("f")) {
				return Float.parseFloat(v);
			} else if(t.equals("d")) {
				return Double.parseDouble(v);
			} else if(t.equals("class")) {
				try {
					return Class.forName(v);
				} catch (ClassNotFoundException e) {
					return null;
				}
			} else if(t.equals("S")) {
				return v;
			} else {
				return get(v);
			}
		} else {
			return this.get(s);
		}
	}
	
}
