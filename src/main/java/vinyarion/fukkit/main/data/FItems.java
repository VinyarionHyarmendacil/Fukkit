package vinyarion.fukkit.main.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.game.FKit;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FItems {
	
	private static FItems instance = null;
	
	public static FItems instance() {
		return instance;
	}
	
	static {
		instance = new FItems();
		instance.tag = FData.instance().tag("custom_items");
		if(!instance.tag.hasKey("items", NBT.LIST)) {
			instance.tag.setTag("items", new NBTTagList());
		}
		NBTTagList list = instance.tag.getTagList("items", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			instance.load(list.getCompoundTagAt(i));
		}
		FMod.log(Level.INFO, "FItems initialized!");
	}
	
	private NBTTagCompound tag;
	
	private List<FItem> items = new ArrayList<FItem>();
	
	public List<String> names() {
		List<String> names = new ArrayList<String>();
		for(FItem item : items) {
			names.add(item.name);
		}
		return Misc.simplify(names);
	}
	
	public boolean remove(String name) {
		for(FItem item : items.toArray(new FItem[items.size()])) {
			if(item.name.equals(name)) {
				items.remove(item);
				tag.removeTag(name);
				NBTTagList list = tag.getTagList("items", NBT.COMPOUND);
				for(int i = 0; i < list.tagCount(); i++) {
					if(list.getCompoundTagAt(i).getString("name").contains(name)) {
						list.removeTag(i);
						break;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public FItem add(Item parent, String name, String display) {
		return this.add(Item.itemRegistry.getNameForObject(parent), name, display);
	}
	
	public FItem add(String parent, String name, String display) {
		FItem newitem = new FItem(parent, name, display);
		for(FItem item : items.toArray(new FItem[items.size()])) {
			if(item.name.equals(name)) {
				items.remove(item);
				items.add(newitem);
				NBTTagList list = tag.getTagList("items", NBT.COMPOUND);
				for(int i = 0; i < list.tagCount(); i++) {
					if(list.getCompoundTagAt(i).getString("name").contains(name)) {
						list.removeTag(i);
						break;
					}
				}
				list.appendTag(newitem.write());
				newitem.isoverwrite = true;
				return newitem;
			}
		}
		tag.getTagList("items", NBT.COMPOUND).appendTag(newitem.write());
		items.add(newitem);
		return newitem;
	}
	
	public void load(NBTTagCompound tag) {
		FItem newitem = new FItem(
				tag.getString("parent"), 
				tag.getString("name"), 
				tag.getString("display"));
		items.add(newitem);
	}
	
	public FItem get(String name) {
		for(FItem item : items) {
			if(item.name.equals(name)) {
				return item;
			}
		}
		return null;
	}
	
}
