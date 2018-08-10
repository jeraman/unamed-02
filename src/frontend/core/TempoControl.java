package frontend.core;

import java.io.Serializable;

import frontend.ui.TempoWindowUi;
import soundengine.time.TimeManager;

public class TempoControl implements Serializable {

	private TempoWindowUi ui;
	private TimeManager tm;
	
	private static final int defaultBPM = 120;
	private static final int defaultBeat = 4;
	private static final int defaultNoteValue = 4;
	private boolean shouldUpdate;
	
	public TempoControl() {
		this.tm = new TimeManager(defaultBPM, defaultBeat, defaultNoteValue);
		this.ui = new TempoWindowUi(tm);
		this.shouldUpdate = false;
	}
	
	public void start() {
		this.tm.start();
		this.shouldUpdate = true;
	}
	
	public void stop() {
		this.tm.stop();
		this.shouldUpdate = false;
	}
	
	public void createUi(int x, int y, int width, int height) {
		tm.loadMetroSample();
		ui.createUi(x, y, width, height);
	}
	
	public void removeUi() {
		ui.removeUi();
	}
	
	public float getTime() {
		return this.tm.getElapsedTime();
	}
	
	public float getSeconds() {
		return (this.tm.getElapsedTime()%60);
	}
	
	public int getMinutes() {
		return (int) (this.tm.getElapsedTime()/60f);
	}
	
	public int getBeat() {
		return this.tm.getCurrentBeat();
	}
	
	public int getBar() {
		return this.tm.getCurrentBar();
	}
	
	public int getNoteCount() {
		return this.tm.getCurrentNoteCount();
	}
	
	public int getBPM() {
		return this.tm.getBpm();
	}
	
	public void updateElapsedTime() {
		this.ui.updateElapsedTime();
	}
	
	public void updateMusicalTime() {
		this.ui.updateMusicalTime();
	}
	
	public void updatePosition() {
		this.ui.updatePosition();
	}

	public void update() {
		this.updatePosition();
		if(this.shouldUpdate) {
			this.updateElapsedTime();
			this.updateMusicalTime();		
		}
	}
}
