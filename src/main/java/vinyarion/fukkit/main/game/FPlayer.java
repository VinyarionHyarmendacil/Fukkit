package vinyarion.fukkit.main.game;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRLevelData;
import lotr.common.LOTRTitle.PlayerTitle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.cmd.FCommandNick;
import vinyarion.fukkit.main.cmd.FCommandTitles;
import vinyarion.fukkit.main.data.FRecipes;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.chestguis.FRPGGuis;

public class FPlayer {
	
	public static void notify(EntityPlayerMP player, String message, Vec3 pos) {
		notify(player, message, pos.xCoord, pos.yCoord, pos.zCoord);
	}
	
	public static void notify(EntityPlayerMP player, String message, double x, double y, double z) {
		notify(player, message, x, y, z, 0);
	}
	
	public static void notify(EntityPlayerMP player, String message, double x, double y, double z, int delta) {

		if(FRPGGuis.inWorldNotify.isSelected(player, null)) {
			FLOTRHooks.alignmentSprite(player, message, delta, x+0.5D, y+0.5D, z+0.5D);
		} else {
			player.addChatMessage(Colors.chat(message));
		}
	}
	
	public static void updatePlayerCrafting(EntityPlayerMP player) {
		if(player.openContainer != null) {
			if(player.openContainer instanceof ContainerWorkbench) {
				String name = player.getCommandSenderName();
				ContainerWorkbench bench = (ContainerWorkbench)player.openContainer;
				FRecipes.instance().updatePlayer(player);
				player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(bench.windowId, 0, bench.craftResult.getStackInSlot(0)));
			} else if(player.openContainer instanceof ContainerPlayer && !player.capabilities.isCreativeMode) {
				String name = player.getCommandSenderName();
				ContainerPlayer bench = (ContainerPlayer)player.openContainer;
				FRecipes.instance().updatePlayer(player);
				player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(bench.windowId, 0, bench.craftResult.getStackInSlot(0)));
			}
		}
	}
	
	public static IChatComponent PNS(EntityPlayerMP player) {
		NBTTagCompound tag = FPlayerData.forPlayer(player.getCommandSenderName()).tag();
		String title = "";
		String prefix = "";
		String suffix = "";
		if(FMod.config.doTitles) {
			PlayerTitle st = LOTRLevelData.getData(player).getPlayerTitle();
			if(st != null) {
				StringBuilder stringbuilder = new StringBuilder();
		        Iterator iterator = st.getFullTitleComponent().iterator();
		        while (iterator.hasNext()) {
		            IChatComponent ichatcomponent = (IChatComponent)iterator.next();
		            {
	                    if(ichatcomponent.getChatStyle().getColor() != null) {
	                        stringbuilder.append(ichatcomponent.getChatStyle().getColor());
	                    }
	                    if(ichatcomponent.getChatStyle().getBold()) {
	                        stringbuilder.append(EnumChatFormatting.BOLD);
	                    }
	                    if(ichatcomponent.getChatStyle().getItalic()) {
	                        stringbuilder.append(EnumChatFormatting.ITALIC);
	                    }
	                    if(ichatcomponent.getChatStyle().getUnderlined()) {
	                        stringbuilder.append(EnumChatFormatting.UNDERLINE);
	                    }
	                    if(ichatcomponent.getChatStyle().getObfuscated()) {
	                        stringbuilder.append(EnumChatFormatting.OBFUSCATED);
	                    }
	                    if(ichatcomponent.getChatStyle().getStrikethrough()) {
	                        stringbuilder.append(EnumChatFormatting.STRIKETHROUGH);
	                    }
		            }
		            stringbuilder.append(ichatcomponent.getUnformattedTextForChat());
		            stringbuilder.append(EnumChatFormatting.RESET);
		        }
				title = stringbuilder.toString();
			}
		}
		if(tag.hasKey(FCommandTitles.CHAT_TAGS, NBT.COMPOUND)) {
			NBTTagCompound tags = tag.getCompoundTag(FCommandTitles.CHAT_TAGS);
			prefix = tags.getString("prefix");
			suffix = tags.getString("suffix");
		}
		return Colors.make(title + Colors.RESET + prefix).appendSibling(FCommandNick.name(player)).appendSibling(Colors.make(suffix));
	}

	public static void blankEntireInventory(EntityPlayerMP player) {
		blankContainerInventory(player);
		blankJustInventory(player);
	}

	public static void blankJustInventory(EntityPlayerMP player) {
		blankContainer(player, player.inventoryContainer);
		setHeld(player, null);
	}

	public static void blankContainerInventory(EntityPlayerMP player) {
		if(player.openContainer != null) {
			blankContainer(player, player.openContainer);
		}
	}
	
	public static void resendEntireInventory(EntityPlayerMP player) {
		resendContainerInventory(player);
		resendJustInventory(player);
	}
	
	public static void resendJustInventory(EntityPlayerMP player) {
		resendContainer(player, player.inventoryContainer);
		setHeld(player);
	}
	
	public static void resendContainerInventory(EntityPlayerMP player) {
		if(player.openContainer != null) {
			resendContainer(player, player.openContainer);
		}
	}
	
	private static void blankContainer(EntityPlayerMP player, Container container) {
		for(int i = 0; i < container.inventorySlots.size(); ++i)
            setSlot(player, container, i, null);
	}
	
	private static void resendContainer(EntityPlayerMP player, Container container) {
//		List crafters = ReflectionHelper.getPrivateValue(Container.class, container, "crafters", "field_75149_d", "e");
		for(int i = 0; i < container.inventorySlots.size(); ++i) {
            ItemStack itemstack = ((Slot)container.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack)container.inventoryItemStacks.get(i);
//            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
                itemstack1 = itemstack == null ? null : itemstack.copy();
                container.inventoryItemStacks.set(i, itemstack1);
//                for (int j = 0; j < crafters.size(); ++j) {
                	setSlot(player, container, i, itemstack1);
//                    ((ICrafting)crafters.get(j)).sendSlotContents(container, i, itemstack1);
//                }
//            }
        }
	}
	
	public static void setHeld(EntityPlayerMP player) {
		player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, 0xdeadbeef, player.inventory.getItemStack()));
	}
	
	public static void setHeld(EntityPlayerMP player, ItemStack stack) {
		player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, 0xdeadbeef, stack));
	}
	
	public static void setHand(EntityPlayerMP player) {
		player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, player.inventory.currentItem, player.inventory.getCurrentItem()));
	}
	
	public static void setHand(EntityPlayerMP player, ItemStack stack) {
		player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, player.inventory.currentItem, stack));
	}
	
	public static void setSlot(EntityPlayerMP player, Container container, int slot, ItemStack value) {
		player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(container.windowId, slot, value));
	}
	
	public static void decrementHeld(EntityPlayer player, int number) {
		player.inventory.decrStackSize(player.inventory.currentItem, number);
	}
	
}
