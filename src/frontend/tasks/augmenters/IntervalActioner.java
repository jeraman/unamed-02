package frontend.tasks.augmenters;

import frontend.Main;

public class IntervalActioner extends AbstractMusicActioner {

	private int root;
	private int interval;
	
	public IntervalActioner(int root, int interval, int velocity, int duration) {
		super(velocity, duration);
		this.root = root;
		this.interval = interval;
	}
	
	protected int getRoot() {
		return root;
	}

	protected void setRoot(int root) {
		this.root = root;
	}

	protected int getInterval() {
		return interval;
	}

	protected void setInterval(int interval) {
		this.interval = interval;
	}

	@Override
	protected void noteOnInSoundEngine() {
		Main.eng.noteOnWithoutAugmenters(0, root, velocity);
		Main.eng.noteOnWithoutAugmenters(0, root+interval, velocity);
	}

	@Override
	protected void noteOffInSoundEngine() {
		Main.eng.noteOff(0, root, velocity);
		Main.eng.noteOff(0, root+interval, velocity);
	}

}
