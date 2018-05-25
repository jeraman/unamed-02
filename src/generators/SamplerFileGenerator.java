package generators;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.UGen;
import ddf.minim.ugens.FilePlayer;
import ddf.minim.ugens.Gain;
import ddf.minim.ugens.Sampler;
import ddf.minim.ugens.TickRate;
import util.Util;

public class SamplerFileGenerator extends Sampler implements Generator,Runnable {
	static final int basePitch	 = 52;
	
	String 	   filename;
	int 	   pitch;
	int 	   volume;
	int 	   duration;
	
	public SamplerFileGenerator(String fileStream) {
		this(fileStream, Integer.MAX_VALUE);
	}

	public SamplerFileGenerator(String filename, int duration) {
		this(filename, basePitch, 127, duration, true);
	}
	
	public SamplerFileGenerator(String filename, int pitch, int volume) {
		this(filename, pitch, volume, Integer.MAX_VALUE, true);
	}

	public SamplerFileGenerator(String filename, int pitch, int volume, int duration) {
		this(filename, pitch, volume, duration, true);
	}
	
	public SamplerFileGenerator(String filename, int pitch, int volume, int duration, boolean shouldLoop) {
		super(filename, 1, GeneratorFactory.minim);
		
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
	}
	
	
	
	public UGen patchEffect (UGen nextGen) {
		//return super.patch(rateControl).patch(gain).patch(nextGen);
//		return super.patch(gain).patch(nextGen);
		return super.patch(nextGen);
	}
	
	public void patchOutput (AudioOutput out) {
//		super.patch(rateControl).patch(gain).patch(out);
//		super.patch(gain).patch(out);
		super.patch(out);
	}
	
	public void close() {
//		super.close();
		//gain = null;
		//rateControl = null;
	}
	
	public void unpatchEffect (UGen nextGen) {
		//gain.unpatch(nextGen);
		//rateControl.unpatch(gain);
//		super.unpatch(rateControl);
		super.unpatch(nextGen);
	}
	
	public void unpatchOutput (AudioOutput out) {
//		gain.unpatch(out);
//		rateControl.unpatch(gain);
//		super.unpatch(rateControl);
//		super.unpatch(gain);
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
		float difPit = ((AudioFileGenerator.basePitch - pitchOffset) % 24) * -1;
		return (1.f + (difPit) * (1.f / 12.f));
	}
	
	public void setPitch(int pitch) {
		float pr = calculateRelativePitch(pitch);
		this.setPlaybackRate(pr);
	}
	
	public void setPlaybackRate(float pr) {
//		rateControl.value.setLastValue(pr);
		this.rate.setLastValue(pr);
	}

	public void setVolume(int v) {
		float newVel = Util.mapFromMidiToAmplitude(v);
		//gain.setValue(newVel);
		this.amplitude.setLastValue(newVel);
	}
	
	public void setLoopStatus(boolean l) {
		this.looping = l;
	}
	
//	public void play() {
//		if (this.shouldLoop)
//			playInLoop();
//		else
//			playOnce();
//	}
//
//	private void playInLoop() {
//		this.loop();
//	}
//
//	private void playOnce() {
//		this.loop(0);
//	}
//
//	public void stop() {
//		super.pause();
//	}
//	
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
		SamplerFileGenerator result = new SamplerFileGenerator(this.filename, newPitch, this.volume, this.duration, this.looping);
		return result;
	}
}
