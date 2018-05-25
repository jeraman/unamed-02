package augmenters;

import augmenters.ArtificialNotes;
import augmenters.AugmentedNote;
import augmenters.AugmentedNoteMemory;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;
import generators.Generator;
import generators.GeneratorFactory;
import processing.core.PApplet;
import util.MidiIO;

public class AugmenterTest extends PApplet{
	MidiIO midi;
	AugmentedNoteMemory memory;
	AudioRecordingStream fileStream;
	
	public static void main(String[] args) {
		PApplet.main("augmenters.AugmenterTest");
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
		AudioOutput out = minim.getLineOut(Minim.MONO, 256);
		GeneratorFactory.setup(minim, out);
		MidiIO.setup(this);
	}
	
	
	public void stop() {
		GeneratorFactory.close();
	}

	public void draw() {
		background(0);
		
		//String t = memory.identifyWhatUserIsPlaying();
		//text(t, width/2, height/2);
	}
	
	public void mousePressed() {
		//Generator gen = GeneratorFactory.temporaryFMGen(60, 127, 1500);
		//Generator gen = GeneratorFactory.temporaryAudioFileGen("123go.mp3", 60, 127, 1500);
		//Generator gen = GeneratorFactory.temporaryOscillatorGen(60, 127, 500);
		//Generator gen = GeneratorFactory.temporaryLiveInpuGen(1500);
	}

	public void noteOn(int channel, int pitch, int velocity) {
		Generator gen = GeneratorFactory.noteOnSampleFileGen("123go.mp3", pitch, velocity);
		//Generator gen = GeneratorFactory.noteOnAudioFileGen(fileStream, pitch, velocity);
		//Generator gen = GeneratorFactory.noteOnFMGen(pitch, velocity);
		//Generator gen = GeneratorFactory.noteOnOscillatorGen(pitch, velocity);
		//Generator gen = GeneratorFactory.noteOnLiveInpuGen(pitch, velocity);

		AugmentedNote newNote = new AugmentedNote(channel, pitch, velocity, gen);
		newNote.addArtificialChord("min7");
		//newNote.addArtificialInterval("5");
		newNote.noteOn();
		memory.put(newNote);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		AugmentedNote n = memory.remove(pitch);
		if (n == null) return;
		n.noteOff();
		n.close();
	}
}
