package vinyarion.fukkit.main.cmd;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.data.FWarps;

public class FCommandUnglitch extends FCommand {
	
	public FCommandUnglitch(String phiName) {
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
		target.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(target.getEntityId()));
		FLOTRHooks.warpViaFT(target, "the Realm of the Living", target.posX, target.posY, target.posZ, target.dimension);
//		target.playerNetServerHandler.sendPacket(new S07PacketRespawn(target.dimension, target.worldObj.difficultySetting, target.worldObj.getWorldInfo().getTerrainType(), target.theItemInWorldManager.getGameType()));
		Info(sender, "Unglitched " + op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}
	
}
