package generators;

import ddf.minim.AudioOutput;
import ddf.minim.UGen;
import ddf.minim.spi.AudioStream;
import ddf.minim.ugens.LiveInput;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Vocoder;
import ddf.minim.ugens.Waves;
import musicalTasksTest.Generator;
import musicalTasksTest.GeneratorFactory;
import musicalTasksTest.Util;
import musicalTasksTest.MusicTheory;

public class LiveInputGenerator extends LiveInput implements Generator,Runnable {
	
	private Vocoder vocode;
	private Oscil mod;
	private int duration;
	private boolean hasVocode;
	
	
	public LiveInputGenerator(AudioStream inputStream) {
		this(inputStream, false, 0, 0);
	}
	
	public LiveInputGenerator(AudioStream inputStream, int pitch, int velocity) {
		this(inputStream, true, pitch, velocity);
	}
	
	public LiveInputGenerator(AudioStream inputStream, boolean hasVocode, int pitch, int velocity) {
		super(inputStream);
		this.hasVocode = hasVocode;
		
		if (hasVocode) {
			vocode = new Vocoder(1024, 8);
			this.patch(vocode.modulator);
			mod = new Oscil((float) MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), Waves.SAW);
		}
	}

	
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
	

	@Override
	public void noteOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noteOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
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
		GeneratorFactory.noteOffGen(this);
	}

}