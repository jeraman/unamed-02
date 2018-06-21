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

public class OscillatorGenerator extends Oscil implements AbstractGenerator, Runnable {

	private float frequency;
	private float amplitude;
	private int duration;
	private String waveform;
	private UGen patched;
	private List<OscillatorGeneratorObserver> observers;

	private static final float amplitudeNormalizer = 5.0f;

	public OscillatorGenerator(int pitch, int velocity, String wf, int duration) {
		this((float) MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), wf, duration);
	}

	public OscillatorGenerator(float frequencyInHertz, float amplitude, String wf, int duration) {
		super(frequencyInHertz, amplitude, getWaveformType(wf));

		this.frequency = frequencyInHertz;
		// this.amplitude = amplitude;
		this.setAmplitude(amplitude);
		this.waveform = wf;

		this.observers = new ArrayList<OscillatorGeneratorObserver>();

		this.patched = this;

		this.duration = duration;

		if (this.shouldNoteOffWithDuration())
			this.noteOffAfterDuration(this.duration);
	}

	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");

		if (parts[0].trim().equalsIgnoreCase("frequency"))
			this.setFrequency(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("amplitude"))
			this.setAmplitude(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("duration"))
			this.setDuration((int) Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("waveform"))
			this.setWaveform(parts[1]);
	}

	protected int getDuration() {
		return duration;
	}

	protected void setDuration(int duration) {
		this.duration = duration;
	}

	protected boolean shouldNoteOffWithDuration() {
		return this.duration > 0;
	}

	protected float getFrequency() {
		return frequency;
	}

	public void setFrequencyFromPitch(int pitch) {
		this.setFrequency((float) MusicTheory.freqFromMIDI(pitch));
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
		super.setAmplitude(amplitude / amplitudeNormalizer);
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
		if (!this.isClosed())
			GeneratorFactory.unpatch(this);
	}

	public void mute() {
		if (!this.isClosed()) {
			this.setAmplitude(0);
			this.setFrequency(0);
		}
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
		// stop playing!
		System.out.println("muting...");
		// GeneratorFactory.unpatch(this);
		// this.noteOff();
		this.mute();
	}

	private static Waveform getWaveformType(String waveName) {
		return Util.getWaveformType(waveName);
	}

	// if frequency is negative, frequency should be unlocked for changes
	private float getRightFrequencyForClone(int newPitch) {
		if (this.frequency <= 0)
			return MusicTheory.freqFromMIDI(newPitch);
		else
			return this.frequency;
	}

	// if amplitude is negative, amplitude should be unlocked for changes
	private float getRightAmplitudeForClone(int newVelocity) {
		if (this.amplitude <= 0)
			return Util.mapFromMidiToAmplitude(newVelocity);
		else
			return this.amplitude;
	}

	public AbstractGenerator cloneWithPitchAndVelocityIfUnlocked(int newPitch, int newVelocity) {
		float newFreq = getRightFrequencyForClone(newPitch);
		float newAmp = getRightAmplitudeForClone(newVelocity);

		return clone(newFreq, newAmp);
	}

	@Override
	public AbstractGenerator cloneWithPitch(int newPitch) {
		float newFreq = MusicTheory.freqFromMIDI(newPitch);
		return this.clone(newFreq, this.amplitude);
	}

	@Override
	public AbstractGenerator cloneWithPitchAndVelocity(int newPitch, int newVelocity) {
		return cloneWithPitchAndVelocityIfUnlocked(newPitch, newVelocity);
	}
	
	public AbstractGenerator clone(int newPitch, int newVelocity) {
		return clone(MusicTheory.freqFromMIDI(newPitch), Util.mapFromMidiToAmplitude(newVelocity));
	}

	private AbstractGenerator clone(float newFreq, float newAmp) {
		OscillatorGenerator clone = new OscillatorGenerator(newFreq, newAmp, this.waveform, this.duration);
		this.linkForFutureChanges(clone);
		return clone;
	}

	private void linkForFutureChanges(OscillatorGenerator clone) {
		new OscillatorGeneratorObserver(this, clone);
	}

	public void unlinkOldObservers() {
		for (int i = observers.size() - 1; i >= 0; i--)
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
		this.observers.add((OscillatorGeneratorObserver) observer);
	}

	@Override
	public synchronized void notifyAllObservers() {
		synchronized (observers) {
			for (GeneratorObserver observer : observers)
				observer.update();
		}
	}

	@Override
	public synchronized void notifyAllObservers(String updatedParameter) {
		synchronized (observers) {
			for (GeneratorObserver observer : observers)
				observer.update(updatedParameter);
		}
	}

}
