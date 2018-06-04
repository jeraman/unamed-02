package soundengine.effects;

public class MoogFilterEffectObserver extends EffectObserver {
	public MoogFilterEffectObserver(Effect original, Effect updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {
		System.out.println("observer " + this + " updating " + updatable + " based on " + original);

		MoogFilterEffect original = (MoogFilterEffect) this.original;
		MoogFilterEffect updatable = (MoogFilterEffect) this.updatable;
		
		if (original.getFrequency() != updatable.getFrequency())
			updatable.setFrequency(original.getFrequency());
		
		if (original.getResonance() != updatable.getResonance())
			updatable.setResonance(original.getResonance());

		if (!original.getType().equalsIgnoreCase(updatable.getType()))
			updatable.setType(original.getType());
		
		this.forwardUpdatesToUpdatable();
	}
}
