package effects;

import ddf.minim.effects.BandPass;

public class BandPassFilterEffect extends BandPass implements Effect {

	public BandPassFilterEffect(float centerFreq, float bandWidth, float sampleRate) {
		super(centerFreq, bandWidth, sampleRate);
	}
	
	public Effect clone() {
		return new BandPassFilterEffect(this.frequency(), this.getBandWidth(), this.sampleRate());
	}

}
