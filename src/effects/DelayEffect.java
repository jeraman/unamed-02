package effects;

import ddf.minim.ugens.Delay;

public class DelayEffect extends Delay implements Effect{
	private float maxDelayTime;
	private float amplitudeFactor;
	private boolean feedBackOn;
	private boolean passAudioOn;
	
	public DelayEffect(float maxDelayTime, float amplitudeFactor, boolean feedBackOn, boolean passAudioOn) {
		super(maxDelayTime, amplitudeFactor, feedBackOn, passAudioOn);
		this.maxDelayTime = maxDelayTime;
		this.amplitudeFactor = amplitudeFactor;
		this.feedBackOn = feedBackOn;
		this.passAudioOn = passAudioOn;
	}
	
	@Override
	public Effect clone() {
		return new DelayEffect(this.maxDelayTime, this.amplitudeFactor, this.feedBackOn, this.passAudioOn);
	}

}
