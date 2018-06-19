package soundengine.generators;

public class SampleFileGeneratorObserver extends GeneratorObserver {

	public SampleFileGeneratorObserver(AbstractGenerator original, AbstractGenerator updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {		
		System.out.println("observer " + this + " updating " + updatable +  " based on " + original);

		SampleFileGenerator original = (SampleFileGenerator) this.original;
		SampleFileGenerator updatable = (SampleFileGenerator) this.updatable;
		
		if (!original.getFilename().equalsIgnoreCase(updatable.getFilename()))
			updatable.setFilename(original.getFilename());
		
		if (original.getPitch() != updatable.getPitch())
			updatable.setPitch(original.getPitch());
		
		if (original.getVolume() != updatable.getVolume())
			updatable.setVelocity(original.getVolume());

		if (original.getLoopStatus() != updatable.getLoopStatus())
			updatable.setLoopStatus(original.getLoopStatus());
		
		this.forwardUpdatesToUpdatable();
	}

}
