package generators;

import augmenters.AugmentedNote;
import augmenters.AugmentedNoteMemory;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.spi.AudioStream;
import generators.AudioFileGenerator;
import generators.OscillatorGenerator;
import processing.core.PApplet;
import util.MidiIO;

public class MidiDrivenGenerator extends PApplet {
	MidiIO midi;
	AugmentedNoteMemory memory;
	AudioRecordingStream fileStream;
	
	public static void main(String[] args) {
		PApplet.main("generators.MidiDrivenGenerator");
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
		MidiIO.setup(this);
	}

	public void draw() {
		background(0);
		
		String t = memory.identifyWhatUserIsPlaying();
		text(t, width/2, height/2);
	}
	
	public void mousePressed() {
		println("memory.size() " + memory.size());
		println("memory: " + memory.getNoteArray());
		println("removalLive " + memory.getToBeDeletedArray());
		
		//Generator gen = GeneratorFactory.temporaryFMGen(60, 127, 1500);
		Generator gen = GeneratorFactory.temporaryAudioFileGen(fileStream, 60, 127, 1500);
		
		//Generator gen = GeneratorFactory.temporaryOscillatorGen(60, 127, 500);
		//Generator gen = GeneratorFactory.temporaryLiveInpuGen(1500);
		GeneratorFactory.patch(gen);
	}

	public void noteOn(int channel, int pitch, int velocity) {
		//Generator gen = GeneratorFactory.noteOnFMGen(pitch, velocity);
		Generator gen = GeneratorFactory.noteOnAudioFileGen(fileStream, pitch, velocity);
		//Generator gen = GeneratorFactory.noteOnOscillatorGen(pitch, velocity);
		//Generator gen = GeneratorFactory.noteOnLiveInpuGen(pitch, velocity);
		GeneratorFactory.patch(gen);
		gen.noteOn();
		memory.put(channel, pitch, velocity, gen);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		AugmentedNote n = memory.remove(pitch);
		if (n == null) return;
		n.getGenerator().noteOff();
		GeneratorFactory.unpatch(n.getGenerator());
	}

}
