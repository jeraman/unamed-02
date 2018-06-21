package soundengine.generators;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.AudioOutput;
import ddf.minim.UGen;
import ddf.minim.ugens.LiveInput;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Summer;
import ddf.minim.ugens.Vocoder;
import ddf.minim.ugens.Waves;
import soundengine.SoundEngine;
import soundengine.util.MusicTheory;
import soundengine.util.Util;

public class LiveInputGenerator extends Oscil implements AbstractGenerator, Runnable {

	private static LiveInput micInput;
	private static Vocoder vocode;
	private static Summer synth;

	private static UGen patched;
	private static ArrayList<UGen> activeEffects;

	private int duration;
	private int pitch;
	private int velocity;
	private boolean hasVocode;
	
	private List<LiveInputGeneratorObserver> observers;

	@Deprecated
	public LiveInputGenerator() {
		this(false, 0, 0, -1);
	}

	public LiveInputGenerator(int pitch, int velocity, int duration) {
		this(true, pitch, velocity, duration);
	}

	public LiveInputGenerator(boolean hasVocode, int pitch, int velocity, int duration) {
		super((float) MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), Waves.SAW);

		if (!isClassInitialized())
			setupClass();

		this.hasVocode = hasVocode;
		this.pitch = pitch;
		this.velocity = velocity;
		
		this.observers = new ArrayList<LiveInputGeneratorObserver>();
		
		this.duration = duration;
		
		if (this.shouldNoteOffWithDuration())
			this.noteOffAfterDuration(this.duration);

	}

	public boolean isClassInitialized() {
		return (LiveInputGenerator.micInput != null);
	}

	public static void setupClass() {
		LiveInputGenerator.micInput = new LiveInput(GeneratorFactory.getInput());
		LiveInputGenerator.vocode = new Vocoder(1024, 8);
		LiveInputGenerator.synth = new Summer();

		LiveInputGenerator.micInput.patch(LiveInputGenerator.vocode.modulator);
		
		patched = LiveInputGenerator.synth.patch(vocode);
		patched.patch(SoundEngine.out);

		activeEffects = new ArrayList<UGen>();
	}

	public static void closeClass() {
		LiveInputGenerator.micInput.close();
		LiveInputGenerator.micInput = null;
		LiveInputGenerator.vocode = null;
		LiveInputGenerator.synth = null;

		patched.unpatch(SoundEngine.out);
		vocode.unpatch(SoundEngine.out);
		synth.unpatch(vocode);

		activeEffects.clear();
		activeEffects = null;
	}

	// add effects methods
//	private synchronized void addEffectIfIsNew(UGen effect) {
//		if (isANewEffect(effect))
//			addEffect(effect);
//	}
//	
//
//	private synchronized static boolean isANewEffect(UGen effect) {
//		boolean result = true;
//		
//		for (UGen e:activeEffects) 
//			if (e.getClass().equals(effect.getClass()))
//				result = false;
//		
//		return result;
//	}
//
//	private synchronized static void addEffect(UGen effect) {
//		// unpatching output
//		patched.unpatch(GeneratorFactory.out);
//
//		System.out.println("there are " + activeEffects.size() + " effects");
//		// adding effect
//		activeEffects.add(effect);
//		patched = LiveInputGenerator.synth.patch(vocode).patch(effect);
//
//		// repatching output
//		patched.patch(GeneratorFactory.out);
//	}
//
//	// remove effects methods
//	private synchronized void removeEffectIfIsActive(UGen effect) {
//		if (isEffectActive(effect))
//			removeEffect(effect);
//	}
//
//	private synchronized static boolean isEffectActive(UGen effect) {
//		return (!isANewEffect(effect));
//	}
//
//	private static void removeEffect(UGen effect) {
//		// unpatching output
//		patched.unpatch(GeneratorFactory.out);
//		// vocode.unpatch(GeneratorFactory.out);
//
//		// removing effect
//		patched.unpatch(effect);
//		activeEffects.remove(effect);
//		
//		patched = LiveInputGenerator.synth.patch(vocode);
//
//		// repatching output
//		patched.patch(GeneratorFactory.out);
//	}
	
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
	
	@Override
	public void patchEffect(UGen effect) {
		//addEffectIfIsNew(effect);
		System.out.println("patchEffect not implemented for class LiveInputGenerator");
	}

	@Override
	public void unpatchEffect(UGen effect) {
		//removeEffectIfIsActive(effect);
		System.out.println("unpatchEffect not implemented for class LiveInputGenerator");
	}

	@Override
	public void patchOutput(AudioOutput out) {
		if (hasVocode) {
			this.patch(synth);
		} else
			LiveInputGenerator.micInput.patch(out);
	}


	@Override
	public void unpatchOutput(AudioOutput out) {
		if (hasVocode) {
			this.unpatch(synth);
		} else
			LiveInputGenerator.micInput.unpatch(out);
	}

	// //TODO: manage how effect PATCHING is going to work with this new
	// structure
	// @Override
	// public void patchEffect(UGen effect) {
	// if (hasVocode)
	// LiveInputGenerator.synth.patch(effect);
	// else
	// LiveInputGenerator.micInput.patch(effect);
	// }
	//
	//
	// @Override
	// public void patchOutput(AudioOutput out) {
	// if (hasVocode) {
	// this.patch(synth);
	// } else
	// LiveInputGenerator.micInput.patch(out);
	// }
	//
	// //TODO: manage how effect UNPATCHING effect is going to work with this
	// new structure
	// @Override
	// public void unpatchEffect(UGen effect) {
	// if (hasVocode)
	// LiveInputGenerator.synth.unpatch(effect);
	// else
	// LiveInputGenerator.micInput.unpatch(effect);
	// }
	//
	// @Override
	// public void unpatchOutput(AudioOutput out) {
	// if (hasVocode) {
	// this.unpatch(synth);
	// } else
	// LiveInputGenerator.micInput.unpatch(out);
	// }

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
		Util.delay(this.duration);
		System.out.println("stop playing!");
		//this.noteOff();
		this.mute();
	}
	
	@Override
	public void attach(GeneratorObserver observer) {
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
	
	public AbstractGenerator cloneWithPitchAndVelocityIfUnlocked(int newPitch, int newVelocity) {
		int rightPitch = getRightPitchForClone(newPitch);
		int rightVelocity = getRightVelocityForClone(newVelocity);
		return clone(rightPitch, rightVelocity, this.duration);
	}

	@Override
	public AbstractGenerator cloneWithPitch(int newPitch) {
		return this.cloneWithPitchAndVelocity(newPitch, this.velocity);
	}
	
	@Override
	public AbstractGenerator cloneWithPitchAndVelocity(int newPitch, int newVelocity) {
		//return this.clone(newPitch, newVelocity);
		return cloneWithPitchAndVelocityIfUnlocked(newPitch, newVelocity);
	}
	
	public AbstractGenerator clone(int newPitch, int newVelocity, int newDuration) {
		LiveInputGenerator clone = new LiveInputGenerator(newPitch, newVelocity, newDuration);
		this.linkForFutureChanges(clone);
		return clone;
	}
	
	private void linkForFutureChanges (LiveInputGenerator clone) {
		new LiveInputGeneratorObserver(this, clone);
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

	public void close() {
		this.observers.clear();
		this.observers = null;
	}

}