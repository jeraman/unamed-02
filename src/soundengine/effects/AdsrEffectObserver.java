package soundengine.effects;

public class AdsrEffectObserver extends EffectObserver {

	public AdsrEffectObserver(AbstractEffect original, AbstractEffect updatable) {
		super(original, updatable);
	}
	
	public void noteOffObservers () {
		((AdsrEffect)updatable).noteOff();
		((AdsrEffect)updatable).noteOffObservers();
	}

	@Override
	public void update() {
//		System.out.println("observer " + this + " updating " + updatable + " based on " + original);

		AdsrEffect original = (AdsrEffect) this.original;
		AdsrEffect updatable = (AdsrEffect) this.updatable;

		if (original.getMaxAmp() != updatable.getMaxAmp() || original.getAttTime() != updatable.getAttTime()
				|| original.getDecTime() != updatable.getDecTime() || original.getSusLvl() != updatable.getSusLvl()
				|| original.getRelTime() != updatable.getRelTime() || original.getBefAmp() != updatable.getBefAmp()
				|| original.getAftAmp() != updatable.getAftAmp())

			updatable.setParameters(original.getMaxAmp(), original.getAttTime(), original.getDecTime(),
									original.getSusLvl(), original.getRelTime(), original.getBefAmp(), original.getAftAmp());

		this.forwardUpdatesToUpdatable();
	}

}
