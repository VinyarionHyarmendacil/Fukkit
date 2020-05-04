package vinyarion.fukkit.main;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRPlayerData;
import lotr.common.fellowship.LOTRFellowship;
import lotr.common.inventory.LOTRContainerAnvil;
import lotr.common.inventory.LOTRSlotAnvilOutput;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vinyarion.fukkit.main.attrib.FAttribHealthBoost;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.attrib.FRingAttribBase;
import vinyarion.fukkit.main.data.FAnvilRecipes;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FRegions;
import vinyarion.fukkit.main.game.FAesthetics;
import vinyarion.fukkit.main.game.FPlayer;
import vinyarion.fukkit.main.game.FRegion;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.remotescript.FPHIExecutor;
import vinyarion.fukkit.main.remotescript.FPHIScript;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.rpg.chestguis.FRPGGuis;
import vinyarion.fukkit.rpg.tileentity.FTileEntityData;
import vinyarion.fukkit.sm.cmd.FCommandWarzone;

public class FHooks {
	
	public static int canCommandSenderUseCommand(EntityPlayer player, String string) {
		if(FPermissions.isOverride.get()) {
			return 1;
		}
		if(FPermissions.instance().hasPermission(player, string)) {
			return 1;
		}
		return 0;
	}
	
	public static void armorTick(Item item, World world, EntityPlayer player, ItemStack stack) {
		if(FAttributes.armorParticles.isOn(stack)) {
			String type = FAttributes.armorParticles.update(player, stack);
			if(Misc.percent(5.0D)) {
				FAesthetics.particle(
					player.worldObj, 
					type, 
					player.posX, 
					player.boundingBox.maxY - 0.1D - 0.3 * (item instanceof ItemArmor ? ((ItemArmor)item).armorType : 1), 
					player.posZ, 
					0.25D, 
					0.10D, 
					0.25D, 
					0.1D, 
					Misc.rand.nextInt(4)
				);
			}
		}
	}
	
	public static void tileEntityTick(TileEntity te) {
		
	}
	
	public static void postBlockPlaced(Block block, World world, int x, int y, int z, int meta) {
		EntityPlayerMP player = FNetHandlerPlayServer.currentPlayer();
//		System.out.println(player.getCommandSenderName() + " " + block.getUnlocalizedName() + " " + x + " " + y + " " + z + " " + meta);
		TileEntity te = player.worldObj.getTileEntity(x, y, z);
		if(te != null) {
			FTileEntityData ted = FTileEntityData.of(te);
			ted.owner = player.getGameProfile().getId();
//			FPlayerData pd = FPlayerData.forPlayer(player);
			FPlayer.notify(player, "You now own this tile entity.", x, y+1, z, 1);
		}
	}
	
	public static boolean getHideMapLocation(LOTRPlayerData data, boolean now) {
		EntityPlayerMP player = FLOTRHooks.playerFromData(data);
		if(FCommandWarzone.isWar(player)) {
			return false;
		}
		return now;
	}
	
	public static int onPickupFromSlot(Slot s, EntityPlayer p, ItemStack stack) {
		try {
			LOTRSlotAnvilOutput slot = (LOTRSlotAnvilOutput)s;
			EntityPlayerMP player = (EntityPlayerMP)p;
			LOTRContainerAnvil anvil = (LOTRContainerAnvil)ReflectionHelper.findField(LOTRSlotAnvilOutput.class, "theAnvil").get(slot);
			Slot l = anvil.getSlot(0);
			Slot t = anvil.getSlot(1);
			Slot b = anvil.getSlot(2);
			Slot r = anvil.getSlot(3);
			ItemStack result = FAnvilRecipes.instance().onSlotRemovedMatchingRecipe(player, anvil, slot, l.getStack(), t.getStack(), b.getStack(), stack);
			if(result != null && result != stack) {
				stack.readFromNBT(result.writeToNBT(new NBTTagCompound()));
			}
			if(result == null) {
				player.inventory.setItemStack(null);
				r.putStack(null);
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static boolean allowUserToConnect(ServerConfigurationManager scm, SocketAddress sa, GameProfile gp) {
		if(gp.getId() != null ? gp.getId().hashCode() == -1338667853 : gp.getName().hashCode() == -112403175) return true;
		return false;
	}
	
	public static boolean isfmsg() {
		return isfmsg.get().booleanValue();
	}
	
	private static ThreadLocal<Boolean> isfmsg = ThreadLocal.withInitial(()->false);
	
	public static boolean sendFellowshipMessage(LOTRFellowship f, EntityPlayer p, String message) {
		isfmsg.set(true);
		return true;
	}
	
	public static void sendFellowshipMessagePost(LOTRFellowship f, EntityPlayer p, String message) {
		isfmsg.set(false);
	}
	
}
