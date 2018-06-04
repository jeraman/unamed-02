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
	
	public static void updateEffect(Effect fx, String[] parameters) {
		if (fx instanceof AdsrEffect)
			updateAdrs((AdsrEffect)fx, parameters);
		if (fx instanceof BandPassFilterEffect)
			updateBandPass((BandPassFilterEffect)fx, parameters);
		if (fx instanceof BitChrushEffect)
			updateBitChrush((BitChrushEffect)fx, parameters);
		if (fx instanceof DelayEffect)
			updateDelay((DelayEffect)fx, parameters);
		if (fx instanceof FlangerEffect)
			updateFlanger((FlangerEffect)fx, parameters);
		if (fx instanceof HighPassFilterEffect)
			updateHighPass((HighPassFilterEffect)fx, parameters);
		if (fx instanceof LowPassFilterEffect)
			updateLowPass((LowPassFilterEffect)fx, parameters);
		if (fx instanceof MoogFilterEffect)
			updateMoogFilter((MoogFilterEffect)fx, parameters);
	}


	// adsr
	private static Effect createAdrs(String[] parameters) {
		float maxAmp = Float.parseFloat(parameters[0]);
		float attTime = Float.parseFloat(parameters[1]);
		float decTime = Float.parseFloat(parameters[2]);
		float susLvl = Float.parseFloat(parameters[3]);
		float relTime = Float.parseFloat(parameters[4]);
		float befAmp = Float.parseFloat(parameters[5]);
		float aftAmp = Float.parseFloat(parameters[6]);
		return createAdrs(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}

	private static Effect createAdrs(float maxAmp, float attTime, float decTime, float susLvl, float relTime,
			float befAmp, float aftAmp) {
		return new AdsrEffect(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}
	
	private static void updateAdrs(AdsrEffect fx, String[] parameters) {
		float maxAmp = Float.parseFloat(parameters[0]);
		float attTime = Float.parseFloat(parameters[1]);
		float decTime = Float.parseFloat(parameters[2]);
		float susLvl = Float.parseFloat(parameters[3]);
		float relTime = Float.parseFloat(parameters[4]);
		float befAmp = Float.parseFloat(parameters[5]);
		float aftAmp = Float.parseFloat(parameters[6]);
		
		fx.setParameters(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
		fx.notifyAllObservers();
	}

	// bandpass filter
	private static Effect createBandPass(String[] parameters) {
		float centerFreq = Float.parseFloat(parameters[0]);
		float bandWidth = Float.parseFloat(parameters[1]);
		float sampleRate = SoundEngine.out.sampleRate();

		return createBandPass(centerFreq, bandWidth, sampleRate);
	}

	private static Effect createBandPass(float centerFreq, float bandWidth, float sampleRate) {
		return new BandPassFilterEffect(centerFreq, bandWidth, sampleRate);
	}
	
	private static void updateBandPass(BandPassFilterEffect fx, String[] parameters) {
		float centerFreq = Float.parseFloat(parameters[0]);
		float bandWidth = Float.parseFloat(parameters[1]);
		
		fx.setCenterFreq(centerFreq);
		fx.setBandWidth(bandWidth);
		fx.notifyAllObservers();
	}

	// bitchrush
	private static Effect createBitChrush(String[] parameters) {
		int bitResolution = Integer.parseInt(parameters[0]);
		float sampleRate = SoundEngine.out.sampleRate();

		return createBitChrush(bitResolution, sampleRate);
	}

	private static Effect createBitChrush(int bitResolution, float sampleRate) {
		return new BitChrushEffect(bitResolution, sampleRate);
	}
	
	private static void updateBitChrush(BitChrushEffect fx, String[] parameters) {
		int bitResolution = Integer.parseInt(parameters[0]);

		fx.setBitResolution(bitResolution);
		fx.notifyAllObservers();
	}

	// delay
	private static Effect createDelay(String[] parameters) {
		float maxDelayTime = Float.parseFloat(parameters[0]);
		float amplitudeFactor = Float.parseFloat(parameters[1]);
		boolean feedBackOn = Boolean.parseBoolean(parameters[2]);
		boolean passAudioOn = Boolean.parseBoolean(parameters[3]);

		return createDelay(maxDelayTime, amplitudeFactor, feedBackOn, passAudioOn);
	}

	private static Effect createDelay(float maxDelayTime, float amplitudeFactor, boolean feedBackOn,
			boolean passAudioOn) {
		return new DelayEffect(maxDelayTime, amplitudeFactor, feedBackOn, passAudioOn);
	}
	

	private static void updateDelay(DelayEffect fx, String[] parameters) {
		float maxDelayTime = Float.parseFloat(parameters[0]);
		float amplitudeFactor = Float.parseFloat(parameters[1]);
//		boolean feedBackOn = Boolean.parseBoolean(parameters[2]);
//		boolean passAudioOn = Boolean.parseBoolean(parameters[3]);
		
		fx.setMaxDelayTime(maxDelayTime);
		fx.setAmplitudeFactor(amplitudeFactor);
		fx.notifyAllObservers();
	}

	// flanger
	private static Effect createFlanger(String[] parameters) {
		float delayLength = Float.parseFloat(parameters[0]);
		float lfoRate = Float.parseFloat(parameters[1]);
		float delayDepth = Float.parseFloat(parameters[2]);
		float feedbackAmplitude = Float.parseFloat(parameters[3]);
		float dryAmplitude = Float.parseFloat(parameters[4]);
		float wetAmplitude = Float.parseFloat(parameters[5]);

		return createFlanger(delayLength, lfoRate, delayDepth, feedbackAmplitude, dryAmplitude, wetAmplitude);
	}

	private static Effect createFlanger(float delayLength, float lfoRate, float delayDepth, float feedbackAmplitude,
			float dryAmplitude, float wetAmplitude) {
		return new FlangerEffect(delayLength, lfoRate, delayDepth, feedbackAmplitude, dryAmplitude, wetAmplitude);
	}
	
	private static void updateFlanger(FlangerEffect fx, String[] parameters) {
		float delayLength = Float.parseFloat(parameters[0]);
		float lfoRate = Float.parseFloat(parameters[1]);
		float delayDepth = Float.parseFloat(parameters[2]);
		float feedbackAmplitude = Float.parseFloat(parameters[3]);
		float dryAmplitude = Float.parseFloat(parameters[4]);
		float wetAmplitude = Float.parseFloat(parameters[5]);	
		
		fx.setDelayLength(delayLength);
		fx.setLfoRate(lfoRate);
		fx.setDelayDepth(delayDepth);
		fx.setFeedbackAmplitude(feedbackAmplitude);
		fx.setDryAmplitude(dryAmplitude);
		fx.setWetAmplitude(wetAmplitude);
		fx.notifyAllObservers();
	}

	// highpass filter
	private static Effect createHighPass(String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		float sampleRate = SoundEngine.out.sampleRate();
		return createHighPass(freq, sampleRate);
	}

	private static Effect createHighPass(float freq, float sampleRate) {
		return new HighPassFilterEffect(freq, sampleRate);
	}
	
	private static void updateHighPass(HighPassFilterEffect fx, String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		
		fx.setCutOffFreq(freq);
		fx.notifyAllObservers();
	}

	// lowpass filter
	private static Effect createLowPass(String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		float sampleRate = SoundEngine.out.sampleRate();

		return createLowPass(freq, sampleRate);
	}

	private static Effect createLowPass(float freq, float sampleRate) {
		return new LowPassFilterEffect(freq, sampleRate);
	}
	

	private static void updateLowPass(LowPassFilterEffect fx, String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		
		fx.setCutOffFreq(freq);
		fx.notifyAllObservers();
	}

	// moog filter
	private static Effect createMoogFilter(String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		float res = Float.parseFloat(parameters[1]);
		String type = parameters[2];
		
		return createMoogFilter(freq, res, type);
	}

	private static Effect createMoogFilter(float freq, float resonance, String type) {
		return new MoogFilterEffect(freq, resonance, type);
	}
	
	private static void updateMoogFilter(MoogFilterEffect fx, String[] parameters) {
		float freq = Float.parseFloat(parameters[0]);
		float res = Float.parseFloat(parameters[1]);
		String type = parameters[2];
		
		fx.setFrequency(freq);
		fx.setResonance(res);
		fx.setType(type);
		fx.notifyAllObservers();
	}

}
