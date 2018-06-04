package soundengine.effects;

public class DelayEffectObserver extends EffectObserver {
	public DelayEffectObserver(Effect original, Effect updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {
		System.out.println("observer " + this + " updating " + updatable + " based on " + original);

		DelayEffect original = (DelayEffect) this.original;
		DelayEffect updatable = (DelayEffect) this.updatable;
		
		if (original.getMaxDelayTime() != updatable.getMaxDelayTime())
			updatable.setMaxDelayTime(original.getMaxDelayTime());
		if (original.getAmplitudeFactor() != updatable.getAmplitudeFactor())
			updatable.setAmplitudeFactor(original.getAmplitudeFactor());
		
		this.forwardUpdatesToUpdatable();
	}
}
