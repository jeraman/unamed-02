package soundengine.effects;

import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.ugens.MoogFilter.Type;
import javafx.util.Pair;
import processing.core.PApplet;
import soundengine.augmenters.AugmentedNote;
import soundengine.augmenters.AugmentedNoteMemory;
import soundengine.augmenters.MusicTheory;
import soundengine.effects.AdsrEffect;
import soundengine.effects.BandPassFilterEffect;
import soundengine.effects.BitChrushEffect;
import soundengine.effects.DelayEffect;
import soundengine.effects.Effect;
import soundengine.effects.FlangerEffect;
import soundengine.effects.HighPassFilterEffect;
import soundengine.effects.LowPassFilterEffect;
import soundengine.effects.MoogFilterEffect;
import soundengine.generators.Generator;
import soundengine.generators.GeneratorFactory;
import soundengine.util.MidiIO;

public class EffectsPipelineTest extends PApplet {
	AugmentedNoteMemory memory;
	AudioRecordingStream fileStream;

	MultiChannelBuffer buf;
	float sampleRate;

	public static void main(String[] args) {
		PApplet.main("soundengine.effects.EffectsPipelineTest");
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

	public void mousePressed() {
		Generator gen = GeneratorFactory.temporaryFMGen(60, 127, 1500);
		// Generator gen = GeneratorFactory.temporaryOscillatorGen(60, 127,
		// 1500);
		AugmentedNote newNote = new AugmentedNote(0, 60, 127);
		newNote.addGenerator(gen);
		newNote.noteOn();
	}

	public void noteOn(int channel, int pitch, int velocity) {
		Generator gen1 = GeneratorFactory.noteOnSampleFileGen(buf, sampleRate, pitch, velocity);
		Generator gen2 = GeneratorFactory.noteOnFMGen(pitch, velocity / 5);
		Generator gen3 = GeneratorFactory.noteOnOscillatorGen(pitch, velocity / 5);
		// this gen is chrashing the entire thing!
		// Generator gen4 = GeneratorFactory.noteOnLiveInpuGen(pitch, velocity);

		// Effect fx = null;
		Effect fx1 = new HighPassFilterEffect(5000, sampleRate);
		Effect fx2 = new LowPassFilterEffect(200, sampleRate);
		Effect fx3 = new BandPassFilterEffect(1000, 100, sampleRate);
		Effect fx4 = new DelayEffect(0.5f, 0.9f, true, true);
		Effect fx5 = new MoogFilterEffect(200, 500, "LP");
		Effect fx6 = new FlangerEffect(1, 0.5f, 1, 0.5f, 0.5f, 0.5f);
		Effect fx7 = new BitChrushEffect(4, sampleRate);
		Effect fx8 = new AdsrEffect(1.f, 0.1f, 0.5f, 0.5f, 1.f, 0.f, 0.f);

		AugmentedNote newNote = new AugmentedNote(channel, pitch, velocity);
		// first the generators
		// newNote.addGenerator(gen2);
		newNote.addGenerator(gen1);
		newNote.addGenerator(gen3);
		// then the effects
		newNote.addEffect(fx6);
		newNote.addEffect(fx4);
		newNote.addEffect(fx8);
		// finally, the augmenters
		// newNote.addArtificialChord("min7");
		newNote.addArtificialInterval("5");
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
