package vinyarion.fukkit.main.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.google.common.collect.Lists;

public class FieldHandle<P, T> {

	private Field theField;
	private P theThing = null;

	private FieldHandle(Field field) {
		this.theField = field;
	}

	public FieldHandle withThing(P object) {
		theThing = object;
		return this;
	}

	public T get(P object) {
		try {
			return (T)theField.get(object);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public T get() {
		return get(theThing);
	}

	public void set(P object, T value) {
		try {
			theField.set(object, value);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void set(T value) {
		set(theThing, value);
	}

	public static <P, T> FieldHandle<P, T> of(Field field) {
		return new FieldHandle<P, T>(field);
	}

	public static <P, T> FieldHandle<P, T> of(Field field, P object) {
		return of(field).withThing(object);
	}

	public static MethodHandle method(Class<?> clazz, Object... values) {
		List<String> names = Lists.newArrayList();
		Class<?> ret = null;
		List<Class<?>> classes = Lists.newArrayList();
		for(Object o : values) {
			if(o instanceof Class<?>) {
				if(ret == null) {
					ret = (Class<?>)o;
				} else {
					classes.add((Class<?>)o);
				}
			} else if(o instanceof String) {
				names.add((String)o);
			}
		}
		MethodType type = MethodType.methodType(ret, classes);
		Exception e = null;
		for(String name : names) {
			try {
				return MethodHandles.lookup().findVirtual(clazz, name, type);
			} catch(Exception ex) {
				e = ex;
			}
		}
		throw new RuntimeException(e);
	}

	@Deprecated
	private static class MethodFieldHandle<P, T> extends FieldHandle<P, T> {

		private Method theGetter;
		private Method theSetter;

		private MethodFieldHandle(Method getter, Method setter) {
			super(null);
			this.theGetter = getter;
			this.theSetter = setter;
		}

		public T get(P object) {
			try {
				return (T)theGetter.invoke(object);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

		public void set(P object, T value) {
			try {
				theSetter.invoke(object, value);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

}
