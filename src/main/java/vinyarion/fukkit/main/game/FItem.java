package vinyarion.fukkit.main.game;

import net.minecraft.command.CommandBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FItem {
	
	public static final FItem DUMMY = new FItem(Items.stick, "DUMMY", "DUMMY") {
		public boolean equals(ItemStack stack) {
			return false;
		}
	};
	
	public boolean issystem = false;
	public boolean isoverwrite = false;
	public final Item item;
	public final String parentname;
	public final String name;
	public final String display;
	
	public FItem(Item parent, String name, String display) {
		this.parentname = Item.itemRegistry.getNameForObject(parent);
		this.item = parent;
		this.name = name;
		this.display = display;
	}
	
	public FItem(String parentname, String name, String display) {
		this.parentname = parentname;
		this.item = CommandBase.getItemByText(MinecraftServer.getServer(), parentname);
		this.name = name;
		this.display = display;
	}
	
	public NBTTagCompound write() {
		NBTTagCompound ret = new NBTTagCompound();
		ret.setString("item", Short.toString((short)Item.getIdFromItem(item)));
		ret.setString("parent", parentname);
		ret.setString("name", name);
		ret.setString("display", display);
		return ret;
	}
	
	public boolean equals(Object o) {
		return o instanceof ItemStack ? this.equals((ItemStack)o) : this == o;
	}
	
	public boolean equals(ItemStack stack) {
		return stack != null && stack.getItem() == this.item && stack.hasTagCompound() && stack.getTagCompound().getString("FItem").equals(this.name);
	}
	
	public ItemStack stack() {
		return this.stack(defaults.size);
	}
	
	public ItemStack stack(int size) {
		return this.stack(size, defaults.damage);
	}
	
	public ItemStack stack(int size, int damage) {
		return this.stack(size, damage, NBT.copy(defaults.tag));
	}
	
	public ItemStack stack(int size, int damage, NBTTagCompound tag) {
		ItemStack ret = new ItemStack(this.item, size, damage);
		if(tag == null) {
			tag = new NBTTagCompound();
		}
		ret.setTagCompound(tag);
		ret.getTagCompound().setString("FItem", this.name);
		ret.setStackDisplayName(Colors.color("&r" + this.display + "&r"));
		return ret;
	}
	
	private final Defaults defaults = new Defaults();
	
	public Defaults getDefaults() {
		return defaults;
	}
	
	public class Defaults {
		public int size = 1;
		public int damage = 0;
		public NBTTagCompound tag = null;
	}
	
}
