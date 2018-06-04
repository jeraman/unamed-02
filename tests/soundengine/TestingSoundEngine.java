package soundengine;

import ddf.minim.Minim;
import processing.core.PApplet;
import soundengine.util.MidiIO;

public class TestingSoundEngine extends PApplet {
	SoundEngine eng;

	public static void main(String[] args) {
		PApplet.main("soundengine.TestingSoundEngine");
	}

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
		Minim minim = new Minim(this);
		eng = new SoundEngine(minim);
		MidiIO.setup(this);
	}

	public void update() {
	}

	public void stop() {
	}

	public void draw() {
		background(0);
	}
	
	public void mousePressed() {

		if (isGen2Active && mouseButton==LEFT)
			eng.updateGenerator("2", new String[] { "60", "127", "SAW"});
		if (isGen2Active && mouseButton==RIGHT)
			eng.updateGenerator("2", new String[] { "60", "127", "SINE"});
		
		if (isGen1Active && mouseButton == LEFT)
			eng.updateGenerator("1", new String[] { "123go.mp3", "60", "127", "false" });
		if (isGen1Active && mouseButton == RIGHT)
			eng.updateGenerator("1", new String[] { "error.mp3", "60", "127", "true" });
	}
	
	public void mouseMoved() {
		
		if (isGen1Active) {
			int a = (int)map( mouseY, 0, height, 256, 0 );
			eng.updateGenerator("1", new String[] { "123go.mp3", "60", ""+a, "true" });
		}
		
		if (isGen3Active) {
			//testing mod parameters
			 float freq1 = map( mouseX, 0, width, 0.1f, 100f );
			 float amp1 = map( mouseY, 0, height, 220, 1f);
			 eng.updateGenerator("3", new String[] { "60", "127", "SINE",""+freq1, ""+amp1, "SAW"});
			//testing carrier parameters
//			int p = (int)map( mouseX, 0, width, 40, 150 );
//			int a = (int)map( mouseY, 0, height, 0, 256 );
//			eng.updateGenerator("3", new String[] { ""+p, ""+a, "SINE", "30", "75.", "SAW"});
		}
		
	}

	public void keyPressed() {
		println("key pressed: " + key);

		// generators
		if (key == '1')
			processGen1();
		if (key == '2')
			processGen2();
		if (key == '3')
			processGen3();

		// effects
		if (key == 'q')
			processFx1();
		if (key == 'w')
			processFx2();
		if (key == 'e')
			processFx3();
		
		// augmenters
		if (key == 'a')
			processAug1();
		if (key == 's')
			processAug2();
		if (key == 'd')
			processAug3();
	}

	//generators
	boolean isGen1Active = false;
	private void processGen1() {
		if (!isGen1Active)
			eng.addGenerator("1", "SAMPLE", new String[] { "123go.mp3", "60", "127", "true" });
		else
			eng.removeGenerator("1");
		isGen1Active = !isGen1Active;
	}

	boolean isGen2Active = false;
	private void processGen2() {
		if (!isGen2Active)
			eng.addGenerator("2", "OSCILLATOR", new String[] { "60", "127", "SINE"});
		else
			eng.removeGenerator("2");
		isGen2Active = !isGen2Active;
	}

	boolean isGen3Active = false;
	private void processGen3() {
		if (!isGen3Active)
			eng.addGenerator("3", "FM", new String[] { "60", "127", "SINE", "30", "75.", "SAW" });
		else
			eng.removeGenerator("3");
		isGen3Active = !isGen3Active;
	}

	//effects
	boolean isFx1Active = false;
	private void processFx1() {
		if (!isFx1Active)
			eng.addEffect("1", "HIGHPASS", new String[] { "5000" });
		else
			eng.removeEffect("1");
		isFx1Active = !isFx1Active;
	}

	boolean isFx2Active = false;
	private void processFx2() {
		if (!isFx2Active)
			eng.addEffect("2", "FLANGER", new String[] { "1", "0.5", "1", "0.5", "0.5", "0.5" });
		else
			eng.removeEffect("2");
		isFx2Active = !isFx2Active;
	}

	boolean isFx3Active = false;
	private void processFx3() {
		if (!isFx3Active)
			eng.addEffect("3", "BITCHRUSH", new String[] { "4" });
		else
			eng.removeEffect("3");
		isFx3Active = !isFx3Active;
	}
	
	//augmenters
	boolean isAug1Active = false;
	private void processAug1() {
		if (!isAug1Active)
			eng.addAugmenter("1", "NOTE", new String[] { "60"});
		else
			eng.removeAugmenter("1");
		isAug1Active = !isAug1Active;
	}
	
	boolean isAug2Active = false;
	private void processAug2() {
		if (!isAug2Active)
			eng.addAugmenter("2", "INTERVAL", new String[] { "12"});
		else
			eng.removeAugmenter("2");
		isAug2Active = !isAug2Active;
	}
	
	boolean isAug3Active = false;
	private void processAug3() {
		if (!isAug3Active)
			eng.addAugmenter("3", "CHORD", new String[] {"min"});
		else
			eng.removeAugmenter("3");
		isAug3Active = !isAug3Active;
	}

	public void noteOn(int channel, int pitch, int velocity) {
		eng.noteOn(channel, pitch, velocity);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		eng.noteOff(channel, pitch, velocity);
	}
}
