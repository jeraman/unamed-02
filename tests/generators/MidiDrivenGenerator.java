package generators;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import generators.AudioFileGenerator;
import generators.OscillatorGenerator;
import musicalTasksTest.Generator;
import musicalTasksTest.GeneratorFactory;
import musicalTasksTest.MidiIO;
import musicalTasksTest.StoredNote;
import musicalTasksTest.StoredNoteMemory;
import processing.core.PApplet;

public class MidiDrivenGenerator extends PApplet {
	MidiIO midi;
	StoredNoteMemory memory;
	
	public static void main(String[] args) {
		PApplet.main("generators.MidiDrivenGenerator");
	}

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
		setupAudio();
		memory = new StoredNoteMemory();
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
		//Generator gen = GeneratorFactory.temporaryAudioFileGen("123go.mp3", 60, 127, 1500);
		//Generator gen = GeneratorFactory.temporaryOscillatorGen(60, 127, 500);
		Generator gen = GeneratorFactory.temporaryLiveInpuGen(1500);
	}

	public void noteOn(int channel, int pitch, int velocity) {
		//Generator gen = GeneratorFactory.noteOnFMGen(pitch, velocity);
		//Generator gen = GeneratorFactory.noteOnAudioFileGen("123go.mp3", pitch, velocity);
		//Generator gen = GeneratorFactory.noteOnOscillatorGen(pitch, velocity);
		Generator gen = GeneratorFactory.noteOnLiveInpuGen(pitch, velocity);
		memory.put(channel, pitch, velocity, gen);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		StoredNote n = memory.remove(pitch);
		if (n == null) return;
		GeneratorFactory.noteOffGen(n.getGenerator());
	}

}
