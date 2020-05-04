package vinyarion.fukkit.main.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Level;

import vinyarion.fukkit.main.FMod;

public class Later {

	private List<Runnable> list = new ArrayList<Runnable>();
	private List<AtomicInteger> cyclelocks = new ArrayList<AtomicInteger>();
	private Thread current = Thread.currentThread();

	public void queue(Runnable task) {
		synchronized(this.list) {
			this.list.add(task);
		}
	}

	public void resolve() {
		synchronized(this.list) {
			for(Runnable task : this.list) {
				try {
					task.run();
				} catch(Exception e) {
					FMod.log(Level.ERROR, "Problem running a task:");
					e.printStackTrace();
				}
			}
			this.eradicate();
		}
		synchronized(this.cyclelocks) {
			for(AtomicInteger count : this.cyclelocks) {
				count.decrementAndGet();
				synchronized(count) {
					count.notifyAll();
				}
			}
		}
		this.current = Thread.currentThread();
	}

	@Deprecated
	public void eradicate() {
		synchronized(this.list) {
			this.list.clear();
		}
	}

	public boolean isResolver() {
		return Thread.currentThread() == this.current;
	}

	public void waitCycles(int cycles) {
		if(cycles < 1) return;
		if(isResolver()) throw new IllegalStateException("The thread which last resolved tried to wait for " + cycles + " cycles");
		AtomicInteger count = new AtomicInteger(cycles);
		synchronized(this.cyclelocks) {
			this.cyclelocks.add(count);
		}
		synchronized(count) {
			for(int passed = cycles; passed >= 0; passed--) {
				try {
					count.wait();
				} catch (InterruptedException e) { }
				passed = count.get();
			}
		}
		synchronized(this.cyclelocks) {
			this.cyclelocks.remove(count);
		}
	}

}
