package effects;

import ddf.minim.ugens.MoogFilter;

public class MoogFilterEffect extends MoogFilter implements Effect{
	private float frequency;
	private float resonance;
	private Type type;
	
	public MoogFilterEffect(float frequencyInHz, float normalizedResonance, Type filterType ) {
		super(frequencyInHz, normalizedResonance, filterType);
		this.frequency = frequencyInHz;
		this.resonance = normalizedResonance;
		this.type = filterType;
	}
	
	@Override
	public Effect clone() {
		return new MoogFilterEffect(this.frequency, this.resonance, this.type);
	}

}
