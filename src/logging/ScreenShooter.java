package logging;

import java.sql.Timestamp;

import frontend.Main;
import frontend.ZenStates;
import processing.core.PApplet;

/**
 * Class responsible for saving screenshots on every SAVING_TIME minutes.
 * @author jeronimo
 *
 */
public class ScreenShooter {
	private PApplet p;
	private int beginTimestamp;
	private String filename;
	private int savingTimeInMinutes;
	private static int SAVING_TIME = 10; //in minutes
	
	public ScreenShooter (PApplet p, String userId) {
		this.p = p;
		this.filename =  "./data/logs/screenshots/" + userId + " - ";
		this.savingTimeInMinutes = SAVING_TIME * 60 * 1000;
		this.initCountdown();
	}
	
	private void initCountdown() {
		this.beginTimestamp = p.millis();
	}
	
	public void updateCountdown() {
		int offset = p.millis() - beginTimestamp;
		if (offset > savingTimeInMinutes ) {
			this.screenShot();
			this.initCountdown();
		}
	}
	
	private void screenShot() {
		p.saveFrame(filename + new Timestamp(System.currentTimeMillis()) + ".jpeg");
	}

}
