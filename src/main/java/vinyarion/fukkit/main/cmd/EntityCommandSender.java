package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityCommandSender implements ICommandSender {
	
	private final ICommandSender sender;
	private final Entity entity;
	public final Vec3 pos;
	
	public EntityCommandSender(ICommandSender sender, Entity entity) {
		this.sender = sender;
		this.entity = entity;
		this.pos = Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
	}
	
	public EntityCommandSender(ICommandSender sender, Entity entity, Vec3 pos) {
		this.sender = sender;
		this.entity = entity;
		this.pos = pos;
	}
	
	public String getCommandSenderName() {
		return entity.getCommandSenderName();
	}
	
	public IChatComponent func_145748_c_() {
		return new ChatComponentText(this.getCommandSenderName());
	}
	
	public void addChatMessage(IChatComponent c) {
		if(sender == null) {
			MinecraftServer.getServer().addChatMessage(new ChatComponentText(this.getCommandSenderName() + " ").appendSibling(c));
		} else {
			sender.addChatMessage(c);
		}
	}
	
	public boolean canCommandSenderUseCommand(int i, String s) {
		return sender == null ? false : sender.canCommandSenderUseCommand(i, s);
	}
	
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(MathHelper.floor_double(pos.xCoord), MathHelper.floor_double(pos.yCoord), MathHelper.floor_double(pos.zCoord));
	}
	
	public World getEntityWorld() {
		return entity.worldObj;
	}
	
}
