package soundengine.generators;

public class OscillatorGeneratorObserver extends GeneratorObserver {

	public OscillatorGeneratorObserver(OscillatorGenerator original, OscillatorGenerator updatable) {
		super(original, updatable);
	}

	@Override
	public void update() {
		OscillatorGenerator original = (OscillatorGenerator) this.original;
		OscillatorGenerator updatable = (OscillatorGenerator) this.updatable;
		
		if (original.getFrequency() != updatable.getFrequency())
			updatable.setFrequency(original.getFrequency());
		
		if (original.getAmplitude() != updatable.getAmplitude())
			updatable.setAmplitude(original.getAmplitude());
		
		if (original.getDuration() != updatable.getDuration())
			updatable.setDuration(original.getDuration());
		
		if (!original.getWaveformString().equals(updatable.getWaveformString()))
			updatable.setWaveform(original.getWaveformString());
		
		this.forwardUpdatesToUpdatable();
	}

}
