package vinyarion.fukkit.main.util.memory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.ReflectionHelper;
import sun.misc.Unsafe;

public class Memory {

	public static final boolean is64bit;
	
	private static final Unsafe unsafe;

	static {
		unsafe = getUnsafe();
		is64bit = System.getProperty("os.arch").contains("64");
	}
	
	private static final Unsafe getUnsafe() {
		try {
			return ReflectionHelper.<Unsafe, Object>getPrivateValue((Class)Class.forName("sun.misc.Unsafe"), null, "theUnsafe");
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final <T> T newInstance(Class<T> type) {
		try {
			return (T)unsafe.allocateInstance(type);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final <T> T dynamicIntercept(T thing, Invoker i) {
		long icpt = icptTypes.containsKey(thing.getClass()) ? icptTypes.get(thing.getClass()).longValue() : icptTypes.get(thing.getClass()).longValue();
		unsafe.putLong(thing, 0L, icpt);
		return thing;
	}
	
	public static final <T, U extends T> U forceCastToChild(T object, Class<U> clazz) {
		return forceCastToChild(object, clazz, true);
	}
	
	public static final <T, U extends T> U forceCastToChild(T object, Class<U> clazz, boolean bit64) {
		return (U)forceCast(object, clazz, bit64);
	}
	
	public static final <T, U extends T> T forceCastToParent(U object, Class<T> clazz) {
		return forceCastToParent(object, clazz, true);
	}
	
	public static final <T, U extends T> T forceCastToParent(U object, Class<T> clazz, boolean bit64) {
		return (T)forceCast(object, clazz, bit64);
	}
	
	private static final long _ClassOffset = 4L;
	
	public static final <T, U> T forceCast(Object object, Class<U> clazz) {
		return forceCast(object, clazz, true);
	}
	
	public static final <T, U> T forceCast(Object object, Class<U> clazz, boolean bit64) {
		U dummy = (U)newInstance(clazz);
		if(bit64) {
			long type = unsafe.getLong(dummy, _ClassOffset);
			unsafe.putLong(object, _ClassOffset, type);
		} else {
			int type = unsafe.getInt(dummy, _ClassOffset);
			unsafe.putInt(object, _ClassOffset, type);
		}
		return (T)(Object)object;
	}
	
	public static final void finalize(Field field) {
		try {
			Field modifiers = field.getClass().getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.set(field, modifiers.getInt(field) | Modifier.FINAL);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static long size(Object o) {
		return o == null ? 0L : unsafe.getAddress(unsafe.getInt(o, 4L) + 12L);
	}
	
	private static long normalize(int value) {
		return (value >= 0) ? (value) : ((~0L >>> 32) & value);
	}
	
	private static final Map<Class<?>, Long> icptTypes = Maps.newHashMap();
	
	private static final long klassOffset = 0L;
	
	static final Class<?> define(String dotDollarName, ClassLoader loader, ProtectionDomain domain, ClassNode klazz) {
		ClassWriter writer = new ClassWriter(1);
		klazz.accept(writer);
		byte[] bytes = writer.toByteArray();
		return unsafe.defineClass(dotDollarName, bytes, 0, bytes.length, loader, domain);
	}
	
}
