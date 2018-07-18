package soundengine.effects;

public class BitChrushEffectObserver extends EffectObserver {
	public BitChrushEffectObserver(AbstractEffect original, AbstractEffect updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {
//		System.out.println("observer " + this + " updating " + updatable + " based on " + original);

		BitChrushEffect original = (BitChrushEffect) this.original;
		BitChrushEffect updatable = (BitChrushEffect) this.updatable;
		
		if (original.getBitResolution() != updatable.getBitResolution())
			updatable.setBitResolution(original.getBitResolution());
		
		this.forwardUpdatesToUpdatable();
	}
}
