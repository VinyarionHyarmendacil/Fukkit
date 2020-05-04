package vinyarion.fukkit.main.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import joptsimple.internal.Strings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;
import vinyarion.fukkit.main.data.FCommandAliases;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.sm.discordbot.FDiscordBot;

public abstract class FCommand extends CommandBase {
	
	private final String phiName;
	private String[] aliases = new String[0];
	private int level = 2;
	
	public FCommand(String phiName) {
		FCommands.fukkitCommandNames.add(phiName);
		this.withAliases(FCommandAliases.instance().aliases(phiName));
		phiName = FCommandAliases.instance().rename(phiName);
		this.phiName = phiName;
	}
	
	public String getCommandName() {
		return this.phiName;
	}
	
	public int getRequiredPermissionLevel() {
        return this.level;
    }
	
	public FCommand setlevel(int level) {
		this.level = level;
        return this;
    }
	
	public FCommand withAliases(String... aliases) {
		this.aliases = Misc.concat(new String[][]{
			this.aliases, aliases
		});
        return this;
    }
	
	public List getCommandAliases() {
		return Arrays.asList(aliases);
	}
	
	public abstract String getCommandUsage(ICommandSender sender);
		
	public abstract void processCommand(ICommandSender sender, String[] args);
	
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		List ret;
		Object o = this.tabComplete(sender, args);
		if(o == null) {
			ret = Collections.emptyList();
		} else if(o instanceof String) {
			ret = Arrays.<String>asList((String)o);
		} else if(o instanceof String[]) {
			ret = Arrays.<String>asList((String[])o);
		} else if(o instanceof List) {
			ret = (List)o;
		} else if(o instanceof Iterable) {
			ret = Lists.newArrayList((Iterable)o);
		} else {
			ret = Collections.emptyList();
		}
		if(args.length > 0 && args[args.length - 1].length() > 0) {
			ret = getListOfStringsFromIterableMatchingLastWord(args, ret);
		}
		return ret;
	}
	
	public abstract Object tabComplete(ICommandSender sender, String[] args);
	
	public static int[] getLookPos(ICommandSender sender) {
		if(sender instanceof EntityPlayerMP) {
			EntityPlayerMP p = (EntityPlayerMP)sender;
			Vec3 eyes = Vec3.createVectorHelper(p.posX, p.boundingBox.minY + p.eyeHeight, p.posZ);
			Vec3 look = p.getLookVec();
			double reach = p.theItemInWorldManager.getBlockReachDistance();
			look.xCoord *= reach;
			look.yCoord *= reach;
			look.zCoord *= reach;
			look = look.addVector(eyes.xCoord, eyes.yCoord, eyes.zCoord);
			World world = sender.getEntityWorld();
			MovingObjectPosition res = world.func_147447_a(eyes, look, false, false, true);
			if(res != null) {
				return new int[]{res.blockX, res.blockY, res.blockZ};
			}
		}
		return new int[]{sender.getPlayerCoordinates().posX, sender.getPlayerCoordinates().posY, sender.getPlayerCoordinates().posZ};
	}
	
	public static void Info(ICommandSender sender, String arg) {
		MinecraftServer.getServer().addChatMessage(new ChatComponentText(arg));
		sender.addChatMessage(Colors.CHAT(arg));
	}
	
//	public static void Notify(ICommandSender sender, String arg) {
//		String msg = "[" + sender.getCommandSenderName() + "]: " + arg;
//		MinecraftServer.getServer().addChatMessage(new ChatComponentText(msg));
//		sender.addChatMessage(Colors.CHAT(msg));
//	}
	
	public static void NotifyAll(ICommandSender sender, String arg) {
		String msg = "[" + sender.getCommandSenderName() + "]: " + arg;
		MinecraftServer.getServer().addChatMessage(new ChatComponentText(msg));
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP player = (EntityPlayerMP)o;
			if(sender != player && FPermissions.instance().hasPermission(player, FPermissions.Others.perm_CommandOutput)) {
				player.addChatMessage(Colors.CHAT(msg));
			}
		}
		sender.addChatMessage(Colors.CHAT(msg));
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasLog()) {
			FDiscordBot.getInstance().queueLog(msg);
		}
	}
	
	public static void NotifyDev(ICommandSender sender, String arg) {
		String msg = "Dev: [" + sender.getCommandSenderName() + "]: " + arg;
		MinecraftServer.getServer().addChatMessage(new ChatComponentText(msg));
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP player = (EntityPlayerMP)o;
			if(player.getCommandSenderName().equals("Vinyarion")) {
				player.addChatMessage(Colors.CHAT(msg));
			}
		}
		if(FDiscordBot.botInitialized() && FDiscordBot.getInstance().hasLog()) {
			FDiscordBot.getInstance().queueLog(msg);
		}
	}
	
	protected static void Invalid(String arg) {
		throw new CommandException(Colors.color("Invalid argument '" + arg + "'!"));
	}
	
	protected static void Error(String iferr) {
		throw new CommandException(Colors.color(iferr));
	}
	
	protected static boolean Assert(boolean flag, String iferr) {
		if(!flag) {
			throw new CommandException(Colors.color(iferr));
		}
		return flag;
	}
	
	public static String[] players() {
		return MinecraftServer.getServer().getAllUsernames();
	}
	
	public static String[] alluuids() {
		return UsernameCache.getMap().keySet().stream().map(uuid -> uuid.toString()).collect(Collectors.toList()).toArray(new String[0]);
	}
	
	public static String[] allnames() {
		return UsernameCache.getMap().values().stream().collect(Collectors.toList()).toArray(new String[0]);
	}
	
	public static String[] reparse(String... args) {
		String cmd = Strings.join(args, " ");
		List<StringBuffer> ret = Lists.newArrayList();
		StringBuffer current = new StringBuffer();
		ret.add(current);
		boolean quotes = false;
		boolean wasquote = true;
		for(int i = 0; i < cmd.length(); i++) {
			char c = cmd.charAt(i);
			if(c == '`') {
				if(!wasquote) {
					quotes = !quotes;
				}
				current.append(c);
			} else {
				wasquote = false;
				if(quotes || !Character.isWhitespace(c)) {
					current.append(c);
				} else {
					current = new StringBuffer();
					ret.add(current);
				}
			}
		}
		String[] reta = new String[ret.size()];
		for(int i = 0; i < reta.length; i++) {
			reta[i] = ret.get(i).toString();
		}
		return reta;
	}
	
	public static List<String> allOf(String[] array, String... additional) {
		List<String> ret = Lists.newArrayList(array);
		ret.addAll(Lists.newArrayList(additional));
		return ret;
	}
	
	public static String rest(String[] args, int start, int maxChars) {
		String ret = rest(args, start);
		return ret.length() <= maxChars ? ret : ret.substring(0, maxChars);
	}
	
	public static String rest(String[] args, int start) {
		if(args.length <= start) {
			return "";
		}
		String ret = args[start];
		for(int i = start + 1; i < args.length; i++) {
			ret += " " + args[i];
		}
		return ret;
	}
	
	public static EntityPlayerMP ensure(ICommandSender sender) {
		return sender instanceof EntityPlayerMP ? (EntityPlayerMP)sender : null;
	}
	
	public static EntityPlayerMP playerFor(ICommandSender sender, String string) {
		if(string == null) {
			return null;
		} else {
			return getPlayer(sender, string);
		}
	}
	
	public static void println(ICommandSender sender, String string) {
		sender.addChatMessage(new ChatComponentText(Colors.color(string)));
	}
	
	public static double[] look(EntityPlayerMP p) {
		Vec3 eyes = Vec3.createVectorHelper(p.posX, p.boundingBox.minY + p.eyeHeight, p.posZ);
		Vec3 look = p.getLookVec();
		double reach = p.theItemInWorldManager.getBlockReachDistance();
		look.xCoord *= reach;
		look.yCoord *= reach;
		look.zCoord *= reach;
		look = look.addVector(eyes.xCoord, eyes.yCoord, eyes.zCoord);
		World world = p.getEntityWorld();
		MovingObjectPosition res = world.func_147447_a(eyes, look, false, false, true);
		if(res != null) {
			return new double[]{res.hitVec.xCoord, res.hitVec.yCoord, res.hitVec.zCoord};
		}
		return new double[]{p.posX, p.boundingBox.minY + p.eyeHeight, p.posZ};
	}
	
}
