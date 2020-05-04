package vinyarion.fukkit.main.game;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FLoot {
	
	public final String name;
	public final String cooldown;
	public final int min;
	public final int max;
	public final List<ItemStack> items = new ArrayList<ItemStack>();
	
	public FLoot(String name, String cooldown, int min, int max) {
		this.name = name;
		this.cooldown = cooldown;
		this.min = min;
		this.max = max;
	}
	
	public FLoot(NBTTagCompound tag) {
		this.name = tag.getString("name");
		this.cooldown = tag.getString("cooldown");
		this.min = tag.getInteger("min");
		this.max = tag.getInteger("max");
		NBTTagList list = tag.getTagList("items", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			this.items.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
		}
	}
	
	public FLoot loadFrom(IInventory chest) {
		this.items.clear();
		for(int i = 0; i < chest.getSizeInventory(); i++) {
			ItemStack stack = chest.getStackInSlot(i);
			if(stack != null) {
				this.items.add(stack.copy());
			}
		}
		return this;
	}
	
	public void giveTo(IInventory chest) {
		int num = Misc.intInRangeInclusive(min, max);
		if(num >= chest.getSizeInventory()) {
			return;
		}
		for(int i = 0; i < chest.getSizeInventory(); i++) {
			chest.setInventorySlotContents(i, null);
		}
		for(int i = num; i > 0; i--) {
			int s = Misc.rand.nextInt(chest.getSizeInventory());
			if(chest.getStackInSlot(s) == null) {
				chest.setInventorySlotContents(s, this.items.get(Misc.rand.nextInt(this.items.size())).copy());
			} else {
				i++;
			}
		}
	}
	
	public NBTTagCompound write(NBTTagCompound tag) {
		tag.setString("name", this.name);
		tag.setString("cooldown", this.cooldown);
		tag.setInteger("min", this.min);
		tag.setInteger("max", this.max);
		NBTTagList list = new NBTTagList();
		tag.setTag("items", list);
		for(ItemStack stack : this.items) {
			list.appendTag(stack.writeToNBT(new NBTTagCompound()));
		}
		return tag;
	}
	
}
