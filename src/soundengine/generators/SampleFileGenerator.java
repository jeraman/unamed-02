package soundengine.generators;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.UGen;
import ddf.minim.ugens.FilePlayer;
import ddf.minim.ugens.Gain;
import ddf.minim.ugens.Sampler;
import ddf.minim.ugens.TickRate;
import javafx.util.Pair;
import soundengine.SoundEngine;
import soundengine.util.Util;

public class SampleFileGenerator extends ModifiedSampler implements Generator,Runnable {
	static final int basePitch	 = 52;
	
	String 	   filename;
	int 	   pitch;
	int 	   volume;
	int 	   duration;
	
	private UGen patched;
	
	private List<SampleFileGeneratorObserver> observers;
	
//	public SamplerFileGenerator(String fileStream) {
//		this(fileStream, Integer.MAX_VALUE);
//	}

	public SampleFileGenerator(String filename) {
		this(filename, basePitch, 127);
	}
	
//	public SamplerFileGenerator(String filename, int pitch, int volume) {
//		this(filename, pitch, volume, Integer.MAX_VALUE, true);
//	}

	public SampleFileGenerator(String filename, int pitch, int volume) {
		this(filename, pitch, volume, true);
	}
	
	public SampleFileGenerator(String filename, int pitch, int volume, boolean shouldLoop) {
		super(filename, 1, SoundEngine.minim);
		
		initializeVariables(filename, pitch, volume, Integer.MIN_VALUE, shouldLoop);
	}
	
	
	@Deprecated
	public SampleFileGenerator() {
		this(new MultiChannelBuffer(0,0), 0, 0, 0);
	}
	
	protected SampleFileGenerator(MultiChannelBuffer sampleData, float sampleRate, int pitch, int volume) {
		this(sampleData, sampleRate, pitch, volume, Integer.MAX_VALUE);
	}

	private SampleFileGenerator(MultiChannelBuffer sampleData, float sampleRate, int pitch, int volume, int duration) {
		this(sampleData, sampleRate, pitch, volume, duration, true);
	}
	
	private SampleFileGenerator(MultiChannelBuffer sampleData, float sampleRate, int pitch, int volume, int duration, boolean shouldLoop) {
		super(sampleData, sampleRate, 1);
		
		initializeVariables("", pitch, volume, duration, shouldLoop);
	}
	
	private void initializeVariables(String filename, int pitch, int volume, int duration, boolean shouldLoop) {
		this.filename = filename;
		//this.filename = filename;
		this.pitch	 = pitch;
		this.volume  = volume;
		//this.shouldLoop  = shouldLoop;
		this.looping = shouldLoop;
		this.duration    = duration;
		
		float pR = calculateRelativePitch(pitch);
		this.setPlaybackRate(pR);
		//this.rateControl = new TickRate(pR);
		//this.gain        = new Gain(Util.mapFromMidiToDecibels(volume));
		this.setVolume(volume);
		
		this.observers = new ArrayList<SampleFileGeneratorObserver>();
		
		this.patched = this;
	}
	
	@Override
	public void patchEffect(UGen effect) {
		this.trigger();
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
		GeneratorFactory.patch(this);
	    this.trigger();
	}
	
	@Override
	public void noteOff() {
		this.stop();
		GeneratorFactory.unpatch(this);
	}
	
	private float calculateRelativePitch(int pitchOffset) {
		float difPit = ((SampleFileGenerator.basePitch - pitchOffset) % 24) * -1;
		return (1.f + (difPit) * (1.f / 12.f));
	}
	
	public void setPitch(int pitch) {
		float pr = calculateRelativePitch(pitch);
		this.setPlaybackRate(pr);
	}
	
	public void setPlaybackRate(float pr) {
		this.rate.setLastValue(pr);
	}

	public void setVolume(int v) {
		float newVel = Util.mapFromMidiToAmplitude(v);
		this.amplitude.setLastValue(newVel);
	}
	
	public void setLoopStatus(boolean l) {
		this.looping = l;
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
		System.out.println("stop playing!");
		this.noteOff();
	}
	

	@Override
	public void attach(GeneratorObserver observer) {
		this.observers.add((SampleFileGeneratorObserver)observer);
	}

	@Override
	public void notifyAllObservers() {
		for (GeneratorObserver observer : observers)
			observer.update();
	}

	@Override
	public Generator clone(int newPitch) {
		return this.clone(newPitch, this.volume);
	}
	
	@Override
	public Generator clone(int newPitch, int newVelocity) {
		SampleFileGenerator clone = new SampleFileGenerator(this.getSampleData(), this.getSampleDataSampleRate(), newPitch, newVelocity, this.duration, this.looping);
		this.linkForFutureChanges(clone);
		return clone;
	}
	
	private void linkForFutureChanges (SampleFileGenerator clone) {
		new SampleFileGeneratorObserver(this, clone);
	}
	
	public void unlinkClonedObservers () {
		for (int i = observers.size(); i >= 0; i--)
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
		this.patched = null;
		this.observers.clear();
		this.observers = null;
	}
}
