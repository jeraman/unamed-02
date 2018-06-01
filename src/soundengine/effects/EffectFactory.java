package soundengine.effects;

import soundengine.SoundEngine;

/**
 * Singleton class used to create custom effects
 * 
 * @author jeraman.info
 *
 */
public class EffectFactory {

	// this class is a singleton
	private EffectFactory() {
	}

	public static Effect createEffect(String type, String[] parameters) {
		Effect fx = null;

		if (type.equalsIgnoreCase("ADSR"))
			fx = createAdrs(parameters);
		if (type.equalsIgnoreCase("BANDPASS"))
			fx = createBandPass(parameters);
		if (type.equalsIgnoreCase("BITCHRUSH"))
			fx = createBitChrush(parameters);
		if (type.equalsIgnoreCase("DELAY"))
			fx = createDelay(parameters);
		if (type.equalsIgnoreCase("FLANGER"))
			fx = createFlanger(parameters);
		if (type.equalsIgnoreCase("HIGHPASS"))
			fx = createHighPass(parameters);
		if (type.equalsIgnoreCase("LOWPASS"))
			fx = createLowPass(parameters);
		if (type.equalsIgnoreCase("MOOGFILTER"))
			fx = createMoogFilter(parameters);

		return fx;
	}

	// adsr
	public static Effect createAdrs(String[] parameters) {
		float maxAmp = Float.parseFloat(parameters[0]);
		float attTime = Float.parseFloat(parameters[1]);
		float decTime = Float.parseFloat(parameters[2]);
		float susLvl = Float.parseFloat(parameters[3]);
		float relTime = Float.parseFloat(parameters[4]);
		float befAmp = Float.parseFloat(parameters[5]);
		float aftAmp = Float.parseFloat(parameters[6]);
		return createAdrs(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}

	public static Effect createAdrs(float maxAmp, float attTime, float decTime, float susLvl, float relTime,
			float befAmp, float aftAmp) {
		return new AdsrEffect(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}

	// bandpass filter
	public static Effect createBandPass(String[] parameters) {
		float centerFreq = Float.parseFloat(parameters[0]);
		float bandWidth = Float.parseFloat(parameters[1]);
		// float sampleRate = Float.parseFloat(parameters[2]);
		float sampleRate = SoundEngine.out.sampleRate();

		return createBandPass(centerFreq, bandWidth, sampleRate);
	}

	public static Effect createBandPass(float centerFreq, float bandWidth, float sampleRate) {
		return new BandPassFilterEffect(centerFreq, bandWidth, sampleRate);
	}

	// bitchrush
	public static Effect createBitChrush(String[] parameters) {
		int bitResolution = Integer.parseInt(parameters[0]);
		// float sampleRate = Float.parseFloat(parameters[1]);
		float sampleRate = SoundEngine.out.sampleRate();

		return createBitChrush(bitResolution, sampleRate);
	}

	public static Effect createBitChrush(int bitResolution, float sampleRate) {
		return new BitChrushEffect(bitResolution, sampleRate);
	}

	// delay
	public static Effect createDelay(String[] parameters) {
		float maxDelayTime = Float.parseFloat(parameters[0]);
		float amplitudeFactor = Float.parseFloat(parameters[1]);
		boolean feedBackOn = Boolean.parseBoolean(parameters[2]);
		boolean passAudioOn = Boolean.parseBoolean(parameters[3]);

		return createDelay(maxDelayTime, amplitudeFactor, feedBackOn, passAudioOn);
	}

	public static Effect createDelay(float maxDelayTime, float amplitudeFactor, boolean feedBackOn,
			boolean passAudioOn) {
		return new DelayEffect(maxDelayTime, amplitudeFactor, feedBackOn, passAudioOn);
	}

	// flanger
	public static Effect createFlanger(String[] parameters) {
		float delayLength = Float.parseFloat(parameters[0]);
		float lfoRate = Float.parseFloat(parameters[1]);
		float delayDepth = Float.parseFloat(parameters[2]);
		float feedbackAmplitude = Float.parseFloat(parameters[3]);
		float dryAmplitude = Float.parseFloat(parameters[4]);
		float wetAmplitude = Float.parseFloat(parameters[5]);

		return createFlanger(delayLength, lfoRate, delayDepth, feedbackAmplitude, dryAmplitude, wetAmplitude);
	}

	public static Effect createFlanger(float delayLength, float lfoRate, float delayDepth, float feedbackAmplitude,
			float dryAmplitude, float wetAmplitude) {
		return new FlangerEffect(delayLength, lfoRate, delayDepth, feedbackAmplitude, dryAmplitude, wetAmplitude);
	}

	// highpass filter
	public static Effect createHighPass(String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		//float sampleRate = Float.parseFloat(parameters[1]);
		float sampleRate = SoundEngine.out.sampleRate();
		return createHighPass(freq, sampleRate);
	}

	public static Effect createHighPass(float freq, float sampleRate) {
		return new HighPassFilterEffect(freq, sampleRate);
	}

	// lowpass filter
	public static Effect createLowPass(String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		// float sampleRate = Float.parseFloat(parameters[1]);
		float sampleRate = SoundEngine.out.sampleRate();

		return createLowPass(freq, sampleRate);
	}

	public static Effect createLowPass(float freq, float sampleRate) {
		return new LowPassFilterEffect(freq, sampleRate);
	}

	// moog filter
	public static Effect createMoogFilter(String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		String type = parameters[1];
		// float sampleRate = Float.parseFloat(parameters[2]);

		float sampleRate = SoundEngine.out.sampleRate();

		return createMoogFilter(freq, sampleRate, type);
	}

	public static Effect createMoogFilter(float freq, float sampleRate, String type) {
		return new MoogFilterEffect(freq, sampleRate, type);
	}

}
