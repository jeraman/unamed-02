package frontend.tasks.augmenters;

import java.io.Serializable;

import frontend.Main;
import soundengine.util.Util;

public class NoteMaker implements Runnable, Serializable {
	
	private int duration;
	private int pitch;
	private int velocity;
	private boolean locked;
	
	public NoteMaker(int pitch, int velocity, int duration) {
		this.pitch = pitch;
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

	protected int getPitch() {
		return pitch;
	}

	protected void setPitch(int pitch) {
		if (!locked)
			this.pitch = pitch;
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
			System.out.println("noting on..." + pitch + " " + velocity);
			this.locked = true;
			Main.eng.noteOn(0, pitch, velocity);
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
		Main.eng.noteOff(0, pitch, 0);
		this.locked = false;
	}

}
