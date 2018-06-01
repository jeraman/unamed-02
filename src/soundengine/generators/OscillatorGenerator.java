package soundengine.generators;

import ddf.minim.AudioOutput;
import ddf.minim.UGen;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waveform;
import ddf.minim.ugens.Waves;
import soundengine.MusicTheory;
import soundengine.util.Util;

public class OscillatorGenerator extends Oscil implements Generator,Runnable {

	int duration;
	
	float frequency;
	float amplitude;
	private Waveform wf;
	
	
	private UGen patched;
	
	
	public OscillatorGenerator() {
		this(0, 0, true);
	}
	
	public OscillatorGenerator(int pitch, int velocity, boolean usesMidiValue) {
		this(pitch, velocity, Waves.SINE, usesMidiValue);
	}
	
	public OscillatorGenerator(int pitch, int velocity, Waveform wf,  boolean usesMidiValue) {
		this((float)MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), wf);
	}
	
	public OscillatorGenerator(float frequencyInHertz, float amplitude) {
		this(frequencyInHertz, amplitude, Waves.SINE);
	}
	
	public OscillatorGenerator(float frequencyInHertz, float amplitude, Waveform wf) {
		super(frequencyInHertz, amplitude, wf);
		
		this.frequency = frequencyInHertz;
		this.amplitude  = amplitude;
		this.wf = wf;
		
		this.patched = this;
	}


	@Override
	public void patchEffect(UGen effect) {
		patched = patched.patch(effect);
	}

	@Override
	public void patchOutput(AudioOutput out) {
		patched.patch(out);
	}

	@Override
	public void unpatchEffect(UGen effect) {
		patched.unpatch(effect);
		super.unpatch(effect);
	}

	@Override
	public void unpatchOutput(AudioOutput out) {
		patched.unpatch(out);
		super.unpatch(out);
	}

	@Override
	public void noteOn() {
		// TODO Auto-generated method stub
		GeneratorFactory.patch(this);
	}

	@Override
	public void noteOff() {
		// TODO Auto-generated method stub
		GeneratorFactory.unpatch(this);
	}

	public void noteOffAfterDuration(int duration) {
		this.duration = duration;
		System.out.println("waiting! " + duration);
		Runnable r = this;
		new Thread(r).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Util.delay(this.duration);
		//stop playing!
		System.out.println("stop playing!");
		//GeneratorFactory.unpatch(this);
		this.noteOff();
	}

	@Override
	public Generator clone(int newPitch) {
		float newFreq = MusicTheory.freqFromMIDI(newPitch);
		return new OscillatorGenerator(newFreq, this.amplitude, this.wf);
	}
	
	@Override
	public Generator clone(int newPitch, int newVelocity) {
		float newFreq = MusicTheory.freqFromMIDI(newPitch);
		float newAmp = Util.mapFromMidiToAmplitude(newVelocity);
		return new OscillatorGenerator(newFreq, newAmp, this.wf);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		this.wf = null;
		this.patched = null;
	}
}
