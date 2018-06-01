package soundengine.effects;

import ddf.minim.ugens.Flanger;

public class FlangerEffect extends Flanger implements Effect {

	/**
	 * delay length in milliseconds ( clamped to [0,100] )
	 */
	private float delayLength;
	
	/**
	 *  lfo rate in Hz ( clamped at low end to 0.001 )
	 */
	private float lfoRate;
	
	/**
	 * delay depth in milliseconds ( minimum of 0 )
	 */
	private float delayDepth;
	
	/**
	 * amount of feedback ( clamped to [0,1] )
	 */
	private float feedbackAmplitude;
	
	/**
	 * amount of dry signal ( clamped to [0,1] )
	 */
	private float dryAmplitude;
	
	/**
	 * amount of wet signal ( clamped to [0,1] )
	 */
	private float wetAmplitude; 
	
	
	public FlangerEffect() {
		this(0, 0, 0, 0, 0, 0);
	}
	
	public FlangerEffect(float delayLength, float lfoRate, float delayDepth, float feedbackAmplitude,
			float dryAmplitude, float wetAmplitude) {
		super(delayLength, lfoRate, delayDepth, feedbackAmplitude, dryAmplitude, wetAmplitude);
		
		this.delayLength = delayLength;
		this.lfoRate = lfoRate;
		this.delayDepth = delayDepth;
		this.feedbackAmplitude = feedbackAmplitude;
		this.dryAmplitude = dryAmplitude;
		this.wetAmplitude = wetAmplitude;
	}
	
	public Effect clone () {
		return new FlangerEffect(delayLength, lfoRate, delayDepth, feedbackAmplitude, dryAmplitude, wetAmplitude);
	}

}
