package vinyarion.fukkit.rpg.palantir;

import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class FFakeNetworkManager extends NetworkManager {

	public FFakeNetworkManager() {
		super(false);
	}

	@Override
	public void scheduleOutboundPacket(Packet p_150725_1_, GenericFutureListener... p_150725_2_) {
		
	}

	@Override
	public void processReceivedPackets() {
		
	}

}
