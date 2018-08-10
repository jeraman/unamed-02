package logging;

import com.google.gson.Gson;

import processing.core.PApplet;

public class TestingLogger extends PApplet {

	static Logger log = new Logger("User 1");

	public static void main(String[] args) {
		PApplet.main("logging.TestingLogger");
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
		try {
			System.out.println("generating exception...");
			generateException();
		} catch (Exception e) {
			System.out.println("exception generated!");
			System.out.println("app stopped");
			log.close(e);
			super.exit();
		}
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
