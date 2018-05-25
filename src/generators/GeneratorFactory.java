package generators;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.spi.AudioStream;


public class GeneratorFactory {
	static Minim minim;
	static AudioOutput out;
	
	public static void setup (Minim minim, AudioOutput out) {
		GeneratorFactory.minim = minim;
		GeneratorFactory.out = out;
	}

	////////////////////////////
	//AudioFile factory
	public static Generator noteOnAudioFileGen(String filename, int pitch, int velocity) {
		Generator gen = new AudioFileGenerator(filename, pitch, velocity);
		return gen;
		//return GeneratorFactory.patch(gen);
	}
 
	public static Generator temporaryAudioFileGen(String filename, int pitch, int velocity, int duration) {
		Generator gen = noteOnAudioFileGen(filename, pitch, velocity);
		gen.noteOffAfterDuration(duration);
		return gen;
	}
	
	
	////////////////////////////
	//Oscillator Factory
	public static Generator noteOnOscillatorGen(int pitch, int velocity) {
		Generator gen = new OscillatorGenerator(pitch, velocity, true);
		return gen;
//		return GeneratorFactory.patch(gen);
	}
	
	public static Generator temporaryOscillatorGen(int pitch, int velocity, int duration) {
		Generator gen = noteOnOscillatorGen(pitch, velocity);
		gen.noteOffAfterDuration(duration);
		return gen;
	}

	
	////////////////////////////
	//FM factory
	public static Generator noteOnFMGen(int pitch, int velocity) {
		Generator gen = new FMGenerator(pitch, velocity);
		return gen;
//		return GeneratorFactory.patch(gen);
	}
	
	public static Generator temporaryFMGen(int pitch, int velocity, int duration) {
		Generator gen = noteOnFMGen(pitch, velocity);
		gen.noteOffAfterDuration(duration);
		return gen;
	}	
	
	////////////////////////////
	//Live Input factory
	public static Generator noteOnLiveInpuGen(int pitch, int velocity) {
		Generator gen = new LiveInputGenerator(getInput(), pitch, velocity);
		return gen;
//		return GeneratorFactory.patch(gen);
	}
	
	public static Generator temporaryLiveInpuGen(int pitch, int velocity, int duration) {
		Generator gen = noteOnLiveInpuGen(pitch, velocity);
		gen.noteOffAfterDuration(duration);
		return gen;
	}
	
	public static Generator noteOnLiveInpuGen() {
		Generator gen = new LiveInputGenerator(getInput());
//		return GeneratorFactory.patch(gen);
		return gen;
	}
	
	public static Generator temporaryLiveInpuGen(int duration) {
		Generator gen = noteOnLiveInpuGen();
		gen.noteOffAfterDuration(duration);
		return gen;
	}
	
	////////////////////////////
	//general note off for all generators
	public static Generator unpatch(Generator gen) {
		gen.unpatchOutput(out);
		return gen;
	}
	
	public static Generator patch(Generator gen) {
		gen.patchOutput(out);
		return gen;
	}
	
	public static AudioStream getInput() {
		return minim.getInputStream( Minim.MONO, 
            out.bufferSize(), 
            out.sampleRate(), 
            out.getFormat().getSampleSizeInBits());
	}

}
