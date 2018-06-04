package soundengine.effects;

public class LowPassFilterEffectObserver extends EffectObserver {
	public LowPassFilterEffectObserver(Effect original, Effect updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {
		System.out.println("observer " + this + " updating " + updatable + " based on " + original);

		LowPassFilterEffect original = (LowPassFilterEffect) this.original;
		LowPassFilterEffect updatable = (LowPassFilterEffect) this.updatable;
		
		if (original.getCutOffFreq() != updatable.getCutOffFreq())
			updatable.setCutOffFreq(original.getCutOffFreq());
		
		this.forwardUpdatesToUpdatable();
	}
}

