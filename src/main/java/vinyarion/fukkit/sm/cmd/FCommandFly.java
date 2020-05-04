package vinyarion.fukkit.sm.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.util.Colors;

public class FCommandFly extends FCommand {
	
	public FCommandFly(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender args) {
		return "/" + getCommandName() + " [<player>] [<fly?>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayerMP player;
		if(args.length == 0) {
			Assert(sender instanceof EntityPlayerMP, "Must be a player!");
			player = (EntityPlayerMP)sender;
		} else {
			player = playerFor(sender, args[0]);
		}
		Assert(player != null, "Must match only one player!");
		boolean is = !player.capabilities.allowFlying;
		if(args.length >= 2) {
			try {
				is = Boolean.parseBoolean(args[1]);
			} catch(Exception e) {
				Error("Unrecognizable boolean '" + args[1] + "'!");
			}
		}
		player.capabilities.allowFlying = is;
		player.capabilities.isFlying = is;
		player.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(player.capabilities));
		Info(sender, (is ? "&aEnabled" : "&cDisabled") + " &rfly for " + player.getCommandSenderName());
		if(sender != player) {
			Info(player, (is ? "&aEnabled" : "&cDisabled") + " &rfly for " + player.getCommandSenderName());
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}
	
}
