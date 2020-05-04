package vinyarion.fukkit.main.game;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FKit {
	
	public final String name;
	public final String cooldown;
	public final List<ItemStack> items = new ArrayList<ItemStack>();
	
	public FKit(String name, String cooldown) {
		this.name = name;
		this.cooldown = cooldown;
	}
	
	public void loadFrom(EntityPlayer player) {
		for(int i = 0; i < 36; i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(stack != null) {
				items.add(stack);
			}
		}
		player.addChatMessage(Colors.CHAT("Set kit " + name + " with " + items.size() + "!"));
	}
	
	public void loadFrom(NBTTagCompound tag) {
		NBTTagList list = tag.getTagList("items", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			items.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
		}
	}
	
	public void giveTo(EntityPlayer player) {
		List<ItemStack> drop = new ArrayList<ItemStack>();
		for(ItemStack stack : items) {
			ItemStack stackCopy = stack.copy();
			if(!player.inventory.addItemStackToInventory(stackCopy)) {
				drop.add(stackCopy);
			}
		}
		if(!drop.isEmpty()) {
			player.addChatMessage(Colors.CHAT("Not enough space! Dropping " + drop.size() + " items on the ground!"));
			for(ItemStack stack : drop) {
				EntityItem item = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, stack);
				item.delayBeforeCanPickup = 10;
				player.worldObj.spawnEntityInWorld(item);
			}
		}
	}
	
	public NBTTagCompound write() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		NBTTagList list = new NBTTagList();
		tag.setTag("items", list);
		for(ItemStack stack : items) {
			list.appendTag(stack.writeToNBT(new NBTTagCompound()));
		}
		return tag;
	}
	
}
