package vinyarion.fukkit.sm.cmd;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FWarps;
import vinyarion.fukkit.main.util.Colors;

public class FCommandTPA extends FCommand {
	
	public FCommandTPA(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		FWarps warps = FWarps.instance();
		FPermissions perms = FPermissions.instance();
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		Assert(args.length > 0, "Must give a player!");
		String name = args[0];
		EntityPlayerMP target = getPlayer(player, name);
		Assert(target != null, "Must specify one player!");
		Request req = new Request(player, target);
		Request ar = null;
		Request tr = null;
		for(Request r : pending) {
			if(r.asker.equals(player)) {
				ar = r;
			}
			if(r.target.equals(target)) {
				tr = r;
			}
		}
		if(ar != null) {
			pending.remove(ar);
		}
		if(tr != null) {
			pending.remove(tr);
		}
		
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}
	
	static List<Request> pending = new ArrayList<Request>();
	static List<Request> timed = new ArrayList<Request>();
	static List<Request> completed = new ArrayList<Request>();
	
	public static void tick() {
		pending.removeAll(completed);
		completed.clear();
		for(Request r : pending) {
			try {
				r.tick();
			} catch(Exception e) {}
		}
		pending.removeAll(timed);
		timed.clear();
	}
	
	public static class Request {
		public final String asker;
		public final String target;
		public int ticks = 1000;
		public Request(EntityPlayerMP asker, EntityPlayerMP target) {
			this.asker = asker.getCommandSenderName();
			this.target = target.getCommandSenderName();
			init(asker, target);
		}
		public void init(EntityPlayerMP asker, EntityPlayerMP target) {
			println(asker, "&6Request sent! (Timeout is 50 seconds.)");
			println(target, "&6" + asker.getCommandSenderName() + " is requesting to teleport to you!");
			println(target, "&6Type /tpaccept to accept, or /tpadeny to deny.");
			println(target, "&6(Timeout is 50 seconds.)");
			pending.add(this);
		}
		public void accept() {
			EntityPlayerMP a = getPlayer(MinecraftServer.getServer(), asker);
			EntityPlayerMP t = getPlayer(MinecraftServer.getServer(), target);
			FLOTRHooks.warpViaFT(a, t.getCommandSenderName(), t.posX, t.posY, t.posZ, t.dimension);
			println(a, "&a" + target + " &6accepted your request!");
			println(t, "&6Accepted " + asker + "'s request!");
			completed.add(this);
		}
		public void deny() {
			EntityPlayerMP a = getPlayer(MinecraftServer.getServer(), asker);
			EntityPlayerMP t = getPlayer(MinecraftServer.getServer(), target);
			println(a, "&c" + target + " &6denied your request!");
			println(t, "&6Denied " + asker + "'s request!");
			completed.add(this);
		}
		public void tick() {
			ticks--;
			if(ticks <= 0) {
				timeout();
			}
		}
		public void timeout() {
			EntityPlayerMP a = getPlayer(MinecraftServer.getServer(), asker);
			EntityPlayerMP t = getPlayer(MinecraftServer.getServer(), target);
			println(a, "&6Request to " + target + " timed out!");
			println(t, "&6" + asker + "'s request timed out!");
			timed.add(this);
		}
	}
	
	public static class ReverseRequest extends Request {
		public ReverseRequest(EntityPlayerMP asker, EntityPlayerMP target) {
			super(asker, target);
		}
		public void init(EntityPlayerMP asker, EntityPlayerMP target) {
			println(asker, "&6Request sent! (Timeout is 50 seconds.)");
			println(target, asker.getCommandSenderName() + " is requesting for you to teleport to them!");
			println(target, "&6Type /tpaccept to accept, or /tpadeny to deny.");
			println(target, "&6(Timeout is 50 seconds.)");
			pending.add(this);
		}
		public void accept() {
			EntityPlayerMP a = getPlayer(MinecraftServer.getServer(), asker);
			EntityPlayerMP t = getPlayer(MinecraftServer.getServer(), target);
			FLOTRHooks.warpViaFT(t, a.getCommandSenderName(), a.posX, a.posY, a.posZ, a.dimension);
			println(a, "&a" + target + " accepted your request!");
			println(t, "&6Accepted " + asker + "'s request!");
			completed.add(this);
		}
	}
	
	public static void accept(EntityPlayerMP target) {
		for(Request r : pending) {
			if(r.target.equals(target.getCommandSenderName())) {
				try {
					r.accept();
				} catch(Exception e) {}
				return;
			}
		}
		Error("No pending requests!");
	}
	
	public static void deny(EntityPlayerMP target) {
		for(Request r : pending) {
			if(r.target.equals(target.getCommandSenderName())) {
				try {
					r.deny();
				} catch(Exception e) {}
				return;
			}
		}
		Error("No pending requests!");
	}
	
}
