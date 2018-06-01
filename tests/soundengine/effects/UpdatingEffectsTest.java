package soundengine.effects;

import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.spi.AudioRecordingStream;
import javafx.util.Pair;
import processing.core.PApplet;
import soundengine.augmenters.AugmentedNote;
import soundengine.augmenters.AugmentedNoteMemory;
import soundengine.effects.LowPassFilterEffect;
import soundengine.generators.Generator;
import soundengine.generators.GeneratorFactory;
import soundengine.util.MidiIO;

public class UpdatingEffectsTest extends PApplet {
	AugmentedNoteMemory memory;
	AudioRecordingStream fileStream;

	MultiChannelBuffer buf;
	float sampleRate;
	
	Generator gen = null;
	LowPassFilterEffect fx = null;

	public static void main(String[] args) {
		PApplet.main("soundengine.effects.UpdatingEffectsTest");
	}

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
		setupAudio();
		memory = new AugmentedNoteMemory();
		fileStream = GeneratorFactory.minim.loadFileStream("123go.mp3");
	}

	public void update() {
		memory.update();
	}

	public void setupAudio() {
		Minim minim = new Minim(this);
		GeneratorFactory.setup(minim);

		Pair<MultiChannelBuffer, Float> pair = GeneratorFactory.loadMultiChannelBufferFromFile("123go.mp3");
		buf = pair.getKey();
		sampleRate = pair.getValue();

		MidiIO.setup(this);

	}

	public void stop() {
		GeneratorFactory.close();
	}

	public void draw() {
		background(0);
		this.memory.size();
	}

	public void mouseMoved() {
		if (fx != null) {
			float cutoff = map(mouseX, 0, width, 60, 2000);
			fx.setFreq(mouseX);
		}
	}
	
	public void noteOn(int channel, int pitch, int velocity) {
		gen = GeneratorFactory.noteOnSampleFileGen(buf, sampleRate, pitch, velocity);

		fx = new LowPassFilterEffect(200, GeneratorFactory.out.sampleRate());

		AugmentedNote newNote = new AugmentedNote(channel, pitch, velocity);
		
		// first the generators
		newNote.addGenerator(gen);
		// then the effects
		
		newNote.addEffect(fx);
		//finally, the augmenters

		newNote.noteOn();
		
		memory.put(newNote);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		AugmentedNote n = memory.remove(pitch);
		if (n == null)
			return;
		n.noteOff();
		// n.close();
	}
}
