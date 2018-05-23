package generators;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.UGen;
import ddf.minim.ugens.FilePlayer;
import ddf.minim.ugens.Gain;
import ddf.minim.ugens.TickRate;
import musicalTasksTest.Generator;
import musicalTasksTest.GeneratorFactory;
import musicalTasksTest.Util;

public class AudioFileGenerator extends FilePlayer implements Generator,Runnable {
	static final int basePitch	 = 52;
	
	TickRate   rateControl;
	Gain       gain;
	boolean    shouldLoop;
	int 	   duration;
	
	public AudioFileGenerator(Minim minim, String filename) {
		this(minim, filename, Integer.MAX_VALUE);
	}

	public AudioFileGenerator(Minim minim, String filename, int duration) {
		this(minim, filename, 60, 127, duration, true);
	}
	
	public AudioFileGenerator(Minim minim, String filename, int pitch, int volume) {
		this(minim, filename, pitch, volume, Integer.MAX_VALUE, true);
	}

	public AudioFileGenerator(Minim minim, String filename, int pitch, int volume, int duration) {
		this(minim, filename, pitch, volume, duration, true);
	}
	
	public AudioFileGenerator(Minim minim, String filename, int pitch, int volume, int duration, boolean shouldLoop) {
		super(minim.loadFileStream(filename));

		float pR = calculateRelativePitch(pitch);
		this.rateControl = new TickRate(pR);
	    this.gain        = new Gain(Util.mapFromMidiToDecibels(volume));
	    this.shouldLoop  = shouldLoop;
	    this.duration    = duration;
	}
	
	public UGen patchEffect (UGen nextGen) {
		return super.patch(rateControl).patch(gain).patch(nextGen);
	}
	
	public void patchOutput (AudioOutput out) {
		super.patch(rateControl).patch(gain).patch(out);
	}
	
	public void unpatchEffect (UGen nextGen) {
		gain.unpatch(nextGen);
		rateControl.unpatch(gain);
		super.unpatch(rateControl);
		super.close();
	}
	
	public void unpatchOutput (AudioOutput out) {
		gain.unpatch(out);
		rateControl.unpatch(gain);
		super.unpatch(rateControl);
		super.close();
	}

	@Override
	public void noteOn() {
	    this.play();
	}
	
	@Override
	public void noteOff() {
		this.stop();
		this.close();
	}
	
	private float calculateRelativePitch(int pitchOffset) {
		float difPit = ((AudioFileGenerator.basePitch - pitchOffset) % 24) * -1;
		return (1.f + (difPit) * (1.f / 12.f));
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
		super.rewind();
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
