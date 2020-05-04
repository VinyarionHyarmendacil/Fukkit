package vinyarion.fukkit.main.util;

public class IncrementingString {

	public final String prefix;

	public final char[] chars;

	public final int[] positions;

	public final int charslength;

	public final int genlength;

	public final int maxlength;

	public IncrementingString(String prefix, String chars, int maxlength) {
		this.prefix = prefix;
		this.chars = chars.toCharArray();
		this.charslength = chars.length();
		this.maxlength = maxlength;
		this.genlength = maxlength - this.charslength;
		this.positions = new int[this.genlength];
	}

	public synchronized void increment() {
		for(int i = this.genlength - 1; i >= 0; i--) {
			this.positions[i]++;
			if(this.positions[i] >= this.charslength) {
				this.positions[i] = 0;
			} else {
				return;
			}
		}
	}

	public synchronized String getNext() {
		StringBuffer ret = new StringBuffer(this.prefix);
		for(int i = 0; i < this.genlength; i++) {
			ret.append(this.chars[this.positions[i]]);
		}
		return ret.toString();
	}

	public synchronized String getAndInc() {
		String ret = this.getNext();
		this.increment();
		return ret;
	}

	public synchronized String incAndGet() {
		this.increment();
		return this.getNext();
	}

}
