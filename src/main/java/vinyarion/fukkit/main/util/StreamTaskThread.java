package vinyarion.fukkit.main.util;

import com.google.common.collect.Lists;

import vinyarion.fukkit.main.util.Submittable.HandledRunnable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class StreamTaskThread extends Thread implements Submittable<Runnable, HandledRunnable> {

	private final BlockingQueue<Runnable> tasks;

	public StreamTaskThread() {
		this.tasks = new ArrayBlockingQueue<Runnable>(1024, true);
	}

	public StreamTaskThread(BlockingQueue<Runnable> tasks) {
		this.tasks = tasks;
	}

	public void submit(Runnable task) {
		retry: try {
			this.tasks.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
			break retry;
		}
	}

	public void run() {
		while(!this.isInterrupted()) {
			retry: try {
				this.tasks.take().run();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break retry;
			}
		}
	}

}
