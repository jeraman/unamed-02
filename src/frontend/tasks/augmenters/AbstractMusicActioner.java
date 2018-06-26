package frontend.tasks.augmenters;

import java.io.Serializable;

import frontend.Main;
import soundengine.util.Util;

public abstract class AbstractMusicActioner implements Runnable, Serializable {
	
	protected int duration;
	protected int velocity;
	protected boolean locked;
	
	public AbstractMusicActioner(int velocity, int duration) {
		this.velocity = velocity;
		this.duration = duration;
		this.locked = false;
	}
	
	protected int getDuration() {
		return duration;
	}

	protected void setDuration(int duration) {
		if (!locked)
			this.duration = duration;
	}

	protected int getVelocity() {
		return velocity;
	}

	protected void setVelocity(int velocity) {
		if (!locked)
			this.velocity = velocity;
	}

	public void noteOneAndScheduleKiller() {
		if (!locked) {
			this.locked = true;
			noteOnInSoundEngine();
			this.scheduleNoteKiller();
		}
	}
	
	private void scheduleNoteKiller() {
		Runnable r = this;
		new Thread(r).start();
	}

	@Override
	public void run() {
		Util.delay(this.duration);
		System.out.println("stop playing!");
		noteOffInSoundEngine();
		this.locked = false;
	}

	protected abstract void noteOnInSoundEngine();

	protected abstract void noteOffInSoundEngine();

}
