package soundengine.effects;

public class FlangerEffectObserver extends EffectObserver {
	public FlangerEffectObserver(Effect original, Effect updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {
		System.out.println("observer " + this + " updating " + updatable + " based on " + original);

		FlangerEffect original = (FlangerEffect) this.original;
		FlangerEffect updatable = (FlangerEffect) this.updatable;
		
		if (original.getDelayLength() != updatable.getDelayLength())
			updatable.setDelayLength(original.getDelayLength());
		
		if (original.getLfoRate() != updatable.getLfoRate())
			updatable.setLfoRate(original.getLfoRate());
		
		if (original.getDelayDepth() != updatable.getDelayDepth())
			updatable.setDelayDepth(original.getDelayDepth());
		
		if (original.getFeedbackAmplitude() != updatable.getFeedbackAmplitude())
			updatable.setFeedbackAmplitude(original.getFeedbackAmplitude());
		
		if (original.getDryAmplitude() != updatable.getDryAmplitude())
			updatable.setDryAmplitude(original.getDryAmplitude());
		
		if (original.getWetAmplitude() != updatable.getWetAmplitude())
			updatable.setWetAmplitude(original.getWetAmplitude());
		
		this.forwardUpdatesToUpdatable();
	}
}

