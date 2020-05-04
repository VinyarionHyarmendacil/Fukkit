package vinyarion.lotrclientutil.common.packets;

import java.util.Set;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketBlockLogClient implements IMessage {
	
	public final Set<Blyat> blyats = Sets.newHashSet();
	
	public PacketBlockLogClient() { }
	
	public void fromBytes(ByteBuf buf) {
		int size = buf.readInt();
		for(int i = 0; i < size; i++) {
			Blyat blyat = new Blyat(buf);
			this.blyats.add(blyat);
		}
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.blyats.size());
		for(Blyat blyat : this.blyats) {
			buf.writeInt(blyat.id);
			buf.writeLong(blyat.time);
			buf.writeInt(blyat.x);
			buf.writeInt(blyat.y);
			buf.writeInt(blyat.z);
			buf.writeInt(blyat.block.length());
			buf.writeBytes(blyat.block.getBytes());
			buf.writeInt(blyat.meta);
			buf.writeBoolean(blyat.broke);
			buf.writeInt(blyat.player.length());
			buf.writeBytes(blyat.player.getBytes());
		}
	}
	
	public static class Blyat {
		public Blyat(ByteBuf buf) {
			this.id = buf.readInt();
			this.time = buf.readLong();
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.z = buf.readInt();
			int l = buf.readInt();
			this.block = new String(buf.readBytes(l).array());
			this.meta = buf.readInt();
			this.broke = buf.readBoolean();
			l = buf.readInt();
			this.player = new String(buf.readBytes(l).array());
		}
		public Blyat(int id, long time, int x, int y, int z, String block, int meta, boolean broke, String player) {
			this.id = id;
			this.time = time;
			this.x = x;
			this.y = y;
			this.z = z;
			this.block = block;
			this.meta = meta;
			this.broke = broke;
			this.player = player;
		}
		public final boolean broke;
		public final int id, x, y, z, meta;
		public final long time;
		public final String block, player;
	}
	
}
