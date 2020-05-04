package vinyarion.fukkit.main.util.collection;

public final class Box<T> {

	private Box() {
		this.boxed = null;
	}

	private Box(T boxed) {
		this.boxed = boxed;
	}

	private T boxed;

	public T getBoxed() {
		return boxed;
	}

	public void setBoxed(T boxed) {
		this.boxed = boxed;
	}

	public static <T> Box<T> of(T boxed) {
		return new Box<T>(boxed);
	}
	public static <T> Box<T> empty() {
		return new Box<T>();
	}

}
