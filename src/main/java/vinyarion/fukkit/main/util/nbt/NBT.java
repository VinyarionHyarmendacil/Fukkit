package vinyarion.fukkit.main.util.nbt;

import java.lang.reflect.Array;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class NBT {
	
	public static final int END = 0;
	public static final int BYTE = 1;
	public static final int SHORT = 2;
	public static final int INT = 3;
	public static final int LONG = 4;
	public static final int FLOAT = 5;
	public static final int DOUBLE = 6;
	public static final int BYTE_ARRAY = 7;
	public static final int STRING = 8;
	public static final int LIST = 9;
	public static final int COMPOUND = 10;
	public static final int INT_ARRAY = 11;
	
	public static NBTTagCompound parseCompound(String string) {
		try {
			return (NBTTagCompound)JsonToNBT.func_150315_a(string);
		} catch (NBTException e) {
			return new NBTTagCompound();
		}
	}
	
	public static NBTTagCompound ensure(ItemStack stack) {
		if(stack == null) {
			return null;
		}
		if(!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}
	
	public static NBTTagCompound ensure(ItemStack stack, String key) {
		if(stack == null) {
			return null;
		}
		if(!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		if(key != null && !stack.getTagCompound().hasKey(key, COMPOUND)) {
			NBTTagCompound ins = new NBTTagCompound();
			stack.getTagCompound().setTag(key, ins);
			return ins;
		}
		return stack.getTagCompound().getCompoundTag(key);
	}
	
	public static NBTTagCompound ensure(NBTTagCompound tag, String key) {
		if(tag == null) {
			return null;
		}
		if(!tag.hasKey(key, COMPOUND)) {
			NBTTagCompound ins = new NBTTagCompound();
			tag.setTag(key, ins);
			return ins;
		}
		return tag.getCompoundTag(key);
	}
	
	public static NBTTagCompound safe(NBTTagCompound tag, String key) {
		if(tag == null) {
			return null;
		}
		return tag.getCompoundTag(key);
	}
	
	public static NBTTagCompound overwrite(NBTTagCompound tag, String key) {
		if(tag == null) {
			return null;
		}
		NBTTagCompound ins = new NBTTagCompound();
		tag.setTag(key, ins);
		return ins;
	}
	
	public static NBTTagList ensureList(NBTTagCompound tag, String key) {
		if(tag == null) {
			return null;
		}
		if(!tag.hasKey(key, LIST)) {
			NBTTagList ins = new NBTTagList();
			tag.setTag(key, ins);
			return ins;
		}
		return (NBTTagList)tag.getTag(key);
	}
	
	public static NBTTagList overwriteList(NBTTagCompound tag, String key) {
		if(tag == null) {
			return null;
		}
		NBTTagList ins = new NBTTagList();
		tag.setTag(key, ins);
		return ins;
	}
	
	public static boolean has(ItemStack stack, String tag) {
		return stack != null ? stack.hasTagCompound() ? stack.getTagCompound().hasKey(tag) : false : false;
	}
	
	public static boolean def(NBTTagCompound tag, String key, boolean def) {
		return tag.hasKey(key) ? tag.getBoolean(key) : def;
	}
	
	public static byte def(NBTTagCompound tag, String key, byte def) {
		return tag.hasKey(key) ? tag.getByte(key) : def;
	}
	
	public static short def(NBTTagCompound tag, String key, short def) {
		return tag.hasKey(key) ? tag.getShort(key) : def;
	}
	
	public static int def(NBTTagCompound tag, String key, int def) {
		return tag.hasKey(key) ? tag.getInteger(key) : def;
	}
	
	public static long def(NBTTagCompound tag, String key, long def) {
		return tag.hasKey(key) ? tag.getLong(key) : def;
	}
	
	public static float def(NBTTagCompound tag, String key, float def) {
		return tag.hasKey(key) ? tag.getFloat(key) : def;
	}
	
	public static double def(NBTTagCompound tag, String key, double def) {
		return tag.hasKey(key) ? tag.getDouble(key) : def;
	}
	
	public static String def(NBTTagCompound tag, String key, String def) {
		return tag.hasKey(key) ? tag.getString(key) : def;
	}
	
	public static byte[] def(NBTTagCompound tag, String key, byte[] def) {
		return tag.hasKey(key) ? tag.getByteArray(key) : def;
	}
	
	public static int[] def(NBTTagCompound tag, String key, int[] def) {
		return tag.hasKey(key) ? tag.getIntArray(key) : def;
	}
	
	public static String[] toArrayString(NBTTagList tagList) {
		String[] ret = new String[tagList.tagCount()];
		for(int i = 0; i < tagList.tagCount(); i++) {
			ret[i] = tagList.getStringTagAt(i);
		}
		return ret;
	}
	
	public static List<String> toListString(NBTTagList tagList) {
		List<String> ret = Lists.newArrayList();
		for(int i = 0; i < tagList.tagCount(); i++) {
			ret.add(tagList.getStringTagAt(i));
		}
		return ret;
	}
	
	public static <T, TAG extends NBTBase> T[] toArray(Class<T> type, NBTTagList tagList, Function<TAG, T> function) {
		T[] ret = (T[])Array.newInstance(type, tagList.tagCount());
		for(int i = 0; i < ret.length; i++) {
			ret[i] = function.apply((TAG)ReflectionHelper.<List, NBTTagList>getPrivateValue(NBTTagList.class, tagList, "tagList", "field_74747_a", "b").get(i));
		}
		return ret;
	}

	public static <T, TAG extends NBTBase> List<T> toList(Class<T> type, NBTTagList tagList, Function<TAG, T> function) {
		List<T> ret = Lists.newArrayList();
		for(int i = 0; i < tagList.tagCount(); i++) {
			ret.add(function.apply((TAG)ReflectionHelper.<List, NBTTagList>getPrivateValue(NBTTagList.class, tagList, "tagList", "field_74747_a", "b").get(i)));
		}
		return ret;
	}

	public static NBTTagList toListTagString(String... array) {
		NBTTagList ret = new NBTTagList();
		for(String item : array) {
			ret.appendTag(new NBTTagString(item));
		}
		return ret;
	}

	public static NBTTagList toListTagString(List<String> list) {
		NBTTagList ret = new NBTTagList();
		for(String item : list) {
			ret.appendTag(new NBTTagString(item));
		}
		return ret;
	}

	public static <T, TAG extends NBTBase> NBTTagList toListTag(Function<T, TAG> function, T... array) {
		NBTTagList ret = new NBTTagList();
		for(T item : array) {
			ret.appendTag(function.apply(item));
		}
		return ret;
	}

	public static <T, TAG extends NBTBase> NBTTagList toListTag(Function<T, TAG> function, List<T> list) {
		NBTTagList ret = new NBTTagList();
		for(T item : list) {
			ret.appendTag(function.apply(item));
		}
		return ret;
	}
	
	public static void iterateCompound(NBTTagCompound tag, BiConsumer<String, NBTTagCompound> biconsumer) {
		tag.func_150296_c()
		.forEach(name -> biconsumer
				.accept((String)name, tag
						.getCompoundTag((String)name)));
	}
	
	public static <TAG extends NBTBase> TAG copy(TAG tag) {
		return tag == null ? null : (TAG)tag.copy();
	}
	
	public static boolean hasEnchantment(ItemStack stack, Enchantment ench) {
		if(!stack.hasTagCompound()) return false;
		NBTTagList enchs = stack.getEnchantmentTagList();
		if(enchs == null) return false;
		for(int i = 0; i < enchs.tagCount(); i++) {
			if((int)enchs.getCompoundTagAt(i).getShort("id") == ench.effectId) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasValue(NBTTagList list, String value) {
		if(list == null || value == null) return false;
		for(int i = 0; i < list.tagCount(); i++) {
			if(list.getStringTagAt(i).equals(value)) {
				return true;
			}
		}
		return false;
	}

	public static void append(NBTTagList list, String value) {
		list.appendTag(new NBTTagString(value));
	}
	
}
