package vinyarion.fukkit.main.handlers;

import java.io.IOException;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.sm.cmd.FCommandInvis;

public final class Invisibility {

	public static final Invisibility INSTANCE = new Invisibility();

	private Invisibility() {}

	public S04PacketEntityEquipment intercept(FNetHandlerPlayServer net, S04PacketEntityEquipment packet) {
		PacketBuffer fbuf = new PacketBuffer(Unpooled.buffer());
		try {
			packet.writePacketData(fbuf);
			int id = fbuf.readInt();
			short slot = fbuf.readShort();
			ItemStack stack = fbuf.readItemStackFromBuffer();
			Entity e = net.playerEntity.worldObj.getEntityByID(id);
			if(e instanceof EntityLivingBase) {
				EntityLivingBase elb = (EntityLivingBase)e;
				if((elb.isPotionActive(Potion.invisibility) && elb.getActivePotionEffect(Potion.invisibility).getAmplifier() >= 1) || 
					(FCommandInvis.invis.contains(elb.getCommandSenderName()))) {
					boolean self = (net.playerEntity.isPotionActive(Potion.invisibility) && net.playerEntity.getActivePotionEffect(Potion.invisibility).getAmplifier() >= 1) || 
							(FCommandInvis.invis.contains(net.playerEntity.getCommandSenderName()));
					if(!self) {
						packet = new S04PacketEntityEquipment(id, slot, null);
					}
				}
			}
		} catch (IOException e) { }
		return packet;
	}

	public Packet intercept(FNetHandlerPlayServer net, S1DPacketEntityEffect packet) {
//		PacketBuffer fbuf = new PacketBuffer(Unpooled.buffer());
//		try {
//			packet.writePacketData(fbuf);
//			int entityid = fbuf.readInt();
//			byte pot = fbuf.readByte();
//			byte amp = fbuf.readByte();
//			short length = fbuf.readShort();
//			if((pot&255) == (Potion.invisibility.id&255)) {
//				Entity e = net.playerEntity.worldObj.getEntityByID(entityid);
//				if(e instanceof EntityLivingBase) {
//					EntityLivingBase elb = (EntityLivingBase)e;
//					if((elb.isPotionActive(Potion.invisibility) && elb.getActivePotionEffect(Potion.invisibility).getAmplifier() >= 1) || 
//						(FCommandInvis.invis.contains(elb.getCommandSenderName()))) {
//						boolean self = (net.playerEntity.isPotionActive(Potion.invisibility) && net.playerEntity.getActivePotionEffect(Potion.invisibility).getAmplifier() >= 1) || 
//								(FCommandInvis.invis.contains(net.playerEntity.getCommandSenderName()));
//						if(self) {
//							return null;
//						} else {
//							return new S1EPacketRemoveEntityEffect(entityid, new PotionEffect(Potion.invisibility.id, length, amp));
//						}
//					}
//				}
//			}
//		} catch (IOException e) { }
		return packet;
	}

	public void every20ticks(EntityPlayer player) {
		{
			for(int i = 0; i < 4; i++) {
				 MinecraftServer.getServer().getConfigurationManager().sendToAllNearExcept(player, player.posX, player.posY, player.posZ, 256.0D, player.worldObj.provider.dimensionId, 
						 new S04PacketEntityEquipment(player.getEntityId(), i + 1, player.getCurrentArmor(i)));
			}
			MinecraftServer.getServer().getConfigurationManager().sendToAllNearExcept(player, player.posX, player.posY, player.posZ, 256.0D, player.worldObj.provider.dimensionId, 
					 new S04PacketEntityEquipment(player.getEntityId(), 0, player.getHeldItem()));
		}
		if((player.isPotionActive(Potion.invisibility) && player.getActivePotionEffect(Potion.invisibility).getAmplifier() >= 2) || 
			(FCommandInvis.invis.contains(player.getCommandSenderName()))) {
			player.getDataWatcher().updateObject(9, Byte.valueOf((byte)0)); //Arrow count
		}
	}

}
