package vinyarion.fukkit.main.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.game.FKit;
import vinyarion.fukkit.main.game.FLoot;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FArmorSetEffects {
	
	private static FArmorSetEffects instance = null;
	
	public static FArmorSetEffects instance() {
		return instance;
	}
	
	static {
		instance = new FArmorSetEffects();
		instance.tag = FData.instance().tag("armorset_effects");
		FMod.log(Level.INFO, "FArmorSetEffects initialized!");
	}
	
	private NBTTagCompound tag;
	
	public boolean remove(String name) {
		FData.changed();
		if(tag.hasKey(name)) {
			tag.removeTag(name);
			return true;
		}
		return false;
	}
	
	public boolean add(String name, String effects) {
		FData.changed();
		if(tag.hasKey(name)) {
			tag.setString(name, effects);
			return true;
		} else {
			tag.setString(name, effects);
			return false;
		}
	}
	
	public String get(String name) {
		return tag.getString(name);
	}
	
	public String[] sets() {
		return (String[])tag.func_150296_c().toArray(new String[0]);
	}
	
}
