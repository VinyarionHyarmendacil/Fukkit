package vinyarion.fukkit.main.util;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class AsyncTaskThread extends Thread {
	
    private static int number = 0;
    
    private static synchronized int next() {
        return number++;
    }
	
	private boolean dead = false;
	private boolean check = false;
	
	public AsyncTaskThread() {
		super("AsyncTaskThread-" + next());
		this.setDaemon(true);
	}
	
	public void runAll(Collection<Runnable> tasks) {
		synchronized(allTasks) {
			allTasks.addAll(tasks);
			check = true;
			allTasks.notifyAll();
		}
	}
	
	public void kill(boolean ignoreDeference) {
		dead = true;
		if(ignoreDeference) {
			this.stop();
		}
	}
	
	private List<Runnable> allTasks = Lists.newArrayList();
	
	public void run() {
		while(!dead) {
			if(!check) {
				wait:
				try {
					synchronized(allTasks) {
						allTasks.wait();
					}
				} catch(Exception e) {
					if(!check) {
						break wait;
					}
				}
			}
			check = false;
			List<Runnable> l;
			synchronized(allTasks) {
				l = ImmutableList.<Runnable>copyOf(allTasks);
				allTasks.clear();
			}
			for(Runnable r : l) {
				try {
					r.run();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
