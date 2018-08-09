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
		test();
	}
	

	public void draw() {
		background(0);
	}
	
	public void test() {
		Gson gson = new Gson();
		int[] ints = {1, 2, 3, 4, 5};
		String[] strings = {"abc", "def", "ghi"};

		// Serialization
		System.out.println(gson.toJson(ints));     // ==> [1,2,3,4,5]
		System.out.println(gson.toJson(strings));  // ==> ["abc", "def", "ghi"]

		// Deserialization
		int[] ints2 = gson.fromJson("[1,2,3,4,5]", int[].class); 
	}

	public void exit() {
		System.out.println("app stopped");
		log.close();
		super.exit();
	}

}
