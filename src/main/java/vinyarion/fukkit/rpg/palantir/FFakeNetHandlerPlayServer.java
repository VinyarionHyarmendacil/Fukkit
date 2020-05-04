package vinyarion.fukkit.rpg.palantir;

import lotr.common.LOTRNetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import vinyarion.fukkit.main.FNetHandlerPlayServer;

public class FFakeNetHandlerPlayServer extends FNetHandlerPlayServer {

	public FFakeNetHandlerPlayServer(FEntityPlayerPalantir totem) {
		super(totem);
	}

	@Override
	public void onNetworkTick() {
	}

	@Override
	public void kickPlayerFromServer(String string) {
	}

	@Override
	public void sendPacket(Packet packet) {
	}

	@Override
	public void processKeepAlive(C00PacketKeepAlive packet) {
	}

	@Override
	public void processChatMessage(C01PacketChatMessage packet) {
	}

	@Override
	public void processUseEntity(C02PacketUseEntity packet) {
	}

	@Override
	public void processPlayer(C03PacketPlayer packet) {
	}

	@Override
	public void processPlayerDigging(C07PacketPlayerDigging packet) {
	}

	@Override
	public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packet) {
	}

	@Override
	public void processHeldItemChange(C09PacketHeldItemChange packet) {
	}

	@Override
	public void processAnimation(C0APacketAnimation packet) {
	}

	@Override
	public void processEntityAction(C0BPacketEntityAction packet) {
	}

	@Override
	public void processInput(C0CPacketInput packet) {
	}

	@Override
	public void processCloseWindow(C0DPacketCloseWindow packet) {
	}

	@Override
	public void processClickWindow(C0EPacketClickWindow packet) {
	}

	@Override
	public void processConfirmTransaction(C0FPacketConfirmTransaction packet) {
	}

	@Override
	public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packet) {
	}

	@Override
	public void processEnchantItem(C11PacketEnchantItem packet) {
	}

	@Override
	public void processUpdateSign(C12PacketUpdateSign packet) {
	}

	@Override
	public void processPlayerAbilities(C13PacketPlayerAbilities packet) {
	}

	@Override
	public void processTabComplete(C14PacketTabComplete packet) {
	}

	@Override
	public void processClientSettings(C15PacketClientSettings packet) {
	}

	@Override
	public void processClientStatus(C16PacketClientStatus packet) {
	}

	@Override
	public void processVanilla250Packet(C17PacketCustomPayload packet) {
	}

}
