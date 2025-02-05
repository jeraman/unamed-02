package soundengine.effects;

import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.ugens.MoogFilter.Type;
import javafx.util.Pair;
import processing.core.PApplet;
import soundengine.core.DecoratedNote;
import soundengine.core.DecoratedNoteMemory;
import soundengine.effects.AdsrEffect;
import soundengine.effects.BandPassFilterEffect;
import soundengine.effects.BitChrushEffect;
import soundengine.effects.DelayEffect;
import soundengine.effects.AbstractEffect;
import soundengine.effects.FlangerEffect;
import soundengine.effects.HighPassFilterEffect;
import soundengine.effects.LowPassFilterEffect;
import soundengine.effects.MoogFilterEffect;
import soundengine.generators.AbstractGenerator;
import soundengine.generators.GeneratorFactory;
import soundengine.util.MidiIO;
import soundengine.util.MusicTheory;

public class EffectsPipelineTest extends PApplet {
	DecoratedNoteMemory memory;
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
		memory = new DecoratedNoteMemory();
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
		AbstractGenerator gen = GeneratorFactory.temporaryFMGen(60, 127, 1500);
		// Generator gen = GeneratorFactory.temporaryOscillatorGen(60, 127,
		// 1500);
		DecoratedNote newNote = new DecoratedNote(0, 60, 127);
		newNote.addGenerator(gen);
		newNote.noteOn();
	}

	public void noteOn(int channel, int pitch, int velocity) {
		AbstractGenerator gen1 = GeneratorFactory.noteOnSampleFileGen(buf, sampleRate, pitch, velocity);
		AbstractGenerator gen2 = GeneratorFactory.noteOnFMGen(pitch, (int) velocity / 5);
		AbstractGenerator gen3 = GeneratorFactory.noteOnOscillatorGen(pitch, (int) (velocity/5), "SINE", -1);
		// this gen is chrashing the entire thing!
		// Generator gen4 = GeneratorFactory.noteOnLiveInpuGen(pitch, velocity);

		// Effect fx = null;
		AbstractEffect fx1 = new HighPassFilterEffect(5000, sampleRate);
		AbstractEffect fx2 = new LowPassFilterEffect(200, sampleRate);
		AbstractEffect fx3 = new BandPassFilterEffect(1000, 100, sampleRate);
		AbstractEffect fx4 = new DelayEffect(0.5f, 0.9f, true, true);
		AbstractEffect fx5 = new MoogFilterEffect(200, 500, "LP");
		AbstractEffect fx6 = new FlangerEffect(1, 0.5f, 1, 0.5f, 0.5f, 0.5f);
		AbstractEffect fx7 = new BitChrushEffect(4, sampleRate);
		AbstractEffect fx8 = new AdsrEffect(1.f, 0.1f, 0.5f, 0.5f, 1.f, 0.f, 0.f);

		DecoratedNote newNote = new DecoratedNote(channel, pitch, velocity);
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
		//newNote.addArtificialInterval("5");
		newNote.noteOn();
		memory.put(newNote);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		DecoratedNote n = memory.remove(pitch);
		if (n == null)
			return;
		n.noteOff();
		// n.close();
	}
}
