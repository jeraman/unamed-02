package effects;

import ddf.minim.ugens.BitCrush;
import generators.GeneratorFactory;

public class BitChrushEffect extends BitCrush implements Effect {

	private int bitResolution;
	
	public BitChrushEffect (int bitRes) {
		super(bitRes, GeneratorFactory.out.sampleRate());
		this.bitResolution = bitRes;
	}
	
	public Effect clone() {
		return new BitChrushEffect(bitResolution);
	}
}
