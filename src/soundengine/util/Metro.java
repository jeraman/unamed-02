package soundengine.util;

public class Metro implements Runnable {
	
	private int bpm;
	private int bar;
	private int beat;
	
	private Thread counter;
	private boolean alive;
	
	public Metro(int bpm) {
		this.bpm = bpm;
		this.bar = 0;
		this.beat = 0;
		this.alive = false;
	}
	
	public int getBar() {
		return this.bar;
	}
	
	public int getBeat() {
		return this.beat;
	}
	
	public void setBar(int newBar) {
		this.beat = newBar;
	}
	
	public void setBeat(int newBeat) {
		this.beat = newBeat;
	}
	
	public void stop() {
		alive = false;
		this.bar = 0;
		this.beat = 0;
		
		try {
			counter.join(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start() {
		alive = true;
		Runnable r = this;
		counter = new Thread(r);
		counter.start();
	}

	@Override
	public void run() {
		
		while (alive) {
			int time = (60 * 1000) / this.bpm;
			Util.delay(time);
			beat = (beat + 1) % 5;

			if (beat == 0)
				bar = bar + 1;
		}	
	}

}
