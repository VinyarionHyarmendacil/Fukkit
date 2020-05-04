package vinyarion.fukkit.main.util.deprecated;

import java.util.ArrayList;
import java.util.Collection;

public class SRArrayList<T> extends ArrayList<T> implements SRList<T> {
	
	public SRArrayList() {
		super();
	}
	
	public SRArrayList(Collection<? extends T> c) {
		super(c);
	}
	
	public SRArrayList(int initialCapacity) {
		super(initialCapacity);
	}
	
	private final ArrayList<T> add = new ArrayList<T>();
	private final ArrayList<T> rem = new ArrayList<T>();
	private boolean mod = false;
	
	public SRArrayList<T> init() {
		mod = true;
		return this;
	}
	
	public SRArrayList<T> resolve() {
		for(T t : add) {
			this.add(t);
		}
		for(T t : rem) {
			this.remove(t);
		}
		mod = false;
		return this;
	}
	
	public boolean add(T e) {
		if(mod) {
			return add.add(e);
		}
		return super.add(e);
	}
	
	public boolean addAll(Collection<? extends T> c) {
		if(mod) {
			return add.addAll(c);
		}
		return super.addAll(c);
	}
	
	public boolean remove(Object o) {
		if(mod) {
			rem.add((T)o);
			return this.contains(o);
		}
		return super.remove(o);
	}
	
	public boolean removeAll(Collection<?> c) {
		boolean ret = false;
		for(Object o : c) {
			ret |= this.remove(o);
		}
		return ret;
	}
	
}
