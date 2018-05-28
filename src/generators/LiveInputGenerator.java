package generators;

import augmenters.MusicTheory;
import ddf.minim.AudioOutput;
import ddf.minim.UGen;
import ddf.minim.ugens.Bypass;
import ddf.minim.ugens.LiveInput;
import ddf.minim.ugens.Multiplier;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Summer;
import ddf.minim.ugens.Vocoder;
import ddf.minim.ugens.Waves;
import util.Util;

public class LiveInputGenerator extends Oscil implements Generator,Runnable {
	
	private static LiveInput micInput;
	private static Vocoder 	vocode;
	private static Summer 	synth;
	
//	private Oscil mod;
	private int duration;
	private int pitch;
	private int velocity;
	private boolean hasVocode;
	
	
	public LiveInputGenerator() {
		this(false, 0, 0);
	}
	
	public LiveInputGenerator(int pitch, int velocity) {
		this(true, pitch, velocity);
	}
	
	public LiveInputGenerator(boolean hasVocode, int pitch, int velocity) {
		super((float)MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), Waves.SAW);	
		
		if (!isClassInitialized())
			setupClass();
		
		this.hasVocode = hasVocode;
		this.pitch = pitch;
		this.velocity = velocity;
			
//		if (hasVocode) {
			//vocode = new Vocoder(1024, 8);
			//this.patch(vocode.modulator);
			//mod = new Oscil((float) MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), Waves.SAW);
			
//		}
	}
	
	public boolean isClassInitialized() {
		return (LiveInputGenerator.micInput != null);
	}
	
	public static void setupClass() {
		LiveInputGenerator.micInput = new LiveInput(GeneratorFactory.getInput());
		LiveInputGenerator.vocode	= new Vocoder(1024, 8);
		LiveInputGenerator.synth	= new Summer();
		
		LiveInputGenerator.micInput.patch(LiveInputGenerator.vocode.modulator);
		LiveInputGenerator.synth.patch(vocode).patch(GeneratorFactory.out);
	}
	
	public static void closeClass() {
		LiveInputGenerator.micInput.close();
		//LiveInputGenerator.synth.unpatch(vocode);
		LiveInputGenerator.micInput = null;
		LiveInputGenerator.vocode 	= null;
		LiveInputGenerator.synth 	= null;
		vocode.unpatch(GeneratorFactory.out);
		synth.unpatch(vocode);
	}
	
	//TODO: manage how effect PATCHING is going to work with this new structure
	@Override
	public UGen patchEffect(UGen effect) {
		if (hasVocode)
			return LiveInputGenerator.synth.patch(effect);
		else
			return LiveInputGenerator.micInput.patch(effect);
	}

	@Override
	public void patchOutput(AudioOutput out) {
		if (hasVocode) {
			this.patch(synth);
			//synth.patch(vocode).patch(out);
		} else
			LiveInputGenerator.micInput.patch(out);
	}

	//TODO: manage how effect UNPATCHING effect is going to work with this new structure
	@Override
	public void unpatchEffect(UGen effect) {
		if (hasVocode) 
			LiveInputGenerator.synth.unpatch(effect);
		else
			LiveInputGenerator.micInput.unpatch(effect);
	}

	@Override
	public void unpatchOutput(AudioOutput out) {
		if (hasVocode) {
			this.unpatch(synth);
		} else
			LiveInputGenerator.micInput.unpatch(out);
	}
	/*
	@Override
	public UGen patchEffect(UGen effect) {
		if (hasVocode)
			return mod.patch(vocode).patch(effect);
		else
			return super.patch(effect);
	}

	@Override
	public void patchOutput(AudioOutput out) {
		if (hasVocode)
			mod.patch(vocode).patch(out);
		else
			super.patch(out);
	}

	@Override
	public void unpatchEffect(UGen effect) {
		if (hasVocode) {
			vocode.unpatch(effect);
			mod.unpatch(vocode);
		} else
			super.unpatch(effect);
	}

	@Override
	public void unpatchOutput(AudioOutput out) {
		if (hasVocode) {
			vocode.unpatch(out);
			mod.unpatch(vocode);
		} else
			super.unpatch(out);
	}
	*/

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
		this.noteOff();
	}

	@Override
	public Generator cloneInADifferentPitch(int newPitch) {
		return new LiveInputGenerator(newPitch, this.velocity);
	}
	
	public void close() {
		//super.close();
		//this.vocode = null;
		//this.mod = null;
	}

}
