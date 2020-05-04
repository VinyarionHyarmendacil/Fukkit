package vinyarion.fukkit.main.game;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.ServerChatEvent;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FChatRP {
	
	public static List<String> rp = new ArrayList<String>();
	public static List<String> muted = new ArrayList<String>();
	
	private static NBTTagList list = NBT.ensureList(FData.instance().tag("muted_players"), "list");
	
	static {
		for(int i = 0; i < list.tagCount(); i++) {
			String name = list.getStringTagAt(i);
			if(!muted.contains(name)) {
				muted.add(name);
			}
		}
	}
	
	public static boolean cancel(ServerChatEvent event) {
		String name = event.player.getCommandSenderName();
		if(muted.contains(name)) {
			return true;
		} else if(rp.contains(name)) {
			IChatComponent msg = Colors.MAKE("&f[&6RP&f] &r").appendSibling(FPlayer.PNS(event.player)).appendText(Colors.TALK + event.message);
			for(EntityPlayerMP rec : of(rp)) {
				if(event.player.getDistanceToEntity(rec) <= 50.0F) {
					rec.addChatMessage(msg);
				}
			}
//			event.player.addChatMessage(msg);
			return true;
		}
		return false;
	}
	
	public static void onSHOUT(EntityPlayerMP sender, String rest) {
		if(rp.contains(sender.getCommandSenderName())) {
			IChatComponent msg = Colors.MAKE("&f[&6RP&f] [&5SHOUT&3]&r ").appendSibling(FPlayer.PNS(sender)).appendText(Colors.TALK + rest);
			for(EntityPlayerMP rec : of(rp)) {
				if(sender.getDistanceToEntity(rec) <= 100.0F) {
					rec.addChatMessage(msg);
				}
			}
//			sender.addChatMessage(msg);
		} else {
			FCommand.Info(sender, "You must be in RP chat to do this.");
		}
	}
	
	public static void onQUIET(EntityPlayerMP sender, String rest) {
		if(rp.contains(sender.getCommandSenderName())) {
			IChatComponent msg = Colors.MAKE("&f[&6RP&f] [&5QUIET&3]&r ").appendSibling(FPlayer.PNS(sender)).appendText(Colors.TALK + rest);
			for(EntityPlayerMP rec : of(rp)) {
				if(sender.getDistanceToEntity(rec) <= 5.0F) {
					rec.addChatMessage(msg);
				}
			}
//			sender.addChatMessage(msg);
		} else {
			FCommand.Info(sender, "You must be in RP chat to do this.");
		}
	}
	
	public static void onEMOTE(EntityPlayerMP sender, String rest) {
		if(rp.contains(sender.getCommandSenderName())) {
			IChatComponent msg = Colors.MAKE("&f[&6RP&f] [&5EMOTE&3]&r ").appendSibling(FPlayer.PNS(sender)).appendText(Colors.TALK + rest);
			for(EntityPlayerMP rec : of(rp)) {
				if(sender.getDistanceToEntity(rec) <= 50.0F) {
					rec.addChatMessage(msg);
				}
			}
		} else {
			FCommand.Info(sender, "You must be in RP chat to do this.");
		}
	}
	
	public static List<EntityPlayerMP> of(List<String> list) {
		List<EntityPlayerMP> ret = new ArrayList<EntityPlayerMP>();
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP player = (EntityPlayerMP)o;
			if(list.contains(player.getCommandSenderName())) {
				ret.add(player);
			}
		}
		return ret;
	}
	
	public static int onRP(EntityPlayerMP sender) {
		String name = sender.getCommandSenderName();
		if(muted.contains(name)) {
			return -1;
		}
		if(rp.contains(name)) {
			rp.remove(name);
			return 1;
		} else {
			rp.add(name);
			return 0;
		}
	}
	
	public static int onMUTE(EntityPlayerMP target) {
		String name = target.getCommandSenderName();
		if(muted.contains(name)) {
			return 1;
		} else {
			rp.remove(name);
			muted.add(name);
			list.appendTag(new NBTTagString(name));
			return 0;
		}
	}
	
	public static int onUNMUTE(EntityPlayerMP target) {
		String name = target.getCommandSenderName();
		if(muted.contains(name)) {
			muted.remove(name);
			for(int i = 0; i < list.tagCount(); i++) {
				if(list.getStringTagAt(i).equals(name)) {
					list.removeTag(i);
					i--;
				}
			}
			return 0;
		} else {
			return 1;
		}
	}
	
}
