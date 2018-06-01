package soundengine.effects;

import ddf.minim.ugens.MoogFilter;

public class MoogFilterEffect extends MoogFilter implements Effect {
	private float frequency;
	private float resonance;
	private String type;

	@Deprecated
	public MoogFilterEffect() {
		this(0, 0, "LP");
	}

	public MoogFilterEffect(float frequencyInHz, float normalizedResonance, String filterType) {
		super(frequencyInHz, normalizedResonance, convertType(filterType));
		this.frequency = frequencyInHz;
		this.resonance = normalizedResonance;
		this.type = filterType;
	}

	@Override
	public Effect clone() {
		return new MoogFilterEffect(this.frequency, this.resonance, this.type);
	}

	private static Type convertType(String type) {
		Type result = null;
		if (type.equalsIgnoreCase("LP"))
			result = Type.LP;
		if (type.equalsIgnoreCase("HP"))
			result = Type.HP;
		if (type.equalsIgnoreCase("BP"))
			result = Type.BP;

		return result;
	}

}
