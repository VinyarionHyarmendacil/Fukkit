package vinyarion.fukkit.main.util.memory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.objectweb.asm.Type;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public class RTC {
	private static final sun.misc.Unsafe u = he(()->(sun.misc.Unsafe)ReflectionHelper.getPrivateValue((Class)Class.forName("sun.misc.Unsafe"), null, "theUnsafe"));
	public static final <T> T ni(Class<T> type) {
		return (T)he(()->u.allocateInstance(type));
	}
	public static final <T> T rtc(Object object, Class<T> clazz) {
		T dummy = ni(clazz);
		long type = u.getLong(dummy, 4L);
		u.putLong(object, 4L, type);
		return (T)object;
	}
	public static final Field getVirtualField(Class top, String... names) {
		Field ret = null;
		back: try {
			if(top != null) {
				ret = ReflectionHelper.findField((Class)top, names);
			}
		} catch(UnableToFindFieldException e) {
			top = top.getSuperclass();
			break back;
		}
		if(ret == null) {
			throw new UnableToFindFieldException(names, new Exception());
		}
		return ret;
	}
	public static final Class getType(Object o) {
		if(o instanceof String) {
			return he(()->Class.forName((String)o));
		} else if(o instanceof Class) {
			return (Class)o;
		} else if(o instanceof Type) {
			return he(()->Class.forName(((Type)o).getClassName()));
		} else {
			return Object.class;
		}
	}
	public static final Class[] getTypes(Object... types) {
		Class[] types_ = new Class[types.length];
		for(int i = 0; i < types.length; i++) {
			types_[i] = getType(types[i]);
		}
		return types_;
	}
	public static final Method getVirtualMethod(Object top, Object[] types, String... names) {
		Class top_ = getType(top);
		Method ret = null;
		Class[] types_ = getTypes(types);
		back: try {
			if(top_ != null) {
				ret = ReflectionHelper.findMethod(top_, null, names, types_);
			}
		} catch(UnableToFindMethodException e) {
			top_ = top_.getSuperclass();
			break back;
		}
		if(ret == null) {
			while(top_ != null) {
				for(Method m : top_.getDeclaredMethods()) {
					for(String n : names) {
						if(m.getName().equals(n)) {
							ret = m;
							break;
						}
					}
				}
				top_ = top_.getSuperclass();
			}
		}
		if(ret == null) {
			throw new UnableToFindMethodException(names, new Exception());
		}
		return ret;
	}
	public static final <R> R he(Failable<R> task) {
		return task.get();
	}
	public static interface Failable<T> extends Supplier<T> {
		public T get_() throws Throwable;
		@Override
		public default T get() {
			try {
				return this.get_();
			} catch(Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
	public static class FW<T> {
		public static <T> FW<T> of(Object clazz, String... names) {
			return new FW(ReflectionHelper.findField(getType(clazz), names));
		}
		private FW(Field f) {
			gets = he(()->MethodHandles.lookup().unreflectGetter(f));
			sets = he(()->MethodHandles.lookup().unreflectSetter(f));
		}
		private final MethodHandle gets;
		private final MethodHandle sets;
		private MethodHandle getsb = null;
		private MethodHandle setsb = null;
		private Supplier<Object> supplier = null;
		private boolean isStatic = false;
		public <TT> FW<TT> asStatic() {
			isStatic = true;
			return (FW<TT>)this;
		}
		public <TT> FW<TT> asVirtual() {
			isStatic = false;
			return (FW<TT>)this;
		}
		public <TT> FW<TT> with(Object o) {
			getsb = gets.bindTo(o);
			setsb = sets.bindTo(o);
			return (FW<TT>)this;
		}
		public <TT> FW<TT> with(Supplier<Object> os) {
			supplier = os;
			return (FW<TT>)this;
		}
		public <TT> FW<TT> without() {
			supplier = null;
			getsb = null;
			setsb = null;
			return (FW<TT>)this;
		}
		public T set(T thing) {
			if(isStatic) {
				he(()->sets.invoke(thing));
			} else if(supplier == null) {
				he(()->setsb.invoke(thing));
			} else {
				he(()->sets.invoke(supplier.get(), thing));
			}
			return thing;
		}
		public T get() {
			if(isStatic) {
				return (T)he(()->gets.invoke());
			} else if(supplier == null) {
				return (T)he(()->getsb.invoke());
			} else {
				return (T)he(()->gets.invoke(supplier.get()));
			}
		}
		public T set(Object o, T thing) {
			he(()->sets.invoke(o, thing));
			return thing;
		}
		public T get(Object o) {
			return (T)he(()->gets.invoke(o));
		}
		public Proxy<T> getProxy() {
			return new Proxy<T>(get());
		}
		public <V extends T> Proxy<V> setProxy(Proxy<V> proxy) {
			this.set(proxy.object);
			return proxy;
		}
	}
	public static class MW<T> {
		public static <T> MW<T> of(Object clazz, String[] names, Object... types) {
			return new MW(ReflectionHelper.findMethod(getType(clazz), null, names, getType(types)));
		}
		public static <T> MW<T> ofTypes(Class clazz, String[] names, Class... types) {
			return new MW(ReflectionHelper.findMethod(clazz, null, names, types));
		}
		private MW(Method m) {
			mh = he(()->MethodHandles.lookup().unreflect(m));
		}
		private final MethodHandle mh;
		private MethodHandle mhb = null;
		public MW<T> without() {
			mhb = null;
			return this;
		}
		public MW<T> with(Object o) {
			mhb = he(()->mh.bindTo(o));
			return this;
		}
		public T call(Object... args) {
			return (T)he(()->(mhb == null ? mh : mhb).invoke(args));
		}
		public T calls(Object... args) {
			return (T)he(()->mh.invoke(args));
		}
		public Proxy<T> callProxy(Object... args) {
			return new Proxy<T>(call(args));
		}
		public Proxy<T> callsProxy(Object... args) {
			return new Proxy<T>(calls(args));
		}
	}
	public static class Proxy<T> {
		private T object;
		public Proxy(T object) {
			this.object = object;
		}
		public Class<T> type() {
			return (Class<T>)(this.object == null ? Object.class : this.object.getClass());
		}
		public T value() {
			return this.object;
		}
		public <TT> TT valued() {
			return (TT)this.object;
		}
		public <F> Proxy<F> get(String... names) {
			return this.<F>field(names).getProxy();
		}
		public <F> FW<F> field(String... names) {
			return new FW(getVirtualField(this.type(), names)).with(this.object);
		}
		public <M> Proxy<M> call(Object[] types, String[] names, Object... args) {
			return this.<M>method(types, names).callProxy(args);
		}
		public <M> Proxy<M> calls(Object[] types, String[] names, Object... args) {
			return this.<M>method(types, names).callsProxy(args);
		}
		public <M> MW<M> method(Object[] types, String... names) {
			return new MW(getVirtualMethod(this.type(), types, names)).with(this.object);
		}
	}
}
