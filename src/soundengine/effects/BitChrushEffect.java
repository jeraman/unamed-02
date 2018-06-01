package soundengine.effects;

import ddf.minim.ugens.BitCrush;
import soundengine.SoundEngine;

public class BitChrushEffect extends BitCrush implements Effect {

	private int bitResolution;
	private float sampleRate;
	
	@Deprecated
	public BitChrushEffect() {
		this(0,0);
	}
	
	public BitChrushEffect (int bitRes, float sampleRate) {
		super(bitRes, sampleRate);
		this.bitResolution = bitRes;
		this.sampleRate = sampleRate;
	}
	
	public Effect clone() {
		return new BitChrushEffect(bitResolution, sampleRate);
	}
}
