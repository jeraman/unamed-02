package soundengine.effects;

import ddf.minim.effects.*;

public class LowPassFilterEffect extends LowPassFS implements Effect {

	@Deprecated
	public LowPassFilterEffect() {
		this(0, 0);
	}

	public LowPassFilterEffect(float freq, float sampleRate) {
		super(freq, sampleRate);
	}

	public Effect clone() {
		return new LowPassFilterEffect(this.frequency(), this.sampleRate());
	}

}
