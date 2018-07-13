package soundengine.generators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ddf.minim.AudioOutput;
import ddf.minim.UGen;
import ddf.minim.spi.AudioStream;
import ddf.minim.ugens.Bypass;
import ddf.minim.ugens.LiveInput;
import ddf.minim.ugens.Multiplier;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Summer;
import ddf.minim.ugens.Vocoder;
import ddf.minim.ugens.Waves;
import soundengine.SoundEngine;
import soundengine.util.MusicTheory;
import soundengine.util.Util;

public class LiveInputGeneratorExtendingOscil extends Oscil implements AbstractGenerator, Runnable {

	private static LiveInput micInput;
	private static Vocoder vocode;
	private static Summer synth;

	// private Oscil mod;
	private int duration;
	private int pitch;
	private int velocity;
	private boolean hasVocode;
	
	private List<LiveInputGeneratorObserver> observers;

	@Deprecated
	public LiveInputGeneratorExtendingOscil() {
		this(false, 0, 0, -1);
	}

	public LiveInputGeneratorExtendingOscil(int pitch, int velocity, int duration) {
		this(true, pitch, velocity, duration);
	}

	public LiveInputGeneratorExtendingOscil(boolean hasVocode, int pitch, int velocity, int duration) {
		super((float) MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), Waves.SAW);

		if (!isClassInitialized())
			setupClass();

		this.hasVocode = hasVocode;
		this.pitch = pitch;
		this.velocity = velocity;
		
		this.observers = new ArrayList<LiveInputGeneratorObserver>();

		// if (hasVocode) {
		// vocode = new Vocoder(1024, 8);
		// this.patch(vocode.modulator);
		// mod = new Oscil((float) MusicTheory.freqFromMIDI(pitch),
		// Util.mapFromMidiToAmplitude(velocity), Waves.SAW);

		// }
		
		this.duration = duration;
		
		if (this.shouldNoteOffWithDuration())
			this.noteOffAfterDuration(this.duration);
	}

	public boolean isClassInitialized() {
		return (LiveInputGeneratorExtendingOscil.micInput != null);
	}

	public static void setupClass() {
		LiveInputGeneratorExtendingOscil.micInput = new LiveInput(GeneratorFactory.getInput());
		LiveInputGeneratorExtendingOscil.vocode = new Vocoder(1024, 8);
		LiveInputGeneratorExtendingOscil.synth = new Summer();

		LiveInputGeneratorExtendingOscil.micInput.patch(LiveInputGeneratorExtendingOscil.vocode.modulator);
		LiveInputGeneratorExtendingOscil.synth.patch(vocode).patch(SoundEngine.out);
	}

	public static void closeClass() {
		LiveInputGeneratorExtendingOscil.micInput.close();
		// LiveInputGenerator.synth.unpatch(vocode);
		LiveInputGeneratorExtendingOscil.micInput = null;
		LiveInputGeneratorExtendingOscil.vocode = null;
		LiveInputGeneratorExtendingOscil.synth = null;
		vocode.unpatch(SoundEngine.out);
		synth.unpatch(vocode);
	}
	
	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (parts[0].trim().equalsIgnoreCase("pitch"))
			this.setPitch(Integer.parseInt(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("velocity"))
			this.setVelocity(Integer.parseInt(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("duration"))
			this.setDuration((int)Float.parseFloat(parts[1].trim()));
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}


	protected boolean shouldNoteOffWithDuration() {
		return this.duration > 0;
	}
	
	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
		super.setFrequency(MusicTheory.freqFromMIDI(pitch));
	}
	
	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int vel) {
		this.velocity = vel;
		super.setAmplitude(Util.mapFromMidiToAmplitude(velocity));
	}

	// TODO: manage how effect PATCHING is going to work with this new structure
	@Override
	public void patchEffect(UGen effect) {
		if (hasVocode) {
			 LiveInputGeneratorExtendingOscil.synth.patch(effect);
		} else
			LiveInputGeneratorExtendingOscil.micInput.patch(effect);
	}

	// TODO: manage how effect UNPATCHING effect is going to work with this new
	// structure
	@Override
	public void unpatchEffect(UGen effect) {
		if (hasVocode) {
			LiveInputGeneratorExtendingOscil.synth.unpatch(effect);
		} else
			LiveInputGeneratorExtendingOscil.micInput.unpatch(effect);
	}

	@Override
	public void patchOutput(AudioOutput out) {
		if (hasVocode) {
			this.patch(synth);
		} else
			LiveInputGeneratorExtendingOscil.micInput.patch(out);
	}

	@Override
	public void unpatchOutput(AudioOutput out) {
		if (hasVocode) {
			this.unpatch(synth);
		} else
			LiveInputGeneratorExtendingOscil.micInput.unpatch(out);
	}
	/*
	 * @Override public UGen patchEffect(UGen effect) { if (hasVocode) return
	 * mod.patch(vocode).patch(effect); else return super.patch(effect); }
	 * 
	 * @Override public void patchOutput(AudioOutput out) { if (hasVocode)
	 * mod.patch(vocode).patch(out); else super.patch(out); }
	 * 
	 * @Override public void unpatchEffect(UGen effect) { if (hasVocode) {
	 * vocode.unpatch(effect); mod.unpatch(vocode); } else
	 * super.unpatch(effect); }
	 * 
	 * @Override public void unpatchOutput(AudioOutput out) { if (hasVocode) {
	 * vocode.unpatch(out); mod.unpatch(vocode); } else super.unpatch(out); }
	 */

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
		System.out.println("stop playing!");
		//this.noteOff();
		this.mute();
	}
	
	@Override
	public synchronized void attach(GeneratorObserver observer) {
		this.observers.add((LiveInputGeneratorObserver)observer);
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

	//if pitch is negative, pitch should be unlocked for changes
	private int getRightPitchForClone(int newPitch) {
		if (this.pitch <= 0)
			return newPitch;
		else
			return this.pitch;
	}

	// if velocity is negative, velocity should be unlocked for changes
	private int getRightVelocityForClone(int newVelocity) {
		if (this.velocity <= 0)
			return newVelocity;
		else
			return this.velocity;
	}

	public AbstractGenerator cloneWithNewPitchVelocityIfUnlocked(int newPitch, int newVelocity) {
		int rightPitch = getRightPitchForClone(newPitch);
		int rightVelocity = getRightVelocityForClone(newVelocity);
		return cloneWithNewPitchVelocityAndDuration(rightPitch, rightVelocity, this.duration);
	}

//	@Override
//	public AbstractGenerator cloneWithPitch(int newPitch) {
//		return this.cloneWithPitchAndVelocity(newPitch, this.velocity);
//	}
//
//	@Override
//	public AbstractGenerator cloneWithPitchAndVelocity(int newPitch, int newVelocity) {
//		// return this.clone(newPitch, newVelocity);
//		return cloneWithPitchAndVelocityIfUnlocked(newPitch, newVelocity);
//	}

	public AbstractGenerator cloneWithNewPitchVelocityAndDuration(int newPitch, int newVelocity, int newDuration) {
		LiveInputGeneratorExtendingOscil clone = new LiveInputGeneratorExtendingOscil(newPitch, newVelocity, newDuration);
		this.linkForFutureChanges(clone);
		return clone;
	}
	
	private void linkForFutureChanges (LiveInputGeneratorExtendingOscil clone) {
		new LiveInputGeneratorObserver(this, clone);
	}
	
	public synchronized void unlinkOldObservers () {
		synchronized (observers) {
		for (int i = observers.size()-1; i >= 0; i--)
			if (observers.get(i).isClosed())
				this.observers.remove(i);
		}
	}

	public boolean isClosed() {
		if (this.observers == null)
			return true;
		else 
			return false;
	}

	public synchronized void close() {
		// super.close();
		// this.vocode = null;
		// this.mod = null;
		this.observers.clear();
		this.observers = null;
	}

}
