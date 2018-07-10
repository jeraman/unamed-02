package frontend.core;

import frontend.ui.TempoWindowUi;
import soundengine.time.TimeManager;

public class TempoControl {

	TempoWindowUi ui;
	TimeManager tm;
	
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
	
	public void createUi() {
		ui.createUi();
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
