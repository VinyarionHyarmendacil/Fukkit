package vinyarion.fukkit.main.util;

public interface Submittable<T, U extends T> {

	public void submit(T t);

	public default void submitAlt(U t) {
		this.submit((T)t);
	}

	public static interface HandledRunnable extends Runnable {
		public void runHandled() throws Exception;
		public default void run() {
			try {
				this.runHandled();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
