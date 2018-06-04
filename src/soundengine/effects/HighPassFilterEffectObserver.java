package soundengine.effects;

public class HighPassFilterEffectObserver  extends EffectObserver {
	public HighPassFilterEffectObserver(Effect original, Effect updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {
		System.out.println("observer " + this + " updating " + updatable + " based on " + original);

		HighPassFilterEffect original = (HighPassFilterEffect) this.original;
		HighPassFilterEffect updatable = (HighPassFilterEffect) this.updatable;
		
		if (original.getCutOffFreq() != updatable.getCutOffFreq())
			updatable.setCutOffFreq(original.getCutOffFreq());
		
		this.forwardUpdatesToUpdatable();
	}
}
