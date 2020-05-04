package vinyarion.fukkit.main.util.collection;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import vinyarion.fukkit.main.util.collection.Reason.Remove;
import vinyarion.fukkit.main.util.collection.Reason.VoidReason;

public interface VMap<K, V> extends Map<K, V>, Iterable<Entry<K, V>> {

	public default Iterator<Entry<K, V>> iterator() {
		return this.entrySet().iterator();
	}

	public default void forEachConsumer(BiConsumer<K, V> biconsumer) {
		if(!this.entrySet().isEmpty()) {
			for(Entry<K, V> entry : this) {
				biconsumer.accept(entry.getKey(), entry.getValue());
			}
//			Iterator<Entry<K, V>> itr = this.iterator();
//			for(Entry<K, V> entry = itr.next(); itr.hasNext(); entry = itr.next()) {
//				biconsumer.accept(entry.getKey(), entry.getValue());
//			}
		}
	}

	public default void forEachPredicate(BiPredicate<K, V> bipredicate) {
		if(!this.entrySet().isEmpty()) {
			Object[] rem = new Object[this.size()];
			int i = 0;
			for(Entry<K, V> entry : this) {
				if(bipredicate.test(entry.getKey(), entry.getValue())) {
					rem[i++] = entry.getKey();
				}
			}
			for(Object o : rem) {
				this.remove(o);
			}
//			Iterator<Entry<K, V>> itr = this.iterator();
//			for(Entry<K, V> entry = itr.next(); itr.hasNext(); entry = itr.next()) {
//				if(bipredicate.test(entry.getKey(), entry.getValue())) {
//					itr.remove();
//				}
//			}
		}
	}

	public default void forEachReason(Reason<K, V> reason) {
		if(!this.entrySet().isEmpty()) {
			Object[] rem = new Object[this.size()];
			int i = 0;
			for(Entry<K, V> entry : this) {
				try {
					V value = reason.process(entry.getKey(), entry.getValue());
					entry.setValue(value);
				} catch(Remove remove) {
					rem[i++] = entry.getKey();
				}
			}
			for(Object o : rem) {
				this.remove(o);
			}
//			Iterator<Entry<K, V>> itr = this.iterator();
//			for(Entry<K, V> entry = itr.next(); itr.hasNext(); entry = itr.next()) {
//				try {
//					entry.setValue(reason.process(entry.getKey(), entry.getValue()));
//				} catch(Remove remove) {
//					itr.remove();
//				}
//			}
		}
	}

	public default void forEachVoidReason(VoidReason<K, V> reason) {
		if(!this.entrySet().isEmpty()) {
			Object[] rem = new Object[this.size()];
			int i = 0;
			for(Entry<K, V> entry : this) {
				try {
					reason.process(entry.getKey(), entry.getValue());
				} catch(Remove remove) {
					rem[i++] = entry.getKey();
				}
			}
			for(Object o : rem) {
				this.remove(o);
			}
//			Iterator<Entry<K, V>> itr = this.iterator();
//			for(Entry<K, V> entry = itr.next(); itr.hasNext(); entry = itr.next()) {
//				try {
//					reason.process(entry.getKey(), entry.getValue());
//				} catch(Remove remove) {
//					itr.remove();
//				}
//			}
		}
	}

	public default <KK, VV> VMap<KK, VV> populate(Consumer<VMap> consumer) {
		consumer.accept(this);
		return (VMap<KK, VV>)this;
	}

	public static <K, V> ArrayMap<K, V> newArrayMap() {
		return new ArrayMap<K, V>();
	}

	public static <K, V> ArrayMap<K, V> newArrayMap(int size) {
		return new ArrayMap<K, V>(size);
	}

	public static <K, V> ListMap<K, V> newListMap() {
		return new ListMap<K, V>();
	}

	public static <K, V> ListMap<K, V> newListMap(int size) {
		return new ListMap<K, V>(size);
	}

	public static <K, V> VHashMap<K, V> newVHashMap() {
		return new VHashMap<K, V>();
	}

	public static <K, V> VHashMap<K, V> newVHashMap(int size) {
		return new VHashMap<K, V>(size);
	}

}
