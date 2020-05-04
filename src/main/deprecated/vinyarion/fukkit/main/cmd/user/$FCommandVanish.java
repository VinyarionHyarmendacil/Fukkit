package vinyarion.fukkit.main.cmd.user;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.Unpooled;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.cmd.FCommand;

@Deprecated
public class $FCommandVanish extends FCommand {
	
	public $FCommandVanish(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender args) {
		return "/" + getCommandName() + " [<player> [<vanish?>]]";
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
		boolean is = vanishedIDS.contains(player.getEntityId());
		if(args.length >= 2) {
			try {
				is = Boolean.parseBoolean(args[1]);
			} catch(Exception e) {
				Error("Unrecognizable boolean '" + args[1] + "'!");
			}
		}
		int id = player.getEntityId();
		if(is) {
			vanishedIDS.remove((Integer)id);
			MinecraftServer.getServer().getConfigurationManager().sendToAllNearExcept(player, player.posX, player.posY, player.posZ, 256.0D, player.dimension, new S0CPacketSpawnPlayer(player));
		} else {
			vanishedIDS.add((Integer)id);
			MinecraftServer.getServer().getConfigurationManager().sendToAllNearExcept(player, player.posX, player.posY, player.posZ, 256.0D, player.dimension, new S13PacketDestroyEntities(id));
		}
		Info(sender, (!is ? "&aEnabled" : "&cDisabled") + " &rvanish for " + player.getCommandSenderName());
		if(sender != player) {
			Info(player, (!is ? "&aEnabled" : "&cDisabled") + " &rvanish for " + player.getCommandSenderName());
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? new String[]{"true", "false"} : null;
	}
	
	public static List<Integer> vanishedIDS = new ArrayList<Integer>();
	
	public static boolean doNotSend(Packet packet) {
		try {
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			packet.writePacketData(buf);
			return vanishedIDS.contains(buf.readInt()) ? true : vanishedIDS.contains(buf.readVarIntFromBuffer());
		} catch(Exception e) {
			return false;
		}
	}
	
}
