package vinyarion.fukkit.main.util.nbt;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.nbt.NBTTagCompound;

public class NBTSerializer<T> {
	
	public NBTSerializer(Class<T> clazz) {
		this.clazz = clazz;
		for(Field field : clazz.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if(Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers)) continue;
			
		}
	}
	
	private final Class<T> clazz;
	
	private final BiMap<Field, NBTField> map = HashBiMap.create();
	
	private class NBTField {
		
	}
	
	public void serializeNBT(T instance, NBTTagCompound nbt) {
		
	}
	
	public void deserializeNBT(T instance, NBTTagCompound nbt) {
		
	}
	
}
