package generators;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.spi.AudioStream;
import javafx.util.Pair;


public class GeneratorFactory {
	public static Minim minim;
	public static AudioOutput out;
	public static AudioStream in;
	
	public static void setup (Minim minim, AudioOutput out,  AudioStream in) {
		GeneratorFactory.minim = minim;
		GeneratorFactory.out = out;
		GeneratorFactory.in = in;
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
	
	public static Generator noteOnSampleFileGen(MultiChannelBuffer buf, float samplerate, int pitch, int velocity) {
		Generator gen = new SamplerFileGenerator(buf, samplerate, pitch, velocity);
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
		Generator gen = new LiveInputGenerator(pitch, velocity);
		return gen;
//		return GeneratorFactory.patch(gen);
	}
	
	public static Generator temporaryLiveInpuGen(int pitch, int velocity, int duration) {
		Generator gen = noteOnLiveInpuGen(pitch, velocity);
		gen.noteOffAfterDuration(duration);
		return gen;
	}
	
	public static Generator noteOnLiveInpuGen() {
		Generator gen = new LiveInputGenerator();
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
	public synchronized static Generator unpatch(Generator gen) {
		gen.unpatchOutput(out);
		return gen;
	}
	
	public synchronized static Generator patch(Generator gen) {
		gen.patchOutput(out);
		return gen;
	}
	
	public synchronized static AudioStream getInput() {
		return minim.getInputStream( Minim.MONO, 
            out.bufferSize(), 
            out.sampleRate(), 
            out.getFormat().getSampleSizeInBits());
	}
	
	public synchronized static Pair<MultiChannelBuffer, Float> loadMultiChannelBufferFromFile(String filename) {
		MultiChannelBuffer sampleData = new MultiChannelBuffer(1, 1);
		float sampleDataSampleRate = GeneratorFactory.minim.loadFileIntoBuffer(filename, sampleData);
		return new Pair<>(sampleData, sampleDataSampleRate);
	}

	public static void close() {
		out.close();
		minim.stop();
		out = null;
		minim = null;
	}

}
