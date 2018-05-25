package generators;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.UGen;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.ugens.FilePlayer;
import ddf.minim.ugens.Gain;
import ddf.minim.ugens.Sampler;
import ddf.minim.ugens.TickRate;
import util.Util;

public class AudioFileGenerator extends FilePlayer implements Generator,Runnable {
	static final int basePitch	 = 52;
	
	TickRate   rateControl;
	Gain       gain;
	
	AudioRecordingStream fileStream;
	
	//String 	   filename;
	int 	   pitch;
	int 	   volume;
	int 	   duration;
	boolean    shouldLoop;
	
	public AudioFileGenerator(AudioRecordingStream fileStream) {
		this(fileStream, Integer.MAX_VALUE);
	}

	public AudioFileGenerator(AudioRecordingStream fileStream, int duration) {
		this(fileStream, basePitch, 127, duration, true);
	}
	
	public AudioFileGenerator(AudioRecordingStream fileStream, int pitch, int volume) {
		this(fileStream, pitch, volume, Integer.MAX_VALUE, true);
	}

	public AudioFileGenerator(AudioRecordingStream fileStream, int pitch, int volume, int duration) {
		this(fileStream, pitch, volume, duration, true);
	}
	
	public AudioFileGenerator(AudioRecordingStream fileStream, int pitch, int volume, int duration, boolean shouldLoop) {
		super(fileStream);
		this.fileStream = fileStream;
		//this.filename = filename;
		this.pitch	 = pitch;
		this.volume  = volume;
		this.shouldLoop  = shouldLoop;
		this.duration    = duration;
		
		float pR = calculateRelativePitch(pitch);
		this.rateControl = new TickRate(pR);
	    this.gain        = new Gain(Util.mapFromMidiToDecibels(volume));
	}
	
	public UGen patchEffect (UGen nextGen) {
		return super.patch(rateControl).patch(gain).patch(nextGen);
	}
	
	public void patchOutput (AudioOutput out) {
		super.patch(rateControl).patch(gain).patch(out);
	}
	
	public void close() {
		super.close();
		//super.buffer = null;
		gain = null;
		rateControl = null;
		fileStream = null;
	}
	
	public void unpatchEffect (UGen nextGen) {
		gain.unpatch(nextGen);
		rateControl.unpatch(gain);
		super.unpatch(rateControl);
	}
	
	public void unpatchOutput (AudioOutput out) {
		gain.unpatch(out);
		rateControl.unpatch(gain);
		super.unpatch(rateControl);
	}

	@Override
	public void noteOn() {
		GeneratorFactory.patch(this);
	    this.play();
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
		rateControl.value.setLastValue(pr);
	}

	public void setVolume(int v) {
		float newVel = Util.mapFromMidiToDecibels(v);
		gain.setValue(newVel);
	}
	
	public void setLoopStatus(boolean l) {
		this.shouldLoop = l;
	}
	
	public void play() {
		if (this.shouldLoop)
			playInLoop();
		else
			playOnce();
	}

	private void playInLoop() {
		this.loop();
	}

	private void playOnce() {
		this.loop(0);
	}

	public void stop() {
		super.pause();
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
		AudioFileGenerator result = new AudioFileGenerator(this.fileStream, newPitch, this.volume, this.duration, this.shouldLoop);
		return result;
	}
}
