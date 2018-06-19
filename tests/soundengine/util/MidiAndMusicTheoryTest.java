package soundengine.util;

import processing.core.PApplet;
import soundengine.core.DecoratedNoteMemory;
import soundengine.generators.AbstractGenerator;
import soundengine.util.MidiIO;
import themidibus.MidiBus;
import ddf.minim.*;

public class MidiAndMusicTheoryTest extends PApplet {

	Minim minim; // my minim variable
	AudioOutput out;
	MidiIO midi;
	DecoratedNoteMemory memory;
	
	public static void main(String[] args) {
		PApplet.main("soundengine.util.MidiAndMusicTheoryTest");
	}

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		background(0);
		setupAudio();
		memory = new DecoratedNoteMemory();
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
