package soundengine.util;

import java.util.concurrent.TimeUnit;

public class Util {
	
	public static float map(int x, int startLower, int startHigher, int endLower, int endHigher) {
		if ((startHigher - startLower) == 0) {
			System.out.println("Division per 0");
			return 0;
		}
		float ratio = (float)(endHigher - endLower) / (startHigher - startLower);
		return ratio * (x - startLower) + endLower;
	}
	
	public static float map(float x, int startLower, int startHigher, int endLower, int endHigher) {
		return Util.map((int)x, startLower, startHigher, endLower, endHigher);
	}
	
	public static float mapFromMidi(int x, int endLower, int endHigher) {
		return Util.map(x, 0, 127, endLower, endHigher);
	}

	public static float mapFromMidiToDecibels(int x) {
		return Util.mapFromMidi(x, -30, 6);
	}
	
	public static float mapFromMidiToAmplitude(int x) {
		return Util.mapFromMidi(x, 0, 1);
	}

	public static int mapFromAmplitudeToMidi(float x) {
		return (int) Util.map(x, 0, 1, 0, 127);
	}
	
	
	
	public static void delay(int time) {
		try {
			TimeUnit.MILLISECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
