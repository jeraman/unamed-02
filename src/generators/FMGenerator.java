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

public class FMGenerator extends Oscil implements Generator,Runnable{

	private Oscil fm;
	private int	  duration;
	
	public FMGenerator (int pitch, int velocity) {
		this((float)MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), Waves.SINE,
				200.f, 50.f, Waves.SINE);
	}
	
	public FMGenerator (float carrierFreq, float carrierAmp, Waveform carrierWave,
						float modFreq, float modAmp, Waveform modWave) {
		super(carrierFreq, carrierAmp, carrierWave);
		fm  = new Oscil( modFreq, modAmp, modWave );
		fm.offset.setLastValue(carrierFreq);
		fm.patch(this.frequency);
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
	
	public void setModFrequency(float modulateFrequency) {
		fm.setFrequency(modulateFrequency);
	}

	public void setModAmplitude(float modulateAmount) {
		fm.setAmplitude(modulateAmount);
	}

	public void setCarrierFrequency(float carrierFreq) {
		// wave.setFrequency( carrierFreq );
		fm.offset.setLastValue(carrierFreq);
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
