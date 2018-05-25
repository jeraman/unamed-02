package generators;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.spi.AudioStream;


public class GeneratorFactory {
	public static Minim minim;
	public static AudioOutput out;
	
	public static void setup (Minim minim, AudioOutput out) {
		GeneratorFactory.minim = minim;
		GeneratorFactory.out = out;
	}

	////////////////////////////
	//AudioFile factory
	public static Generator noteOnAudioFileGen(AudioRecordingStream fileStream, int pitch, int velocity) {
		Generator gen = new AudioFileGenerator(fileStream, pitch, velocity);
		return gen;
		//return GeneratorFactory.patch(gen);
	}
 
	public static Generator temporaryAudioFileGen(AudioRecordingStream fileStream, int pitch, int velocity, int duration) {
		Generator gen = noteOnAudioFileGen(fileStream, pitch, velocity);
		gen.noteOffAfterDuration(duration);
		return gen;
	}

	////////////////////////////
	//SampleFile factory
	public static Generator noteOnSampleFileGen(String fileStream, int pitch, int velocity) {
		Generator gen = new SamplerFileGenerator(fileStream, pitch, velocity);
		return gen;
		//return GeneratorFactory.patch(gen);
	}
	
	public static Generator temporarySampleFileGen(String fileStream, int pitch, int velocity, int duration) {
		Generator gen = noteOnSampleFileGen(fileStream, pitch, velocity);
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

	public static void close() {
		out.close();
		minim.stop();
		out = null;
		minim = null;
	}

}
