package soundengine.generators;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.spi.AudioStream;
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
	public static AbstractGenerator noteOnFMGen(int pitch, int velocity) {
		AbstractGenerator gen = new FMGenerator(pitch, velocity, "SINE", 5.0f, 5.0f, "SINE", 1000);
		return gen;
	}
	
	@Deprecated
	public static AbstractGenerator temporaryFMGen(int pitch, int velocity, int duration) {
		AbstractGenerator gen = noteOnFMGen(pitch, velocity);
		gen.noteOffAfterDuration(duration);
		return gen;
	}
	
	@Deprecated
	public static AbstractGenerator noteOnSampleFileGen(MultiChannelBuffer buf, float samplerate, int pitch, int velocity) {
		AbstractGenerator gen = new SampleFileGenerator(buf, samplerate, pitch, velocity);
		return gen;
	}
	
	// end of deprecated
	/////////////////////////////////////////////////


	//this class is a singleton
	private GeneratorFactory(){
	}
	
	public static AbstractGenerator createGenerator(String type, String[] parameters) {
		AbstractGenerator gen = null;

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
	
	public static void updateGenerator(AbstractGenerator gen, String[] parameters) {
		if (gen instanceof OscillatorGenerator)
			updateOscillatorGen((OscillatorGenerator)gen, parameters);
		if (gen instanceof FMGenerator)
			updateFMGen((FMGenerator)gen, parameters);
		if (gen instanceof SampleFileGenerator)
			updateSampleFileGen((SampleFileGenerator)gen, parameters);
		if (gen instanceof LiveInputGenerator)
			updateLiveInpuGen((LiveInputGenerator)gen, parameters);
	}
	
	public static void updateGenerator(AbstractGenerator gen, String singleParameter) {
		gen.updateParameterFromString(singleParameter);
		gen.notifyAllObservers(singleParameter);
	}

	//Oscillator Factory
	public static AbstractGenerator noteOnOscillatorGen(float freq, float amp, String waveform, int duration) {
		AbstractGenerator gen = new OscillatorGenerator(freq, amp, waveform, duration);
		return gen;
	}
	
	public static AbstractGenerator noteOnOscillatorGen(String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		float amp = Float.parseFloat(parameters[1]);
		String waveform = parameters[2];
		int duration = Integer.parseInt(parameters[3]);
		return noteOnOscillatorGen(freq, amp, waveform, duration);
	}
	
	
	public static void updateOscillatorGen(OscillatorGenerator gen, String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		String waveform = parameters[2];
		int duration = Integer.parseInt(parameters[3]);
		
		System.out.println("updateOscillatorGen " + gen);
		System.out.println("pitch " + pitch);
		System.out.println("velocity " + velocity);
		System.out.println("waveform " + waveform);
		System.out.println("duration " + duration);
		
		gen.setFrequencyFromPitch(pitch);
		gen.setAmplitudeFromVelocity(velocity);
		gen.setWaveform(waveform);
		gen.setDuration(duration);
		
		gen.notifyAllObservers();
	}
	
	//FM factory
	public static AbstractGenerator noteOnFMGen(float carrierFreq, float carrierAmp, String carrierWave,
			float modFreq, float modAmp, String modWave, int duration) {
		AbstractGenerator gen = new FMGenerator(carrierFreq, carrierAmp, carrierWave, modFreq, modAmp, modWave, duration);
		return gen;
	}

	public static AbstractGenerator noteOnFMGen(String[] parameters){
		float carrierFreq = Float.parseFloat(parameters[0]);
		float carrierAmp = Float.parseFloat(parameters[1]);
		String carrierWave = parameters[2];
		
		float modFreq = Float.parseFloat(parameters[3]);
		float modAmp = Float.parseFloat(parameters[4]);
		String modWave = parameters[5];
		int duration = Integer.parseInt(parameters[6]);
		
		return noteOnFMGen(carrierFreq, carrierAmp, carrierWave, modFreq, modAmp, modWave, duration);
	}


	private static void updateFMGen(FMGenerator gen, String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		String carrierWave = parameters[2];
		
		float modFreq = Float.parseFloat(parameters[3]);
		float modAmp = Float.parseFloat(parameters[4]);
		String modWave = parameters[5];
		int duration = Integer.parseInt(parameters[6]);
		
		gen.setCarrierFreqFromPitch(pitch);
		gen.setCarrierAmpFromVelocity(velocity);
		gen.setCarrierWave(carrierWave);
		gen.setModFreq(modFreq);
		gen.setModAmp(modAmp);
		gen.setModWave(modWave);
		gen.setDuration(duration);
		
		gen.notifyAllObservers();
	}
	
	
	//SampleFile factory
	public static AbstractGenerator noteOnSampleFileGen(String fileStream, int pitch, int velocity, boolean shouldLoop, int duration) {
		AbstractGenerator gen = new SampleFileGenerator(fileStream, pitch, velocity, shouldLoop, duration);
		return gen;
	}
	
	public static AbstractGenerator noteOnSampleFileGen(String[] parameters) {
		String filename = parameters[0];
		int pitch = Integer.parseInt(parameters[1]);
		int velocity = Integer.parseInt(parameters[2]);
		boolean shouldLoop = Boolean.parseBoolean(parameters[3]);
		int duration = Integer.parseInt(parameters[4]);
		
		return noteOnSampleFileGen(filename, pitch, velocity, shouldLoop, duration);
	}
	

	private static void updateSampleFileGen(SampleFileGenerator gen, String[] parameters) {
		// TODO Auto-generated method stub
		String filename = parameters[0];
		int pitch = Integer.parseInt(parameters[1]);
		int velocity = Integer.parseInt(parameters[2]);
		boolean shouldLoop = Boolean.parseBoolean(parameters[3]);
		int duration = Integer.parseInt(parameters[4]);
		
		gen.setFilename(filename);
		gen.setPitch(pitch);
		gen.setVelocity(velocity);
		gen.setLoopStatus(shouldLoop);
		gen.setDuration(duration);
		
		gen.notifyAllObservers();
	}
	
	//Live Input factory
	public static AbstractGenerator noteOnLiveInpuGen(int pitch, int velocity, int duration) {
		AbstractGenerator gen = new LiveInputGenerator(pitch, velocity, duration);
//		Generator gen = new LiveInputGeneratorExtendingLiveInput(pitch, velocity, duration);
//		Generator gen = new LiveInputGeneratorExtendingOscil(pitch, velocity, duration);
		return gen;
	}
	
	public static AbstractGenerator noteOnLiveInpuGen(String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		int duration = Integer.parseInt(parameters[2]);
		
		return noteOnLiveInpuGen(pitch, velocity, duration);
	}
	

	@Deprecated
	public static AbstractGenerator noteOnLiveInpuGen() {
		AbstractGenerator gen = new LiveInputGenerator();
		return gen;
	}
	
	@Deprecated
	public static AbstractGenerator temporaryLiveInpuGen(int duration) {
		AbstractGenerator gen = noteOnLiveInpuGen();
		gen.noteOffAfterDuration(duration);
		return gen;
	}
	
	private static void updateLiveInpuGen(LiveInputGenerator gen, String[] parameters) {
		// TODO Auto-generated method stub
		int pitch = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		int duration = Integer.parseInt(parameters[2]);
		
		gen.setPitch(pitch);
		gen.setVelocity(velocity);
		gen.setDuration(duration);

		gen.notifyAllObservers();
		
	}
	
	////////////////////////////
	//general note off for all generators
	public synchronized static AbstractGenerator unpatch(AbstractGenerator gen) {
		gen.unpatchOutput(SoundEngine.out);
		return gen;
	}
	
	public synchronized static AbstractGenerator patch(AbstractGenerator gen) {
		gen.patchOutput(SoundEngine.out);
		return gen;
	}
	
	public synchronized static AudioStream getInput() {
//	return minim.getInputStream( Minim.MONO, out.bufferSize(), out.sampleRate(),out.getFormat().getSampleSizeInBits());
		return SoundEngine.in;
	}
	
	public synchronized static Pair<MultiChannelBuffer, Float> loadMultiChannelBufferFromFile(String filename) {
		MultiChannelBuffer sampleData = new MultiChannelBuffer(1, 1);
		float sampleDataSampleRate = SoundEngine.minim.loadFileIntoBuffer(filename, sampleData);
		return new Pair<>(sampleData, sampleDataSampleRate);
	}
}
