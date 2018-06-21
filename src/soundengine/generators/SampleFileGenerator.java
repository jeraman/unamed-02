package soundengine.generators;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.AudioOutput;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.UGen;
import javafx.util.Pair;
import soundengine.SoundEngine;
import soundengine.util.Util;

public class SampleFileGenerator extends ModifiedSampler implements AbstractGenerator,Runnable {
	static final int basePitch	 = 52;
	
	String 	   filename;
	int 	   pitch;
	int 	   velocity;
	int 	   duration;
	
	private UGen patched;
	
	private List<SampleFileGeneratorObserver> observers;
	
//	public SamplerFileGenerator(String fileStream) {
//		this(fileStream, Integer.MAX_VALUE);
//	}

	@Deprecated
	public SampleFileGenerator(String filename) {
		this(filename, basePitch, 127);
	}
	
//	public SamplerFileGenerator(String filename, int pitch, int volume) {
//		this(filename, pitch, volume, Integer.MAX_VALUE, true);
//	}

	@Deprecated
	public SampleFileGenerator(String filename, int pitch, int volume) {
		this(filename, pitch, volume, true, -1);
	}
	
	public SampleFileGenerator(String filename, int pitch, int volume, boolean shouldLoop, int duration) {
		super(filename, 1, SoundEngine.minim);
		
		initializeVariables(filename, pitch, volume, shouldLoop, duration);
	}
	
	
	@Deprecated
	public SampleFileGenerator() {
		this(new MultiChannelBuffer(0,0), 0, 0, 0);
	}
	
	@Deprecated
	protected SampleFileGenerator(MultiChannelBuffer sampleData, float sampleRate, int pitch, int volume) {
		this(sampleData, sampleRate, pitch, volume, Integer.MAX_VALUE);
	}

	private SampleFileGenerator(MultiChannelBuffer sampleData, float sampleRate, int pitch, int volume, int duration) {
		this(sampleData, sampleRate, pitch, volume, true, duration);
	}
	
	private SampleFileGenerator(MultiChannelBuffer sampleData, float sampleRate, int pitch, int volume, boolean shouldLoop, int duration) {
		super(sampleData, sampleRate, 1);
		
		initializeVariables("", pitch, volume, shouldLoop, duration);
	}
	
	private void initializeVariables(String filename, int pitch, int velocity, boolean shouldLoop, int duration) {
		this.filename = filename;
		//this.filename = filename;
		this.pitch	 = pitch;
		this.velocity  = velocity;
		//this.shouldLoop  = shouldLoop;
		this.looping = shouldLoop;
		
		float pR = calculateRelativePitch(pitch);
		this.setPlaybackRate(pR);
		//this.rateControl = new TickRate(pR);
		//this.gain        = new Gain(Util.mapFromMidiToDecibels(volume));
		this.setVelocity(velocity);
		
		this.observers = new ArrayList<SampleFileGeneratorObserver>();
		
		this.patched = this;
		
		this.duration = duration;
		
		if (this.shouldNoteOffWithDuration())
			this.noteOffAfterDuration(this.duration);
	}
	
	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.trim().split(":");
		
		if (parts[0].trim().equalsIgnoreCase("filename"))
			this.setFilename(parts[1].trim());
		if (parts[0].trim().equalsIgnoreCase("pitch"))
			this.setPitch(Integer.parseInt(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("velocity"))
			this.setVelocity(Integer.parseInt(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("duration"))
			this.setDuration((int)Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("loop"))
			this.setLoopStatus(Boolean.parseBoolean(parts[1].trim()));
	}
	
	
	public String getFilename() {
		return this.filename;
	}
	
	
	public void setFilename(String filename) {
		//if name hasn't changed, give up1
		if(this.filename.equalsIgnoreCase(filename))
			return;
		
		//else
		this.stop();
		this.filename = filename;
		Pair<MultiChannelBuffer, Float> pair = GeneratorFactory.loadMultiChannelBufferFromFile(filename);
		this.setSample(pair.getKey(), pair.getValue());
		this.trigger();
	}
	
	protected int getDuration() {
		return duration;
	}

	protected void setDuration(int duration) {
		this.duration = duration;
	}
	
	protected boolean shouldNoteOffWithDuration() {
		return this.duration > 0;
	}
	
	public int getPitch() {
		return this.pitch;
	}
	
	public void setPitch(int pitch) {
		this.pitch = pitch;
		float pr = calculateRelativePitch(pitch);
		this.setPlaybackRate(pr);
	}

	private float calculateRelativePitch(int pitchOffset) {
		float difPit = Math.abs(((SampleFileGenerator.basePitch - pitchOffset) % 24) * -1);
		return (1.f + (difPit) * (1.f / 12.f));
	}
	
	public void setPlaybackRate(float pr) {
		this.rate.setLastValue(pr);
	}

	public int getVolume() {
		return this.velocity;
	}
	
	public void setVelocity(int v) {
		this.velocity = v;
		float newVel = Util.mapFromMidiToAmplitude(velocity);
		this.amplitude.setLastValue(newVel);
	}
	
	public boolean getLoopStatus() {
		return this.looping;
	}
	
	public void setLoopStatus(boolean l) {
		this.looping = l;
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
		if (!this.isClosed()) {
			this.stop();
			GeneratorFactory.unpatch(this);
		}
	}
	
	public void mute() {
		if (!this.isClosed()) {
			this.setVelocity(0);
			this.setPitch(0);
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
		System.out.println("stop playing!");
//		this.noteOff();
		this.mute();
	}
	

	@Override
	public synchronized void attach(GeneratorObserver observer) {
		this.observers.add((SampleFileGeneratorObserver)observer);
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
	
	public AbstractGenerator cloneWithExactPitchAndVelocity(int newPitch, int newVelocity) {
		return clone(newPitch, newVelocity, this.duration);
	}

	@Override
	public AbstractGenerator cloneWithPitch(int newPitch) {
		return this.cloneWithPitchAndVelocity(newPitch, this.velocity);
	}

	@Override
	public AbstractGenerator cloneWithPitchAndVelocity(int newPitch, int newVelocity) {
		// return this.clone(newPitch, newVelocity);
		return cloneWithPitchAndVelocityIfUnlocked(newPitch, newVelocity);
	}

	public AbstractGenerator clone(int newPitch, int newVelocity, int newDuration) {
		SampleFileGenerator clone = new SampleFileGenerator(this.getSampleData(), this.getSampleDataSampleRate(), newPitch, newVelocity, this.looping, newDuration);
		this.linkForFutureChanges(clone);
		return clone;
	}
	
	private void linkForFutureChanges (SampleFileGenerator clone) {
		new SampleFileGeneratorObserver(this, clone);
	}
	
	public synchronized void unlinkOldObservers () {
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
		this.patched = null;
		this.observers.clear();
		this.observers = null;
	}
}
