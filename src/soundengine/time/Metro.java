package soundengine.time;

import java.io.Serializable;

import ddf.minim.AudioSample;
import frontend.Main;
import soundengine.SoundEngine;
import soundengine.util.Util;

/**
 * Class that counts time in musical terms using BPM, bars, beats, and note
 * values
 * 
 * @author jeraman.info
 *
 */
class Metro extends AbstractTimeMeter implements Serializable {

	private int bpm;
	private int currentBar;
	private int currentBeat;
	private int currentNoteCount;
	private int globalBeat;
	private int globalNoteValue;
	private boolean shouldSound;

	transient AudioSample sample;
	
	public Metro() {
		this(120);
	}

	public Metro(int bpm) {
		this(bpm, 4, 4);
	}

	public Metro(int bpm, int globalBeat, int globalNoteValue) {
		super();
		this.bpm = bpm;
		this.globalBeat = globalBeat;
		this.globalNoteValue = globalNoteValue;
		this.currentBar = 0;
		this.currentBeat = 0;
		this.loadSample();
		this.shouldSound = false;
	}
	
	public void loadSample() {
		this.sample = SoundEngine.minim.loadSample("metronome.wav", 256);
	}
	
	public void enableSound() {
		this.shouldSound = true;
	}
	
	public void disableSound() {
		this.shouldSound = false;
	}
	
	public boolean getMetronomeStatus() {
		return this.shouldSound;
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
		super.stop();
	}

	public void start() {
		this.currentBar = 0;
		this.currentBeat = this.globalBeat-1;
		this.currentNoteCount = 0;

		super.start();
	}

	@Override
	public void run() {

		while (this.isAlive()) {
			if (currentBeat == this.globalBeat-1) 
				playBar1();
			
			playBars234();
			
			int time = (int) 60 * 1000 / this.bpm;
			
			float mult = this.globalNoteValue/4f;
			int adaptedTime = (int) (time/mult);

			while(currentNoteCount < globalNoteValue) {
				Util.delay(adaptedTime/globalNoteValue);
				currentNoteCount++;
			}
		}
	}

	private void playBar1() {
		currentBar = currentBar + 1;
		currentNoteCount = 0;
		if (shouldSound) {
			sample.setGain(-5);
			sample.trigger();
		}
	}

	private void playBars234() {
		currentBeat = (currentBeat + 1) % globalBeat;
		currentNoteCount = 0;
		if (shouldSound) {
			sample.setGain(-25);
			sample.trigger();
		}
	}

}
