package soundengine.generators;

public class SampleFileGeneratorObserver extends GeneratorObserver {

	public SampleFileGeneratorObserver(Generator original, Generator updatable) {
		super(original, updatable);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
		
		
		this.forwardUpdatesToUpdatable();
	}

}
