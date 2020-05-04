package vinyarion.fukkit.main.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.game.FKit;
import vinyarion.fukkit.main.game.FLoot;
import vinyarion.fukkit.main.util.nbt.NBT;
import vinyarion.fukkit.rpg.tileentity.FTileEntityData;

public class FLootTables {
	
	private static FLootTables instance = null;
	
	public static FLootTables instance() {
		return instance;
	}
	
	static {
		instance = new FLootTables();
		instance.tag = FData.instance().tag("loot_tables");
		if(!instance.tag.hasKey("tables", NBT.LIST)) {
			instance.tag.setTag("tables", new NBTTagList());
		}
		NBTTagList list = instance.tag.getTagList("tables", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			instance.loadLoot(list.getCompoundTagAt(i));
		}
		FMod.log(Level.INFO, "FLootTables initialized!");
	}
	
	private NBTTagCompound tag;
	
	private List<FLoot> loottables = new ArrayList<FLoot>();
	
	public List<String> tables() {
		List<String> names = new ArrayList<String>();
		for(FLoot loot : loottables) {
			names.add(loot.name);
		}
		return names;
	}
	
	public boolean removeLoot(String name) {
		for(FLoot loot : loottables.toArray(new FLoot[loottables.size()])) {
			if(loot.name.equals(name)) {
				loottables.remove(loot);
				NBTTagList list = tag.getTagList("tables", NBT.COMPOUND);
				for(int i = 0; i < list.tagCount(); i++) {
					if(list.getCompoundTagAt(i).getString("name").contains(name)) {
						list.removeTag(i);
						break;
					}
				}
				FData.changed();
				return true;
			}
		}
		return false;
	}
	
	public boolean addLoot(String name, IInventory chest, String cooldown, int min, int max) {
		FData.changed();
		FLoot newloot = new FLoot(name, cooldown, min, max);
		newloot.loadFrom(chest);
		for(FLoot loot : loottables.toArray(new FLoot[loottables.size()])) {
			if(loot.name.equals(name)) {
				loottables.remove(loot);
				loottables.add(newloot);
				NBTTagList list = tag.getTagList("tables", NBT.COMPOUND);
				for(int i = 0; i < list.tagCount(); i++) {
					if(list.getCompoundTagAt(i).getString("name").contains(name)) {
						list.removeTag(i);
						break;
					}
				}
				tag.getTagList("tables", NBT.COMPOUND).appendTag(newloot.write(new NBTTagCompound()));
				return true;
			}
		}
		loottables.add(newloot);
		tag.getTagList("tables", NBT.COMPOUND).appendTag(newloot.write(new NBTTagCompound()));
		return false;
	}
	
	public void loadLoot(NBTTagCompound tag) {
		FLoot loot = new FLoot(tag);
		loottables.add(loot);
	}
	
	public FLoot getLoot(String name) {
		for(FLoot loot : loottables) {
			if(loot.name.equals(name)) {
				return loot;
			}
		}
		return null;
	}
	
	public NBTTagCompound getData(TileEntity chest) {
		return NBT.safe(FTileEntityData.of(chest).getData(), "loot_data");
	}

	public boolean removeData(TileEntity chest) {
		NBTTagCompound data = FTileEntityData.of(chest).getData();
		if(data == null) return false;
		data.removeTag("loot_data");
		return true;
	}
	
	public boolean addData(String name, TileEntity chest) {
		NBTTagCompound data = NBT.ensure(FTileEntityData.of(chest).getData(), "loot_data");
		if(data == null) return false;
		data.setString("table", name);
		return true;
	}

	public boolean isLootChest(TileEntity chest) {
		NBTTagCompound data = FTileEntityData.of(chest).getData();
		if(data == null) return false;
		return data.hasKey("loot_data");
	}
	
}
