package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.ugens.Flanger;

public class FlangerEffect extends Flanger implements AbstractEffect {

	private float delayLength; //delay length in milliseconds ( clamped to [0,100] )
	private float lfoRate; //lfo rate in Hz ( clamped at low end to 0.001 )
	private float delayDepth; // delay depth in milliseconds ( minimum of 0 )
	private float feedbackAmplitude; // amount of feedback ( clamped to [0,1] )
	private float dryAmplitude; //amount of dry signal ( clamped to [0,1] )
	private float wetAmplitude; //amount of wet signal ( clamped to [0,1] )
	
	
	private List<FlangerEffectObserver> observers;
	private boolean closed;

	
	@Deprecated
	public FlangerEffect() {
		this(0, 0, 0, 0, 0, 0);
	}
	
	public FlangerEffect(float delayLength, float lfoRate, float delayDepth, float feedbackAmplitude,
			float dryAmplitude, float wetAmplitude) {
		super(delayLength, lfoRate, delayDepth, feedbackAmplitude, dryAmplitude, wetAmplitude);
		
		this.delayLength = delayLength;
		this.lfoRate = lfoRate;
		this.delayDepth = delayDepth;
		this.feedbackAmplitude = feedbackAmplitude;
		this.dryAmplitude = dryAmplitude;
		this.wetAmplitude = wetAmplitude;
		
		this.observers = new ArrayList<FlangerEffectObserver>();
		this.closed = false;
		
	}
	
	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");

		if (parts[0].trim().equalsIgnoreCase("delayLength"))
			this.setDelayDepth(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("lfoRate"))
			this.setLfoRate(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("delayDepth"))
			this.setDelayDepth(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("feedbackAmplitude"))
			this.setFeedbackAmplitude(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("dryAmplitude"))
			this.setDryAmplitude(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("wetAmplitude"))
			this.setWetAmplitude(Float.parseFloat(parts[1].trim()));
	}
	
	protected float getDelayLength() {
		return delayLength;
	}

	protected void setDelayLength(float delayLength) {
		this.delayLength = delayLength;
		this.delay.setLastValue(this.delayLength);
	}

	protected float getLfoRate() {
		return lfoRate;
	}

	protected void setLfoRate(float lfoRate) {
		this.lfoRate = lfoRate;
		this.rate.setLastValue(this.lfoRate);
	}

	protected float getDelayDepth() {
		return delayDepth;
	}

	protected void setDelayDepth(float delayDepth) {
		this.delayDepth = delayDepth;
		this.depth.setLastValue(this.delayDepth);
	}

	protected float getFeedbackAmplitude() {
		return feedbackAmplitude;
	}

	protected void setFeedbackAmplitude(float feedbackAmplitude) {
		this.feedbackAmplitude = feedbackAmplitude;
		this.feedback.setLastValue(this.feedbackAmplitude);
	}

	protected float getDryAmplitude() {
		return dryAmplitude;
	}

	protected void setDryAmplitude(float dryAmplitude) {
		this.dryAmplitude = dryAmplitude;
		this.dry.setLastValue(this.dryAmplitude);
	}

	protected float getWetAmplitude() {
		return wetAmplitude;
	}

	protected void setWetAmplitude(float wetAmplitude) {
		this.wetAmplitude = wetAmplitude;
		this.wet.setLastValue(this.wetAmplitude);
	}
	
	public AbstractEffect clone () {
		FlangerEffect clone = new FlangerEffect(delayLength, lfoRate, delayDepth, feedbackAmplitude, dryAmplitude, wetAmplitude);
		this.linkClonedObserver(clone);
		return clone;
	}
	
	
	@Override
	public void attach(EffectObserver observer) {
		this.observers.add((FlangerEffectObserver)observer);
	}

	@Override
	public void notifyAllObservers() {
		for (EffectObserver observer : observers)
			observer.update();
	}
	
	@Override
	public void notifyAllObservers(String updatedParameter) {
		for (EffectObserver observer : observers)
			observer.update(updatedParameter);
	}
	
	private void linkClonedObserver(FlangerEffect clone) {
		new FlangerEffectObserver(this, clone);
	}

	public void unlinkOldObservers() {
		for (int i = observers.size() - 1; i >= 0; i--)
			if (observers.get(i).isClosed())
				this.observers.remove(i);
	}
	
	@Override
	public void close() {
		this.closed = true;
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}
	

}
