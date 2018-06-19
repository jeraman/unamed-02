package soundengine.generators;

public class LiveInputGeneratorObserver extends GeneratorObserver {

	public LiveInputGeneratorObserver(AbstractGenerator original, AbstractGenerator updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {
		LiveInputGenerator original = (LiveInputGenerator) this.original;
		LiveInputGenerator updatable = (LiveInputGenerator) this.updatable;
		
		if (original.getPitch() != updatable.getPitch())
			updatable.setPitch(original.getPitch());
		
		if (original.getVelocity() != updatable.getVelocity())
			updatable.setVelocity(original.getVelocity());
		
		if (original.getDuration() != updatable.getDuration())
			updatable.setDuration(original.getDuration());
		
		this.forwardUpdatesToUpdatable();
	}

}
