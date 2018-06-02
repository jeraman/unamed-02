package soundengine.generators;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.AudioOutput;
import ddf.minim.UGen;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waveform;
import ddf.minim.ugens.Waves;
import soundengine.MusicTheory;
import soundengine.util.Util;

public class FMGenerator extends Oscil implements Generator,Runnable{

	private Oscil fm;
	
	private int	  duration;
	
	private float carrierFreq;
	private float carrierAmp;
	private String carrierWave;
	private float modFreq;
	private float modAmp;
	private String modWave;
	
	private UGen patched;
	
	private List<FMGeneratorObserver> observers;
	
	//TODO: delete deprecated constructors
	@Deprecated
	public FMGenerator() {
		this(0, 0);
	}
	
	@Deprecated
	public FMGenerator (int pitch, int velocity) {
		this((float)MusicTheory.freqFromMIDI(pitch), Util.mapFromMidiToAmplitude(velocity), "SINE", 30.f, 75.f, "QUARTERPULSE");
	}
	
	public FMGenerator (float carrierFreq, float carrierAmp, String carrierWave,
						float modFreq, float modAmp, String modWave) {
		
		super(carrierFreq, carrierAmp, getWaveformType(carrierWave));
		
		this.carrierFreq = carrierFreq;
		this.carrierAmp  = carrierAmp;
		this.carrierWave = carrierWave;
		this.modFreq	 = modFreq;
		this.modAmp		 = modAmp;
		this.modWave 	 = modWave;
				
		fm  = new Oscil( this.modFreq, this.modAmp, getWaveformType(this.modWave));
		fm.offset.setLastValue(carrierFreq);
		fm.patch(this.frequency);
		
		this.observers = new ArrayList<FMGeneratorObserver>();
		
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
	public void attach(GeneratorObserver observer) {
		this.observers.add((FMGeneratorObserver)observer);
	}

	@Override
	public void notifyAllObservers() {
		for (GeneratorObserver observer : observers)
			observer.update();
	}

	@Override
	public Generator clone(int newPitch) {
		float newFreq = MusicTheory.freqFromMIDI(newPitch);
		return cloneWithFreqAndAmplitude(newFreq, carrierAmp);
	}

	@Override
	public Generator clone(int newPitch, int newVelocity) {
		float newFreq = MusicTheory.freqFromMIDI(newPitch);
		float newAmp = Util.mapFromMidiToAmplitude(newVelocity);
		return cloneWithFreqAndAmplitude(newFreq, newAmp);
	}
	
	private Generator cloneWithFreqAndAmplitude(float newFreq, float newAmp) {
		FMGenerator clone = new FMGenerator(newFreq, newAmp, carrierWave, modFreq, modAmp, modWave);
		this.linkForFutureChanges(clone);
		return clone;
	}
	
	private void linkForFutureChanges (FMGenerator clone) {
		new FMGeneratorObserver(this, clone);
	}
	
	private static Waveform getWaveformType (String waveName) {
		Waveform result = null;
		
		if (waveName.equalsIgnoreCase("PHASOR"))
			return Waves.PHASOR;
		if (waveName.equalsIgnoreCase("QUARTERPULSE"))
			return Waves.QUARTERPULSE;
		if (waveName.equalsIgnoreCase("SAW"))
			return Waves.SAW;
		if (waveName.equalsIgnoreCase("SINE"))
			return Waves.SINE;
		if (waveName.equalsIgnoreCase("SQUARE"))
			return Waves.SQUARE;
		if (waveName.equalsIgnoreCase("TRIANGLE"))
			return Waves.TRIANGLE;
		
		return result;
	}

	@Override
	public void close() {
		this.fm = null;
		this.carrierWave = null;
		this.modWave = null;
		this.observers.clear();
		this.observers = null;
	}

}
