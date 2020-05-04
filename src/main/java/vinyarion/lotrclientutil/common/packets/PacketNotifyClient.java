package vinyarion.lotrclientutil.common.packets;

import java.util.Set;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketNotifyClient implements IMessage {
	
	public final Set<String> strings = Sets.newHashSet();
	
	public PacketNotifyClient() { }
	
	public void fromBytes(ByteBuf buf) {
		int size = buf.readInt();
		for(int i = 0; i < size; i++) {
			int length = buf.readInt();
			byte[] bytes = buf.readBytes(length).array();
			String string = new String(bytes);
			this.strings.add(string);
		}
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.strings.size());
		for(String string : this.strings) {
			byte[] bytes = string.getBytes();
			buf.writeInt(bytes.length);
			buf.writeBytes(bytes);
		}
	}
	
}
