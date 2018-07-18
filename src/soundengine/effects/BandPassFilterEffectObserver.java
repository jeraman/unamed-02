package soundengine.effects;

public class BandPassFilterEffectObserver extends EffectObserver {
	public BandPassFilterEffectObserver(AbstractEffect original, AbstractEffect updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {
//		System.out.println("observer " + this + " updating " + updatable +  " based on " + original);
		
		BandPassFilterEffect original = (BandPassFilterEffect) this.original;
		BandPassFilterEffect updatable = (BandPassFilterEffect) this.updatable;
		
		if (original.getCenterFreq() != updatable.getCenterFreq())
			updatable.setCenterFreq(original.getCenterFreq());
		if (original.getBandWidth() != updatable.getBandWidth())
			updatable.setBandWidth(original.getBandWidth());
		
		this.forwardUpdatesToUpdatable();
	}
}
