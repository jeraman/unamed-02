package soundengine.util;

/**
 * Class that counts time in musical terms using BPM, bars, beats, and note
 * values
 * 
 * @author jeronimo
 *
 */
public class Metro implements Runnable {

	private int bpm;
	private int currentBar;
	private int currentBeat;
	private int currentNoteCount;
	private int globalBeat;
	private int globalNoteValue;

	private Thread counter;
	private boolean alive;

	public Metro() {
		this(120);
	}

	public Metro(int bpm) {
		this(bpm, 4, 4);
	}

	public Metro(int bpm, int globalBeat, int globalNoteValue) {
		this.bpm = bpm;
		this.globalBeat = globalBeat;
		this.globalNoteValue = globalNoteValue;
		this.currentBar = 0;
		this.currentBeat = 0;
		this.alive = false;
	}

	public int getBpm() {
		return bpm;
	}

	public void setBpm(int bpm) {
		this.bpm = bpm;
	}

	public int getCurrentBar() {
		return currentBar;
	}

	public void setCurrentBar(int currentBar) {
		this.currentBar = currentBar;
	}

	public int getCurrentBeat() {
		return currentBeat + 1;
	}

	public void setCurrentBeat(int currentBeat) {
		this.currentBeat = currentBeat;
	}

	public int getGlobalBeat() {
		return globalBeat;
	}

	public void setGlobalBeat(int globalBeat) {
		this.globalBeat = globalBeat;
	}

	public int getCurrentNoteCount() {
		return currentNoteCount + 1;
	}

	public void setCurrentNoteCount(int currentNoteCount) {
		this.currentNoteCount = currentNoteCount;
	}
	
	public int getGlobalNoteValue() {
		return globalNoteValue;
	}
	
	public void setGlobalNoteValue(int globalNoteValue) {
		this.globalNoteValue = globalNoteValue;
	}

	public void stop() {
		alive = false;
		try {
			counter.join(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		this.currentBar = 0;
		this.currentBeat = 0;
		this.currentNoteCount = 0;

		alive = true;
		Runnable r = this;
		counter = new Thread(r);
		counter.start();
	}

	@Override
	public void run() {

		while (alive) {
			//int time = (int) ((60 * 1000) / (this.bpm * (this.globalNoteValue)));
			int time = (int) 60 * 1000 / this.bpm;
			
			float mult = this.globalNoteValue/4f;
			int adaptedTime = (int) (time/mult);
			
			Util.delay(adaptedTime);

			//currentNoteCount = (currentNoteCount + 1) % this.globalNoteValue;
			
			//if (currentNoteCount == 0) {
				currentBeat = (currentBeat + 1) % globalBeat;

				if (currentBeat == 0)
					currentBar = currentBar + 1;
			//}
		}
	}

}
