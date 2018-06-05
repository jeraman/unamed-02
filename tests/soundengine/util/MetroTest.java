package soundengine.util;

import processing.core.PApplet;

public class MetroTest extends PApplet {

	public static void main(String[] args) {
		PApplet.main("soundengine.util.MetroTest");
	}

	Metro m = new Metro(120);
	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
		m.start();
	}
	
	public void mousePressed() {
		println("stopping...");
		m.stop();
		println("restarting...");
		m.start();
	}
	
	public void draw() {
		println("metro: " + m.getBar() + " : " + m.getBeat());
	}


}

