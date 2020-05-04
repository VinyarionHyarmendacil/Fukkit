package vinyarion.fukkit.main.util.deprecated;

import java.util.List;

public interface ThreadSafeList<T> extends List<T> {
	
	public ThreadSafeList<T> resolve();
	
}
