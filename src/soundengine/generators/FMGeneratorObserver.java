package soundengine.generators;

public class FMGeneratorObserver extends GeneratorObserver {

	public FMGeneratorObserver(FMGenerator original, FMGenerator updatable) {
		super(original, updatable);
	}

	
	@Override
	public void update() {
//		System.out.println("observer " + this + " updating " + updatable +  " based on " + original);

		FMGenerator original = (FMGenerator) this.original;
		FMGenerator updatable = (FMGenerator) this.updatable;
		
		if (original.getCarrierFreq() != updatable.getCarrierFreq())
			updatable.setCarrierFreq(original.getCarrierFreq());
		
		if (original.getCarrierAmp() != updatable.getCarrierAmp())
			updatable.setCarrierAmp(original.getCarrierAmp());
		
		if (!original.getCarrierWaveString().equals(updatable.getCarrierWaveString()))
			updatable.setCarrierWave(original.getCarrierWaveString());

		if (original.getModFreq() != updatable.getModFreq())
			updatable.setModFreq(original.getModFreq());
		
		if (original.getModAmp() != updatable.getModAmp())
			updatable.setModAmp(original.getModAmp());
		
		if (!original.getModWaveString().equals(updatable.getModWaveString()))
			updatable.setModWave(original.getModWaveString());
		
		if (original.getDuration() != updatable.getDuration())
			updatable.setDuration(original.getDuration());
		
//		System.out.println("Observser " + this +  " in action!");
//		System.out.println("Original " + original);
//		System.out.println("Updatable " + updatable);
//		System.out.println("Forwarding changes to children...");
		
		this.forwardUpdatesToUpdatable();
	}

}
