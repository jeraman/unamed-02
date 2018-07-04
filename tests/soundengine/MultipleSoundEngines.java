package soundengine;

import ddf.minim.Minim;
import processing.core.PApplet;
import soundengine.util.MidiIO;

public class MultipleSoundEngines extends PApplet {
	SoundEngine eng1;
	SoundEngine eng2;
	SoundEngine eng3;

	public static void main(String[] args) {
		PApplet.main("soundengine.MultipleSoundEngines");
	}

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
		Minim minim = new Minim(this);
		eng1 = new SoundEngine(minim);
		eng2 = new SoundEngine(minim);
		eng3 = new SoundEngine(minim);
		MidiIO.setup(this);
	}

	public void draw() {
		background(0);
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
	

	// generators
	boolean isGen1Active = false;

	private void processGen1() {
		if (!isGen1Active)
			eng1.addGenerator("1", "SAMPLE", new String[] { "123go.mp3", "-1", "-1", "true", "-1" });
		else
			eng1.removeGenerator("1");
		isGen1Active = !isGen1Active;
	}

	boolean isGen2Active = false;

	private void processGen2() {
		if (!isGen2Active)
			eng2.addGenerator("2", "OSCILLATOR", new String[] { "-1", "-1", "SINE", "-1" });
		else
			eng2.removeGenerator("2");
		isGen2Active = !isGen2Active;
	}

	boolean isGen3Active = false;

	private void processGen3() {
		if (!isGen3Active)
			eng3.addGenerator("3", "FM", new String[] { "220", "-1", "SINE", "30", "75.", "SAW", "-1" });
		else
			eng3.removeGenerator("3");
		isGen3Active = !isGen3Active;
	}

	// effects
	boolean isFx1Active = false;

	private void processFx1() {
		if (!isFx1Active)
			// eng.addEffect("1", "HIGHPASS", new String[] { "5000" });
			eng1.addEffect("1", "LOWPASS", new String[] { "5000" });
		else
			eng1.removeEffect("1");
		isFx1Active = !isFx1Active;
	}

	boolean isFx2Active = false;

	private void processFx2() {
		if (!isFx2Active)
			eng2.addEffect("2", "FLANGER", new String[] { "1", "0.5", "1", "0.5", "0.5", "0.5" });
		// eng.addEffect("2", "MOOGFILTER", new String[] { "300", "150", "BP"});
		else
			eng2.removeEffect("2");
		isFx2Active = !isFx2Active;
	}

	boolean isFx3Active = false;

	private void processFx3() {
		if (!isFx3Active)
			// eng.addEffect("3", "BITCHRUSH", new String[] { "4" });
			eng3.addEffect("3", "DELAY", new String[] { "0.5", "1", "true", "true" });
		else
			eng3.removeEffect("3");
		isFx3Active = !isFx3Active;
	}

	// augmenters
	boolean isAug1Active = false;

	private void processAug1() {
		if (!isAug1Active)
			eng1.addAugmenter("1", "NOTE", new String[] { "60", "-1","-1" });
		else
			eng1.removeAugmenter("1");
		isAug1Active = !isAug1Active;
	}

	boolean isAug2Active = false;

	private void processAug2() {
		if (!isAug2Active)
			eng2.addAugmenter("2", "INTERVAL", new String[] { "-1", "127", "-1", "12" });
		else
			eng2.removeAugmenter("2");
		isAug2Active = !isAug2Active;
	}

	boolean isAug3Active = false;

	private void processAug3() {
		if (!isAug3Active)
			eng3.addAugmenter("3", "CHORD", new String[] { "60", "127", "-1", "min", });
		else
			eng3.removeAugmenter("3");
		isAug3Active = !isAug3Active;
	}

	public void noteOn(int channel, int pitch, int velocity) {
		eng1.noteOn(channel, pitch, velocity);
		eng2.noteOn(channel, pitch, velocity);
		eng3.noteOn(channel, pitch, velocity);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		eng1.noteOff(channel, pitch, velocity);
		eng2.noteOff(channel, pitch, velocity);
		eng3.noteOff(channel, pitch, velocity);
	}

}
