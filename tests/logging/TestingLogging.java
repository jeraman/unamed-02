package logging;

import processing.core.PApplet;

public class TestingLogging extends PApplet {

	static SessionLog log = new SessionLog("User 1");

	public static void main(String[] args) {
		PApplet.main("logging.TestingLogging");
	}

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		System.out.println("app started");
	}

	public void draw() {
		background(0);
	}

	public void mousePressed() {
		System.out.println("generating exception...");
		generateException();
	}

	public void generateException() {
		int array[] = new int[2];
		array[3] = 0; // error
	}

	public void exit() {
		System.out.println("app stopped");
		log.close();
		super.exit();
	}

}
