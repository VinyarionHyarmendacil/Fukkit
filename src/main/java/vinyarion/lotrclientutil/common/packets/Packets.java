package vinyarion.lotrclientutil.common.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class Packets<T extends IMessage> implements IMessageHandler<T, IMessage> {

	private static IHandler handlerimpl;
	
	public static void setHandler(IHandler handler) {
		handlerimpl = handler;
	}
	
	public IMessage onMessage(T message, MessageContext ctx) {
		if(handlerimpl != null) {
			handlerimpl.handle(message, ctx);
		}
		return null;
	}

	public static void registerTo(SimpleNetworkWrapper net) {
		int disc = 0;
		net.registerMessage(new Packets(){}, PacketNotifyServer.class, disc++, Side.SERVER);
		net.registerMessage(new Packets(){}, PacketNotifyClient.class, disc++, Side.CLIENT);
		net.registerMessage(new Packets(){}, PacketBlockLogClient.class, disc++, Side.CLIENT);
	}

}
