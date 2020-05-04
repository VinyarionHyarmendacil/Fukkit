package vinyarion.lotrclientutil.common.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketNotifyServer implements IMessage {
	
	public PacketNotifyServer() { }
	
	public void fromBytes(ByteBuf buf) { }
	
	public void toBytes(ByteBuf buf) { }
	
}
