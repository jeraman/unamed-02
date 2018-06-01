package soundengine.generators;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.spi.AudioStream;
import ddf.minim.ugens.Waveform;
import javafx.util.Pair;
import soundengine.SoundEngine;


/**
 * Singleton class used to create custom generators
 * @author jeraman.info
 *
 */
public class GeneratorFactory {
	
	/////////////////////////////////////////////////
	//@TODO delete these @Deprecated variables. Used only in old tests.
	@Deprecated
	public static Minim minim;
	@Deprecated
	public static AudioOutput out;
	@Deprecated
	public static AudioStream in;
	
	
	/////////////////////////////////////////////////
	//@TODO delete these @Deprecated methods. Used only in old tests.
	@Deprecated
	public static void setup(Minim minim) {
		GeneratorFactory.minim = minim;
		GeneratorFactory.out = minim.getLineOut(Minim.MONO, 256);
		GeneratorFactory.in = minim.getInputStream(Minim.MONO, out.bufferSize(), out.sampleRate(),
				out.getFormat().getSampleSizeInBits());
		in.open();
	}
	
	@Deprecated
	public static void close() {
		out.close();
		in.close();
		minim.stop();
		out = null;
		minim = null;
	}
	
	@Deprecated
	public synchronized static Pair<MultiChannelBuffer, Float> loadMultiChannelBufferFromFile(String filename) {
		MultiChannelBuffer sampleData = new MultiChannelBuffer(1, 1);
		float sampleDataSampleRate = GeneratorFactory.minim.loadFileIntoBuffer(filename, sampleData);
		return new Pair<>(sampleData, sampleDataSampleRate);
	}

	@Deprecated
	public static Generator noteOnFMGen(int pitch, int velocity) {
		Generator gen = new FMGenerator(pitch, velocity);
		return gen;
	}
	
	@Deprecated
	public static Generator temporaryFMGen(int pitch, int velocity, int duration) {
		Generator gen = noteOnFMGen(pitch, velocity);
		gen.noteOffAfterDuration(duration);
		return gen;
	}
	
	@Deprecated
	public static Generator noteOnSampleFileGen(MultiChannelBuffer buf, float samplerate, int pitch, int velocity) {
		Generator gen = new SamplerFileGenerator(buf, samplerate, pitch, velocity);
		return gen;
	}
	
	// end of deprecated
	/////////////////////////////////////////////////

	
	

	//this class is a singleton
	private GeneratorFactory(){
	} 
	
	
	//Oscillator Factory
	public static Generator noteOnOscillatorGen(int pitch, int velocity) {
		Generator gen = new OscillatorGenerator(pitch, velocity, true);
		return gen;
	}
	
	public static Generator noteOnOscillatorGen(String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		return noteOnOscillatorGen(pitch, velocity);
	}
	
	public static Generator temporaryOscillatorGen(int pitch, int velocity, int duration) {
		Generator gen = noteOnOscillatorGen(pitch, velocity);
		gen.noteOffAfterDuration(duration);
		return gen;
	}
	
	
	//FM factory
	public static Generator noteOnFMGen(float carrierFreq, float carrierAmp, String carrierWave,
			float modFreq, float modAmp, String modWave) {
		Generator gen = new FMGenerator(carrierFreq, carrierAmp, carrierWave, modFreq, modAmp, modWave);
		return gen;
	}

	public static Generator noteOnFMGen(String[] parameters){
		float carrierFreq = Float.parseFloat(parameters[0]);
		float carrierAmp = Float.parseFloat(parameters[1]);
		String carrierWave = parameters[2];
		
		float modFreq = Float.parseFloat(parameters[3]);
		float modAmp = Float.parseFloat(parameters[4]);
		String modWave = parameters[5];
		
		return noteOnFMGen(carrierFreq, carrierAmp, carrierWave, modFreq, modAmp, modWave);
	}
	
	public static Generator temporaryFMGen(float carrierFreq, float carrierAmp, String carrierWave,
			float modFreq, float modAmp, String modWave, int duration) {
		Generator gen = noteOnFMGen(carrierFreq, carrierAmp, carrierWave, modFreq, modAmp, modWave);
		gen.noteOffAfterDuration(duration);
		return gen;
	}	
	
	
	//SampleFile factory
	public static Generator noteOnSampleFileGen(String fileStream, int pitch, int velocity, boolean shouldLoop) {
		Generator gen = new SamplerFileGenerator(fileStream, pitch, velocity, shouldLoop);
		return gen;
	}
	
	public static Generator noteOnSampleFileGen(String[] parameters) {
		String filename = parameters[0];
		int pitch = Integer.parseInt(parameters[1]);
		int velocity = Integer.parseInt(parameters[2]);
		boolean shouldLoop = Boolean.parseBoolean(parameters[3]);
		
		return noteOnSampleFileGen(filename, pitch, velocity, shouldLoop);
	}
	
	public static Generator temporarySampleFileGen(String filename, int pitch, int velocity, int duration, boolean shouldLoop) {
		Generator gen = noteOnSampleFileGen(filename, pitch, velocity, shouldLoop);
		gen.noteOffAfterDuration(duration);
		return gen;
	}
	
	//Live Input factory
	public static Generator noteOnLiveInpuGen(int pitch, int velocity) {
		Generator gen = new LiveInputGenerator(pitch, velocity);
//		Generator gen = new LiveInputGeneratorExtendingLiveInput(pitch, velocity);
//		Generator gen = new LiveInputGeneratorExtendingOscil(pitch, velocity);
		return gen;
	}
	
	public static Generator noteOnLiveInpuGen(String[] parameters) {
		int pitch = Integer.parseInt(parameters[1]);
		int velocity = Integer.parseInt(parameters[2]);
		return noteOnLiveInpuGen(pitch, velocity);
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
		gen.unpatchOutput(SoundEngine.out);
		return gen;
	}
	
	public synchronized static Generator patch(Generator gen) {
		gen.patchOutput(SoundEngine.out);
		return gen;
	}
	
	public synchronized static AudioStream getInput() {
//	return minim.getInputStream( Minim.MONO, out.bufferSize(), out.sampleRate(),out.getFormat().getSampleSizeInBits());
		return SoundEngine.in;
	}

	public static Generator createGenerator(String type, String[] parameters) {
		Generator gen = null;

		if (type.equalsIgnoreCase("OSCILLATOR"))
			gen = noteOnOscillatorGen(parameters);
		if (type.equalsIgnoreCase("FM"))
			gen = noteOnFMGen(parameters);
		if (type.equalsIgnoreCase("SAMPLE"))
			gen = noteOnSampleFileGen(parameters);
		if (type.equalsIgnoreCase("LIVEINPUT"))
			gen = noteOnLiveInpuGen(parameters);
		
		return gen;
	}


}
