package musicalTasksTest;

import processing.core.PApplet;
import themidibus.MidiBus;
import util.MidiIO;
import augmenters.AugmentedNoteMemory;
import augmenters.MusicTheory;
import ddf.minim.*;
import generators.AudioFileGenerator;
import generators.Generator;

public class MidiAndMusicTheoryTest extends PApplet {

	Minim minim; // my minim variable
	AudioOutput out;
	MidiIO midi;
	AugmentedNoteMemory memory;
	
	public static void main(String[] args) {
		PApplet.main("main.MidiAndMusicTheoryTest");
	}

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
		setupAudio();
		memory = new AugmentedNoteMemory();
	}

	public void setupAudio() {
		minim = new Minim(this);
		out = minim.getLineOut();
		MidiIO.setup(this);
	}

	public void draw() {
		background(0);
		
		//String t = memory.identifyWhatUserIsPlaying();
		//text(t, width/2, height/2);
	}

	public void noteOn(int channel, int pitch, int velocity) {
		memory.put(channel, pitch, velocity);
		MidiIO.inputNoteOn(channel, pitch, velocity);
		MidiIO.outputNoteOn(channel, pitch, velocity);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		memory.remove(pitch);
		MidiIO.inputNoteOff(channel, pitch, velocity);
		MidiIO.outputNoteOff(channel, pitch, velocity);
	}

	public void controllerChange(int channel, int number, int value) {
		MidiIO.inputControllerChange(channel, number, value);
		MidiIO.outputControllerChange(channel, number, value);
	}

}
