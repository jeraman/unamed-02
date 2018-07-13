package soundengine.time;

import java.io.Serializable;

import soundengine.SoundEngine;

/**
 * Metronome that is able to display bars & beats, elapsed time, and to produce sound.
 * @author jeraman.info
 *
 */
public class TimeManager implements Serializable {

	private Timer t;
	private Metro m;
	
	public TimeManager(int bpm, int globalBeat, int globalNoteValue) {
		this.t = new Timer();
		this.m = new Metro(bpm, globalBeat, globalNoteValue);
	}
	
	public void loadMetroSample() {
		this.m.loadSample();
	}
	
	public void enableSound() {
		m.enableSound();
	}
	
	public void disableSound() {
		m.disableSound();
	}
	
	public int getBpm() {
		return m.getBpm();
	}

	public void setBpm(int bpm) {
		m.setBpm(bpm);
	}

	public int getCurrentBar() {
		return m.getCurrentBar();
	}

	public void setCurrentBar(int currentBar) {
		m.setCurrentBar(currentBar);
	}

	public int getCurrentBeat() {
		return m.getCurrentBeat();
	}

	public void setCurrentBeat(int currentBeat) {
		m.setCurrentBeat(currentBeat);
	}

	public int getGlobalBeat() {
		return m.getGlobalBeat();
	}

	public void setGlobalBeat(int globalBeat) {
		m.setGlobalBeat(globalBeat);
	}

	public int getCurrentNoteCount() {
		return m.getCurrentNoteCount();
	}

	public void setCurrentNoteCount(int currentNoteCount) {
		m.setCurrentNoteCount(currentNoteCount);
	}
	
	public int getGlobalNoteValue() {
		return m.getGlobalNoteValue();
	}
	
	public void setGlobalNoteValue(int globalNoteValue) {
		m.setGlobalNoteValue(globalNoteValue);
	}
	
	public boolean getMetronomeStatus() {
		return this.m.getMetronomeStatus();
	}
	
	public String getMusicalTime() {
		String result = this.getCurrentBar() +" : " + this.getCurrentBeat() + " : " + this.getCurrentNoteCount(); 
		return result; 
	}
	
	public float getElapsedTime() {
		return t.getElapsedTime();
	}
	
	public void start() {
		t.start();
		m.start();
	}
	
	public void stop() {
		t.stop();
		m.stop();
	}
}
