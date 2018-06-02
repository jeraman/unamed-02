package soundengine.generators;

public class FMGeneratorObserver extends GeneratorObserver {

	public FMGeneratorObserver(FMGenerator original, FMGenerator updatable) {
		super(original, updatable);
	}

	
	@Override
	public void update() {
		System.out.println("observer " + this + " updating " + updatable +  " based on " + original);
		// TODO: check if any of these features has changed and update accordingly.
		// float carrierFreq;
		// float carrierAmp;
		// String carrierWave;
		// float modFreq;
		// float modAmp;
		// String modWave;
	}

}
