package vinyarion.fukkit.main.game;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class FAesthetics {
	
    public static void particle(World world, String name, double x, double y, double z, double vx, double vy, double vz, double size, int number) {
    	MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(
			new S2APacketParticles(
				name, 
				(float)x, 
				(float)y, 
				(float)z, 
				(float)vx, 
				(float)vy, 
				(float)vz, 
				(float)size, 
				number
			), 
			world.provider.dimensionId
		);
    }
    
    public static void soundDimPos(World world, String name, double x, double y, double z, float v, float p) {
    	MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(
			new S29PacketSoundEffect(
				name, 
				x, 
				y, 
				z, 
				v, 
				p
			), 
			world.provider.dimensionId
		);
    }
    
    public static void soundDimGen(World world, String name, float v, float p) {
    	for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
    		EntityPlayerMP player = (EntityPlayerMP)o;
    		if(player.dimension == world.provider.dimensionId) {
        		player.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(
    				name, 
    				player.posX, 
    				player.posY, 
    				player.posZ, 
    				v, 
    				p
    			));
    		}
    	}
    }
    
    public static void soundPlayer(EntityPlayerMP player, String name, float v, float p) {
    	player.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(
    		name, 
    		player.posX, 
    		player.posY, 
    		player.posZ, 
    		v, 
    		p
    	));
    }
    
    public static class Particle {
    	
    	public static String blockcrack(Block block, int color) {
    		return "blockcrack_" + Block.getIdFromBlock(block) + "_" + color;
    	}
    	
    	public static String blockdust(Block block, int color) {
    		return "blockdust_" + Block.getIdFromBlock(block) + "_" + color;
    	}
    	
    }
	
}
