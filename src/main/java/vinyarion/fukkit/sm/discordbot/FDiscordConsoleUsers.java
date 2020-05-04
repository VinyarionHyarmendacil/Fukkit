package vinyarion.fukkit.sm.discordbot;

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
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.game.FItem;
import vinyarion.fukkit.main.game.FKit;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FDiscordConsoleUsers {
	
	private static FDiscordConsoleUsers instance = null;
	
	public static FDiscordConsoleUsers instance() {
		return instance;
	}
	
	static {
		instance = new FDiscordConsoleUsers();
		instance.tag = FData.instance().tag("discord_console_users");
		if(!instance.tag.hasKey("users", NBT.LIST)) {
			instance.tag.setTag("users", new NBTTagList());
		}
		FMod.log(Level.INFO, "FDiscordConsoleUsers initialized!");
	}
	
	private NBTTagCompound tag;
	
	public String[] names() {
		List<String> names = new ArrayList<String>();
		NBTTagList list = tag.getTagList("users", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			names.add(list.getStringTagAt(i));
		}
		names = Misc.simplify(names);
		return names.toArray(new String[names.size()]);
	}
	
	public boolean remove(String name) {
		NBTTagList list = tag.getTagList("users", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			if(list.getStringTagAt(i).equals(name)) {
				list.removeTag(i);
				return true;
			}
		}
		return false;
	}
	
	public boolean add(String name) {
		NBTTagList list = tag.getTagList("users", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			if(list.getStringTagAt(i).equals(name)) {
				return false;
			}
		}
		list.appendTag(new NBTTagString(name));
		return true;
	}
	
	public boolean has(String name) {
		NBTTagList list = tag.getTagList("users", NBT.COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			if(list.getStringTagAt(i).equals(name)) {
				return true;
			}
		}
		return false;
	}
	
}
