package vinyarion.fukkit.main.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

public class ArrayMap<K, V> implements VMap<K, V> {

	public ArrayMap() {
		this(16);
	}

	public ArrayMap(int size) {
		this.keys = new Object[size];
		this.values = new Object[size];
	}

	private int size;
	private Object[] keys;
	private Object[] values;

	public int size() {
		return this.size;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public boolean containsKey(Object key) {
		for(int i = 0; i < size; i++)
			if(Objects.equals(this.keys[i], key))
				return true;
		return false;
	}

	public boolean containsValue(Object value) {
		for(int i = 0; i < size; i++)
			if(Objects.equals(this.values[i], value))
				return true;
		return false;
	}

	public V get(Object key) {
		for(int i = 0; i < size; i++)
			if(Objects.equals(this.keys[i], key))
				return (V)this.values[i];
		return null;
	}

	public V put(K key, V value) {
		for(int i = 0; i < size; i++)
			if(Objects.equals(this.keys[i], key)) {
				V ret = (V)this.values[i];
				this.values[i] = value;
				return ret;
			}
		this.keys[this.size] = key;
		this.values[this.size] = value;
		this.size++;
		if(this.keys.length <= this.size) {
			Object[] oldKeys = this.keys;
			Object[] oldValues = this.values;
			this.keys = new Object[this.size * 2];
			this.values = new Object[this.size * 2];
			System.arraycopy(oldKeys, 0, this.keys, 0, oldKeys.length);
			System.arraycopy(oldValues, 0, this.values, 0, oldValues.length);
		}
		return null;
	}

	public V remove(Object key) {
		for(int i = 0; i < size; i++)
			if(Objects.equals(this.keys[i], key)) {
				V ret = (V)this.values[i];
				this.size--;
				if(i != this.size) {
					this.values[i] = this.values[this.size];
					this.values[size] = null;
				} else {
					this.values[i] = null;
				}
				return ret;
			}
		return null;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		m.entrySet().forEach(e->this.put(e.getKey(), e.getValue()));
	}

	public void clear() {
		this.size = 0;
		Arrays.fill(this.keys, null);
		Arrays.fill(this.values, null);
	}

	public Set<K> keySet() {
		return (Set<K>)Sets.newHashSet(this.keys);
	}

	public Collection<V> values() {
		return (Collection<V>)Arrays.asList(this.values);
	}

	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, V>> ret =  new HashSet();
		for(int i = 0; i < this.size(); i++) {
			ret.add(new ArrayEntry<K, V>(i));
		}
		return ret;
	}

	private class ArrayEntry<KK, VV> implements Entry<KK, VV> {

		ArrayEntry(int idx) {
			this.idx = idx;
		}

		private final int idx;

		public KK getKey() {
			return (KK)keys[this.idx];
		}

		public VV getValue() {
			return (VV)values[this.idx];
		}

		public VV setValue(VV value) {
			VV ret = (VV)values[this.idx];
			values[this.idx] = (V)value;
			return ret;
		}
		
	}

}
