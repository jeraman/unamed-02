package frontend.tasks.augmenters;

import frontend.Main;
import soundengine.SoundEngine;

public class IntervalActioner extends AbstractMusicActioner {

	private int root;
	private int interval;

	public IntervalActioner(int root, int interval, int velocity, int duration, SoundEngine eng) {
		super(velocity, duration, eng);
		this.root = root;
		this.interval = interval;
	}

	protected int getRoot() {
		return root;
	}

	protected void setRoot(int root) {
		//if (!this.locked)
		if (this.root != root) {
			this.noteOffInSoundEngine();
			this.root = root;
		}
	}

	protected int getInterval() {
		return interval;
	}

	protected void setInterval(int interval) {
		//if (!this.locked)
		if (this.interval != interval) {
			this.noteOffInSoundEngine();
			this.interval = interval;
		}
	}

	@Override
	protected void noteOnInSoundEngine() {
		if (root == -1) return;
		this.eng.noteOnWithoutAugmenters(0, root, velocity);
		this.eng.noteOnWithoutAugmenters(0, root + interval, velocity);
	}

	@Override
	protected void noteOffInSoundEngine() {
		if (root == -1) return;
		this.eng.noteOff(0, root, velocity);
		this.eng.noteOff(0, root + interval, velocity);
	}

}
