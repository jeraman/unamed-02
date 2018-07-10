package soundengine.time;

import ddf.minim.Minim;
import processing.core.PApplet;
import soundengine.SoundEngine;
import soundengine.time.Metro;
import soundengine.time.TimeManager;
import soundengine.time.Timer;

public class MetroTest extends PApplet {

	public static void main(String[] args) {
		PApplet.main("soundengine.time.MetroTest");
	}

	// Metro m = new Metro(120, 3, 8);
	// Timer t = new Timer();

	TimeManager tm;

	boolean counting = false;
	boolean shouldSound = true;

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
		new SoundEngine(new Minim(this));
		tm = new TimeManager(120, 7, 3);
	}

	public void keyPressed() {
		if (shouldSound)
			tm.enableSound();
		else
			tm.disableSound();

		shouldSound = !shouldSound;
	}

	public void mousePressed() {
		if (counting) {
			println("stopping...");
			// m.stop();
			// t.stop();
			tm.stop();
			counting = false;
		} else {
			println("restarting...");
			// m.start();
			// t.start();
			tm.start();
			counting = true;
		}
	}

	public void draw() {
		if (counting) {
			// println("metro: " + m.getCurrentBar() + " : " +
			// m.getCurrentBeat() + " : " + m.getCurrentNoteCount());
			// println("timer: " + t.getElapsedTime());
			println("TimeManager in time: " + tm.getElapsedTime());
			println("TimeManager in music: " + tm.getMusicalTime());
		}
	}

}
