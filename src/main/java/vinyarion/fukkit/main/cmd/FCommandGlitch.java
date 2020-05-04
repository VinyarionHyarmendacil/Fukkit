package vinyarion.fukkit.main.cmd;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;

public class FCommandGlitch extends FCommand {
	
	public FCommandGlitch(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length > 0, "Not enough args!");
		String op = args[0];
		EntityPlayerMP target = playerFor(sender, op);
		Assert(target != null, "Must specify a player!");
		target.playerNetServerHandler.sendPacket(new S0CPacketSpawnPlayer(target));
		Info(sender, "Glitched " + op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}
	
}
