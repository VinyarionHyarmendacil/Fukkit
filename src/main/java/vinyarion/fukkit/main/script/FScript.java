package vinyarion.fukkit.main.script;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.Time;

public class FScript {

	public static final ThreadLocal<EntityPlayer> local = new ThreadLocal<EntityPlayer>();

	public final List<String> data;

	public FScript(String data) {
		this(Lists.newArrayList(data.split("\\R")));
	}

	public FScript(List<String> data) {
		this.data = data;
	}

	public void command(EntityPlayer player) {
		String name = player == null ? "" : player.getCommandSenderName();
		if(player != null) {
			local.set(player);
		}
		try {
			for(String line : this.data) {
				executeCommand(line);
			}
		} catch(Exception e) {
			MinecraftServer.getServer().addChatMessage(new ChatComponentText("This script has something wrong with it!"));
			e.printStackTrace();
		}
		local.remove();
	}

	public static void executeCommand(String string) {
		EntityPlayer player = local.get();
		String name = player == null ? "" : player.getCommandSenderName();
		String how = string.substring(0, 2);
		String cmd = string.substring(2);
		if(how.equals("p:")) {
			FMod.later.queue(FPermissions.runnable(()->MinecraftServer.getServer().getCommandManager().executeCommand(player, cmd)));
		} else if(how.equals("s:")) {
			FMod.later.queue(FPermissions.runnable(()->MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), cmd.replace("{PLAYER}", name))));
		} else {
			FMod.later.queue(FPermissions.runnable(
				player == null ? 
					()->MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), cmd) :
					()->MinecraftServer.getServer().getCommandManager().executeCommand(player, string)
			));
		}
	}

}
