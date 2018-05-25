package generators;

import augmenters.MusicTheory;
import ddf.minim.AudioOutput;
import ddf.minim.UGen;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waveform;
import ddf.minim.ugens.Waves;
import util.Util;

public class FMGenerator extends Oscil implements Generator,Runnable{

	private Oscil fm;
	
	private int	  duration;
	
	private float carrierFreq;
	private float carrierAmp;
	private Waveform carrierWave;
	private float modFreq;
	private float modAmp;
	private Waveform modWave;
	
	
	public FMGenerator (int pitch, int velocity) {
		this((float)MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), Waves.SINE,
				200.f, 50.f, Waves.QUARTERPULSE);
	}
	
	public FMGenerator (float carrierFreq, float carrierAmp, Waveform carrierWave,
						float modFreq, float modAmp, Waveform modWave) {
		super(carrierFreq, carrierAmp, carrierWave);
		
		this.carrierFreq = carrierFreq;
		this.carrierAmp  = carrierAmp;
		this.carrierWave = carrierWave;
		this.modFreq	 = modFreq;
		this.modAmp		 = modAmp;
		this.modWave 	 = modWave;
		
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
		GeneratorFactory.patch(this);
	}

	@Override
	public void noteOff() {
		// TODO Auto-generated method stub
		GeneratorFactory.unpatch(this);
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
		//GeneratorFactory.unpatch(this);
		this.noteOff();
	}

	@Override
	public Generator cloneInADifferentPitch(int newPitch) {
		float newFreq = MusicTheory.freqFromMIDI(newPitch);
		return new FMGenerator(newFreq, carrierAmp, carrierWave, modFreq, modAmp, modWave);
	}

}
