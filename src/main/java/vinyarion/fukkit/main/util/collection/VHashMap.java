package vinyarion.fukkit.main.util.collection;

import java.util.HashMap;
import java.util.Map;

public class VHashMap<K, V> extends HashMap<K, V> implements VMap<K, V> {

	VHashMap() {
		super();
	}

	VHashMap(int arg0, float arg1) {
		super(arg0, arg1);
	}

	VHashMap(int arg0) {
		super(arg0);
	}

	VHashMap(Map<? extends K, ? extends V> arg0) {
		super(arg0);
	}

}
