package vinyarion.fukkit.main.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.game.FKit;
import vinyarion.fukkit.main.util.Misc;

public class FKits {
	
	private static FKits instance = null;
	
	public static FKits instance() {
		return instance;
	}
	
	static {
		instance = new FKits();
		instance.tag = FData.instance().tag("kits");
		for(Object o : instance.tag.func_150296_c()) {
			String name = (String)o;
			instance.load(name, instance.tag.getCompoundTag(name));
		}
		FMod.log(Level.INFO, "FKits initialized!");
	}
	
	private NBTTagCompound tag;
	
	private List<FKit> kits = new ArrayList<FKit>();
	
	public String[] kits() {
		List<String> names = new ArrayList<String>();
		for(FKit kit : kits) {
			names.add(kit.name);
		}
		names = Misc.simplify(names);
		return names.toArray(new String[names.size()]);
	}
	
	public boolean remove(String name) {
		for(FKit kit : kits.toArray(new FKit[kits.size()])) {
			if(kit.name.equals(name)) {
				kits.remove(kit);
				tag.removeTag(name);
				return true;
			}
		}
		return false;
	}
	
	public boolean add(String name, EntityPlayer player, String cooldown) {
		FKit newkit = new FKit(name, cooldown);
		newkit.loadFrom(player);
		for(FKit kit : kits.toArray(new FKit[kits.size()])) {
			if(kit.name.equals(name)) {
				kits.remove(kit);
				kits.add(newkit);
				tag.setTag(name, newkit.write());
				return true;
			}
		}
		kits.add(newkit);
		return false;
	}
	
	public void load(String name, NBTTagCompound tag) {
		FKit newkit = new FKit(name, tag.getString("cooldown"));
		newkit.loadFrom(tag);
		kits.add(newkit);
	}
	
	public FKit get(String name) {
		for(FKit kit : kits) {
			if(kit.name.equals(name)) {
				return kit;
			}
		}
		return null;
	}
	
}
