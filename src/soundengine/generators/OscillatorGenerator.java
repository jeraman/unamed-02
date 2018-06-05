package soundengine.generators;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.AudioOutput;
import ddf.minim.UGen;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waveform;
import ddf.minim.ugens.Waves;
import soundengine.util.MusicTheory;
import soundengine.util.Util;

public class OscillatorGenerator extends Oscil implements Generator,Runnable {

	private int duration;
	
	private float frequency;
	private float amplitude;
	private String waveform;
	
	private UGen patched;
	
	private List<OscillatorGeneratorObserver> observers;
	
	@Deprecated
	public OscillatorGenerator() {
		this(0, 0, true);
	}
	
	@Deprecated
	public OscillatorGenerator(int pitch, int velocity, boolean usesMidiValue) {
		this(pitch, velocity, "SINE", usesMidiValue);
	}
	
	@Deprecated
	public OscillatorGenerator(float frequencyInHertz, float amplitude) {
		this(frequencyInHertz, amplitude, "SINE");
	}
	
	public OscillatorGenerator(int pitch, int velocity, String wf,  boolean usesMidiValue) {
		this((float)MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), wf);
	}
	
	
	public OscillatorGenerator(float frequencyInHertz, float amplitude, String wf) {
		super(frequencyInHertz, amplitude, getWaveformType(wf));
		
		this.frequency = frequencyInHertz;
		this.amplitude  = amplitude;
		this.waveform = wf;
		
		this.observers = new ArrayList<OscillatorGeneratorObserver>();
		
		this.patched = this;
	}
	
	
	protected int getDuration() {
		return duration;
	}

	protected void setDuration(int duration) {
		this.duration = duration;
	}

	protected float getFrequency() {
		return frequency;
	}

	public void setFrequencyFromPitch(int pitch) {
		this.setFrequency((float)MusicTheory.freqFromMIDI(pitch));
	}
	
	public void setFrequency(float frequency) {
		this.frequency = frequency;
		super.setFrequency(this.frequency);
	}

	protected float getAmplitude() {
		return amplitude;
	}

	public void setAmplitudeFromVelocity(int velocity) {
		this.setAmplitude(Util.mapFromMidiToAmplitude(velocity));
	}
	
	public void setAmplitude(float amplitude) {
		this.amplitude = amplitude;
		super.setAmplitude(amplitude);
	}

	public Waveform getWaveform() {
		return getWaveformType(waveform);
	}

	public String getWaveformString() {
		return this.waveform;
	}

	public void setWaveform(String wf) {
		this.waveform = wf;
		super.setWaveform(getWaveform());
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
	
	private static Waveform getWaveformType (String waveName) {
		Waveform result = null;
		
		if (waveName.equalsIgnoreCase("PHASOR"))
			result = Waves.PHASOR;
		if (waveName.equalsIgnoreCase("QUARTERPULSE"))
			result = Waves.QUARTERPULSE;
		if (waveName.equalsIgnoreCase("SAW"))
			result = Waves.SAW;
		if (waveName.equalsIgnoreCase("SINE"))
			result = Waves.SINE;
		if (waveName.equalsIgnoreCase("SQUARE"))
			result = Waves.SQUARE;
		if (waveName.equalsIgnoreCase("TRIANGLE"))
			result = Waves.TRIANGLE;
		
		return result;
	}

	@Override
	public Generator clone(int newPitch) {
		float newFreq = MusicTheory.freqFromMIDI(newPitch);
		return this.cloneWithFreqAndAmplitude(newFreq, this.amplitude);
	}
	
	@Override
	public Generator clone(int newPitch, int newVelocity) {
		float newFreq = MusicTheory.freqFromMIDI(newPitch);
		float newAmp = Util.mapFromMidiToAmplitude(newVelocity);
		return this.cloneWithFreqAndAmplitude(newFreq, newAmp);
	}
	
	private Generator cloneWithFreqAndAmplitude(float newFreq, float newAmp) {
		OscillatorGenerator clone = new OscillatorGenerator(newFreq, newAmp, this.waveform);
		this.linkForFutureChanges(clone);
		return clone;
	}
	
	private void linkForFutureChanges (OscillatorGenerator clone) {
		new OscillatorGeneratorObserver(this, clone);
	}
	
	public void unlinkOldObservers () {
		for (int i = observers.size()-1; i >= 0; i--)
			if (observers.get(i).isClosed())
				this.observers.remove(i);
	}

	public boolean isClosed() {
		if (this.observers == null)
			return true;
		else 
			return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		this.waveform = null;
		this.patched = null;
		this.observers.clear();
		this.observers = null;
	}

	@Override
	public void attach(GeneratorObserver observer) {
		this.observers.add((OscillatorGeneratorObserver)observer);
	}

	@Override
	public void notifyAllObservers() {
		for (GeneratorObserver observer : observers)
			observer.update();
	}
}
