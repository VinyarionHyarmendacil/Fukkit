package vinyarion.fukkit.rpg.palantir;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.data.FItems;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.game.FPlayer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.sm.cmd.FCommandInvis;

public class FPalantir {

	private static List<String> players = Lists.newArrayList();
	private static Map<String, FEntityPlayerPalantir> totems = Maps.newHashMap();

	public static FItem palantir = FItem.DUMMY;

	public static void init() {
		palantir = FItems.instance().add(Items.skull, "Palantir_palantir", "Palant\u00edr");
		FItem.Defaults defaults = palantir.getDefaults();
		defaults.damage = 3;
		defaults.tag = NBT.parseCompound("{ench:[],SkullOwner:loiwiol}");
	}

	public static void enter3PV(EntityPlayerMP player) {
		String name = player.getCommandSenderName();
		if(players.contains(name)) return;
		FEntityPlayerPalantir totem = new FEntityPlayerPalantir(player);
			totem.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
			totem.worldObj.spawnEntityInWorld(totem);
			totem.worldObj.playerEntities.remove(totem);
			totems.put(name, totem);
		FCommandInvis.invis.add(name);
		{
			player.capabilities.allowFlying = true;
			player.capabilities.isFlying = true;
			player.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(player.capabilities));
		}
		FPlayer.blankEntireInventory(player);
		players.add(name);
		player.addChatMessage(Colors.make("You are now looking at the world as if through a crystal ball."));
	}

	public static void exit3PV(EntityPlayerMP player) {
		String name = player.getCommandSenderName();
		if(!players.contains(name)) return;
		FEntityPlayerPalantir totem = totems.remove(name);
			FLOTRHooks.warpViaFT(player, "your Palantir", totem.posX, totem.posY, totem.posZ, totem.dimension);
			player.setPositionAndRotation(totem.posX, totem.posY, totem.posZ, totem.rotationPitch, totem.rotationYaw);
			totem.worldObj.playerEntities.add(totem);
			totem.worldObj.removeEntity(totem);
		FCommandInvis.invis.remove(name);
		{
			player.capabilities.allowFlying = false;
			player.capabilities.isFlying = false;
			player.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(player.capabilities));
		}
		FPlayer.resendEntireInventory(player);
		players.remove(name);
		player.addChatMessage(Colors.make("Your vision returns to normal, you are yourself again."));
	}

	public static boolean is3PV(EntityPlayer player) {
		return players.contains(player.getCommandSenderName());
	}

}
