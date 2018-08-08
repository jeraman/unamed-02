package frontend.tasks.augmenters;

import java.io.Serializable;

import frontend.Main;
import soundengine.SoundEngine;
import soundengine.util.Util;

public abstract class AbstractMusicActioner implements Runnable, Serializable {
	
	protected int duration;
	protected int velocity;
	protected boolean locked;
	transient protected SoundEngine eng;
	
	protected volatile boolean needToTerminate;
	
	private int timerMilestone;

	transient private Thread myStopThread;
	
	public AbstractMusicActioner(int velocity, int duration, SoundEngine eng) {
		this.velocity = velocity;
		this.duration = duration;
		this.locked = false;
		this.eng = eng;
		this.needToTerminate = false;
		this.myStopThread = null;
	}
	
	protected void build (SoundEngine eng) {
		this.eng = eng;
	}
	
	protected int getDuration() {
		return duration;
	}

	protected void setDuration(int duration) {
//		if (!locked)
			this.duration = duration;
	}

	protected int getVelocity() {
		return velocity;
	}

	protected void setVelocity(int velocity) {
//		if (!locked)
			this.velocity = velocity;
	}
	
	protected void start() {
		this.needToTerminate = false;
	}
	
	protected void terminate() {
		this.needToTerminate = true;
	}
	
	private void resetTimer() {
		this.timerMilestone = Util.millis();
	}

	public void noteOnAndScheduleKiller() {
		if (!locked) {
			this.locked = true;
			noteOnInSoundEngine();
			this.scheduleNoteKiller();
		}
	}
	
	private void scheduleNoteKiller() {
		Runnable r = this;
		this.resetTimer();
		if (myStopThread == null) {
			myStopThread = new Thread(r);
			myStopThread.start();
		}
	}
	
	private void waitForDurationOrStop() {
		while ((Util.millis() - this.timerMilestone) < duration && !needToTerminate){
//			System.out.println(needToTerminate);
		}
	}

	@Override
	public void run() {
		waitForDurationOrStop();
		noteOffInSoundEngine();
		this.locked = false;
		this.needToTerminate = false;
		killRunThread();
	}
	
	private void killRunThread() {
		//this.myStopThread.interrupt();
		this.myStopThread = null;
//		try {
//			this.myStopThread.join();
//			this.myStopThread = null;
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	protected abstract void noteOnInSoundEngine();

	protected abstract void noteOffInSoundEngine();

}
