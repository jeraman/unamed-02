package soundengine.util;

/**
 * 
 * @author jeraman.info
 *
 */
class Timer extends AbstractTimeMeter {
	private long startTime;
	private float elapsedTime;
	
	public Timer() {
		super();
	}
	
	float getElapsedTime() {
		return this.elapsedTime;
	}
	
	public void stop() {
		super.stop();
	}
	
	public void start() {
		this.startTime = (long) System.nanoTime();
		this.elapsedTime = 0;
		super.start();
	}
	
	@Override
	public void run() {
		while (this.isAlive()) {
			this.elapsedTime = (System.nanoTime() - this.startTime) / 1000000000.0f;
		}
	}
}
