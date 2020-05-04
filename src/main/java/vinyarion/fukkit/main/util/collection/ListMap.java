package vinyarion.fukkit.main.util.collection;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class ListMap<K, V> extends AbstractMap<K, V> implements VMap<K, V> {

	public ListMap() {
		this(16);
	}

	public ListMap(int size) {
		this.entries = new ListSet<Entry<K, V>>(size);
	}

	private ListSet<Entry<K, V>> entries;

	public Set<Entry<K, V>> entrySet() {
		return this.entries;
	}

	private static final class ListSet<T> extends ArrayList<T> implements Set<T> {
		ListSet() {
			super();
		}
//		ListSet(Collection<? extends T> c) {
//			super(c);
//		}
		ListSet(int size) {
			super(size);
		}
	}

}
