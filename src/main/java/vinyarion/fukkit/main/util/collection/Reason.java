package vinyarion.fukkit.main.util.collection;

public interface Reason<K, V> {

	public V process(K key, V value) throws Remove;

	public static void remove() throws Remove {
		throw new Remove();
	}

	public static final class Remove extends Exception {

		public static void remove() throws Remove {
			throw new Remove();
		}

	}

	public static interface VoidReason<K, V> {

		public void process(K key, V value) throws Remove;

		public static void remove() throws Remove {
			throw new Remove();
		}

	}

}
