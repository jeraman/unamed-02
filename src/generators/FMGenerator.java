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
	
	private UGen patched;
	
	
	public FMGenerator (int pitch, int velocity) {
		this((float)MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), Waves.SINE, 30.f, 75.f, Waves.QUARTERPULSE);
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

	public void setModAmplitude(float modulateAmount) {
		fm.setAmplitude(modulateAmount);
	}

	public void setCarrierFrequency(float carrierFreq) {
		// wave.setFrequency( carrierFreq );
		fm.offset.setLastValue(carrierFreq);
	}

	@Override
	public synchronized void noteOn() {
		// TODO Auto-generated method stub
		GeneratorFactory.patch(this);
	}

	@Override
	public synchronized void noteOff() {
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
	public Generator cloneInADifferentPitch(int newPitch) {
		float newFreq = MusicTheory.freqFromMIDI(newPitch);
		return new FMGenerator(newFreq, carrierAmp, carrierWave, modFreq, modAmp, modWave);
	}

	@Override
	public void close() {
		this.fm = null;
		this.carrierWave = null;
		this.modWave = null;
	}

}
