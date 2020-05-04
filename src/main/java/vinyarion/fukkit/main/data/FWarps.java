package vinyarion.fukkit.main.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FWarps {
	
	private static FWarps instance = null;
	
	public static FWarps instance() {
		return instance;
	}
	
	static {
		instance = new FWarps();
		instance.tag = FData.instance().tag("warps");
		FMod.log(Level.INFO, "FWarps initialized!");
	}
	
	private NBTTagCompound tag;
	
	public void warp(EntityPlayerMP sender, String warp) {
		NBTTagCompound w = tag.getCompoundTag(warp);
		int x = w.getInteger("x");
		int y = w.getInteger("y");
		int z = w.getInteger("z");
		int dim = w.getInteger("dim");
		FLOTRHooks.warpViaFT(sender, warp, x, y, z, dim);
//		transferPlayerToDimension(sender, dim, x + 0.5D, y + 0.5D, z + 0.5D);
//		MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(sender, dim);
//		sender.setPositionAndUpdate((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D);
	}
	
	/*
	public void transferPlayerToDimension(EntityPlayerMP player, int newdim, double x, double y, double z) {
		if(newdim == player.dimension) {
			player.mountEntity(null);
			player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
			return;
		}
		player.worldObj.removeEntity(player);
//		WorldServer out = player.getServerForPlayer();
//        player.playerNetServerHandler.sendPacket(new S07PacketRespawn());
//        player.playerNetServerHandler.sendPacket(new S01PacketJoinGame(player.getEntityId(), player.theItemInWorldManager.getGameType(), 
//        		out.getWorldInfo().isHardcoreModeEnabled(), out.provider.dimensionId, out.difficultySetting, 
//        		MinecraftServer.getServer().getConfigurationManager().getMaxPlayers(), out.getWorldInfo().getTerrainType()));
        {
	        int prevdim = player.dimension;
	        WorldServer from = MinecraftServer.getServer().worldServerForDimension(prevdim);
	        WorldServer to = MinecraftServer.getServer().worldServerForDimension(newdim);
	        player.dimension = newdim;
			player.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(player.getEntityId()));
//	        player.playerNetServerHandler.sendPacket(new S01PacketJoinGame(player.getEntityId(), player.theItemInWorldManager.getGameType(), player.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled(), player.getServerForPlayer().provider.dimensionId, player.getServerForPlayer().difficultySetting, MinecraftServer.getServer().getMaxPlayers(), player.getServerForPlayer().getWorldInfo().getTerrainType()));
//	        player.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|Brand", MinecraftServer.getServer().getServerModName().getBytes(Charsets.UTF_8)));
//	        player.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(player.capabilities));
//	        player.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(player.inventory.currentItem));
	        player.playerNetServerHandler.sendPacket(new S07PacketRespawn(newdim, to.difficultySetting, to.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType())); // Forge: Use new dimensions information
	        player.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(player.getPlayerCoordinates().posX, player.getPlayerCoordinates().posY, player.getPlayerCoordinates().posZ));
	        from.removePlayerEntityDangerously(player);
	        player.isDead = false;
	        {
	        	player.setLocationAndAngles(x, y, z, 90.0F, 0.0F);
	            if (player.isEntityAlive()) {
	                from.updateEntityWithOptionalForce(player, false);
	                to.spawnEntityInWorld(player);
	            }
	            player.setWorld(to);
	        }
	        MinecraftServer.getServer().getConfigurationManager().func_72375_a(player, from);
	        player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
	        player.theItemInWorldManager.setWorld(to);
	        MinecraftServer.getServer().getConfigurationManager().updateTimeAndWeatherForPlayer(player, to);
	        MinecraftServer.getServer().getConfigurationManager().syncPlayerInventory(player);
	        Iterator iterator = player.getActivePotionEffects().iterator();
	        while (iterator.hasNext()) {
	            PotionEffect potioneffect = (PotionEffect)iterator.next();
	            player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
	        }
	        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, prevdim, newdim);
	        //Fix for invis after teleport?
//          player.playerNetServerHandler.sendPacket(new S21PacketChunkData(player.getServerForPlayer().getChunkFromBlockCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ)), false, 0));
//			MinecraftServer.getServer().getConfigurationManager().sendToAllNearExcept(player, player.posX, player.posY, player.posZ, 256.0D, player.worldObj.provider.dimensionId, new S0CPacketSpawnPlayer(player));
//	        MinecraftServer.getServer().getConfigurationManager().initializeConnectionToPlayer(player.playerNetServerHandler.netManager, player, player.playerNetServerHandler);
        }
    }
    */
	
	public boolean has(String warp) {
		return tag.hasKey(warp, NBT.COMPOUND);
	}
	
	public void add(ICommandSender sender, String warp, int x, int y, int z, int dim) {
		NBTTagCompound w = new NBTTagCompound();
		w.setInteger("x", x);
		w.setInteger("y", y);
		w.setInteger("z", z);
		w.setInteger("dim", dim);
		tag.setTag(warp, w);
		FData.changed();
	}
	
	public void remove(String warp) {
		tag.removeTag(warp);
		FData.changed();
	}
	
	public void add(ICommandSender sender, String warp) {
		add(sender, warp, sender.getPlayerCoordinates().posX, 
				sender.getPlayerCoordinates().posY, 
				sender.getPlayerCoordinates().posZ, 
				sender.getEntityWorld().provider.dimensionId);
	}
	
	public void add(ICommandSender sender, String warp, int x, int y, int z) {
		add(sender, warp, x, y, z, sender.getEntityWorld().provider.dimensionId);
	}
	
	public List<String> warps() {
		List<String> ret = new ArrayList<String>();
		for(Object o : tag.func_150296_c()) {
			if(o instanceof String) {
				ret.add((String)o);
			}
		}
		return ret;
	}
	
	
	
}
