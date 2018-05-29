package effects;

import ddf.minim.effects.HighPassSP;

public class HighPassFilterEffect extends HighPassSP implements Effect {

	public HighPassFilterEffect(float freq, float sampleRate) {
		super(freq, sampleRate);
	}
	
	public Effect clone() {
		return new HighPassFilterEffect(this.frequency(), this.sampleRate());
	}

}
