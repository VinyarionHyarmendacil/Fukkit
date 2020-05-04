package vinyarion.fukkit.main.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import vinyarion.fukkit.main.util.Submittable.HandledRunnable;

public class StreamTaskThreadPool implements Submittable<Runnable, HandledRunnable> {

	private final BlockingQueue<Runnable> tasks;
	private final StreamTaskThread[] threads;

	public StreamTaskThreadPool(int concurrency) {
		this.tasks = new ArrayBlockingQueue<Runnable>(1024, true);
		this.threads = new StreamTaskThread[concurrency];
		for(int i = 0; i < concurrency; i++) {
			(this.threads[i] = new StreamTaskThread(this.tasks)).start();
		}
	}

	public void submit(Runnable task) {
		retry: try {
			this.tasks.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
			break retry;
		}
	}

}
