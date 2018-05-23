package generators;

import ddf.minim.AudioOutput;
import ddf.minim.UGen;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waveform;
import ddf.minim.ugens.Waves;
import musicalTasksTest.Generator;
import musicalTasksTest.GeneratorFactory;
import musicalTasksTest.MusicTheory;
import musicalTasksTest.Util;

public class OscillatorGenerator extends Oscil implements Generator,Runnable {

	int duration;
	
	public OscillatorGenerator(int pitch, int velocity, boolean usesMidiValue) {
		this(pitch, velocity, Waves.SINE, usesMidiValue);
	}
	
	public OscillatorGenerator(int pitch, int velocity, Waveform wf,  boolean usesMidiValue) {
		super((float)MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), wf);
		System.out.println(Util.mapFromMidiToAmplitude(velocity));
		System.out.println((float)MusicTheory.freqFromMIDI(pitch));
	}
	
	public OscillatorGenerator(float frequencyInHertz, float amplitude) {
		super(frequencyInHertz, amplitude);
	}
	
	public OscillatorGenerator(float frequencyInHertz, float amplitude, Waveform wf) {
		super(frequencyInHertz, amplitude, wf);
	}

	@Override
	public UGen patchEffect(UGen effect) {
		return super.patch(effect);
	}

	@Override
	public void patchOutput(AudioOutput out) {
		super.patch(out);
	}

	@Override
	public void unpatchEffect(UGen effect) {
		super.unpatch(effect);
	}

	@Override
	public void unpatchOutput(AudioOutput out) {
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

	@Override
	public void close() {
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
