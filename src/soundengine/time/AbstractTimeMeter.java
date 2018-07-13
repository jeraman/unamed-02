package soundengine.time;

import java.io.Serializable;

/**
 * Abstract class used by metro and timer.
 * @author jeronimo
 *
 */
abstract class AbstractTimeMeter implements Runnable, Serializable {
	
	transient private Thread counter;
	transient private boolean alive;

	public AbstractTimeMeter() {
		this.setAlive(false);
	}
	
	public void start() {
		this.setAlive(true);
		Runnable r = this;
		this.counter = new Thread(r);
		this.counter.start();
	}
	
	public synchronized void setAlive( boolean alive) {
		this.alive = alive;
	}
	
	public synchronized boolean isAlive() {
		return this.alive;
	}
	
	public void stop() {
		if (!this.alive) return;
		this.setAlive(false);
		try {
			this.counter.join(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void run();
}
