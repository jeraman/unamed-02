package soundengine.util;

import processing.core.PApplet;

public class MetroTest extends PApplet {

	public static void main(String[] args) {
		PApplet.main("soundengine.util.MetroTest");
	}

	Metro m = new Metro(120, 3, 8);
	Timer t = new Timer();
	TimeManager tm = new TimeManager(120, 3, 8);
	boolean counting = false;

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
	}


	public void mousePressed() {
		if (counting) {
			println("stopping...");
			m.stop();
			t.stop();
			tm.stop();
			counting = false;
		} else {
			println("restarting...");
			m.start();
			t.start();
			tm.start();
			counting = true;
		}
	}

	public void draw() {
		if (counting) {
			println("metro: " + m.getCurrentBar() + " : " + m.getCurrentBeat() + " : " + m.getCurrentNoteCount());
			println("timer: " + t.getElapsedTime());
			println("TimeManager in time: " + tm.getElapsedTime());
			println("TimeManager in music: " + tm.getMusicalTime());
		}
	}

}
