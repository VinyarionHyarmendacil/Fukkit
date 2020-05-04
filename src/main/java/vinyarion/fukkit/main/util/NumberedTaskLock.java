package vinyarion.fukkit.main.util;

import java.util.List;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class NumberedTaskLock<T> {
	
	public NumberedTaskLock() {
		this.tasks = 0;
	}
	
	private volatile int tasks;
	private final Object lock = new Object();
	private volatile List<Thread> waitingThreads = Lists.newArrayList();
	
	public void addTask(T task) {
		synchronized(lock) {
			if(task != null) {
				tasks++;
			}
		}
	}
	
	public void completeTask(T task) {
		synchronized(lock) {
			if(task != null) {
				tasks--;
				if(tasks <= 0) {
					lock.notifyAll();
				}
			}
		}
	}
	
	public void waitOn() {
		while(tasks > 0) {
			synchronized(lock) {
				try {
					lock.wait();
				} catch(Exception e) {
					
				}
			}
		}
	}
	
}
