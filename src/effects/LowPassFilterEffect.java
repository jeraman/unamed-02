package effects;

import ddf.minim.effects.*;

public class LowPassFilterEffect extends LowPassFS implements Effect {

	public LowPassFilterEffect(float freq, float sampleRate) {
		super(freq, sampleRate);
	}
	
	public Effect clone() {
		return new LowPassFilterEffect(this.frequency(), this.sampleRate());
	}

}
