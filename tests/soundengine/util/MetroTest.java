package soundengine.util;

import processing.core.PApplet;

public class MetroTest extends PApplet {

	public static void main(String[] args) {
		PApplet.main("soundengine.util.MetroTest");
	}

	Metro m = new Metro(120, 3, 8);

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
		
		if (counting)
			m.start();
	}

	boolean counting = false;

	public void mousePressed() {
		if (counting) {
			println("stopping...");
			m.stop();
			counting = false;
		} else {
			println("restarting...");
			m.start();
			counting = true;
		}
	}

	public void draw() {
		if (counting)
			println("metro: " + m.getCurrentBar() + " : " + m.getCurrentBeat() + " : " + m.getCurrentNoteCount());
	}

}
