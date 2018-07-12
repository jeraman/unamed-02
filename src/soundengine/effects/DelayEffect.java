package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.ugens.Delay;

public class DelayEffect extends Delay implements AbstractEffect{
	private float maxDelayTime;
	private float amplitudeFactor;
	private boolean feedBackOn;
	private boolean passAudioOn;
	
	private List<DelayEffectObserver> observers;
	private boolean closed;
	
	@Deprecated
	public DelayEffect() {
		this(0,0,true, true);
	}
	
	public DelayEffect(float maxDelayTime, float amplitudeFactor, boolean feedBackOn, boolean passAudioOn) {
		super(maxDelayTime, amplitudeFactor, feedBackOn, passAudioOn);
		this.maxDelayTime = maxDelayTime;
		this.amplitudeFactor = amplitudeFactor;
		this.feedBackOn = feedBackOn;
		this.passAudioOn = passAudioOn;
		
		this.observers = new ArrayList<DelayEffectObserver>();
		this.closed = false;
	}
	
	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");

		if (parts[0].trim().equalsIgnoreCase("delayTime"))
			this.setMaxDelayTime(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("amplitudeFactor"))
			this.setAmplitudeFactor(Float.parseFloat(parts[1].trim()));
	}
	
	protected float getMaxDelayTime() {
		return maxDelayTime;
	}

	protected void setMaxDelayTime(float maxDelayTime) {
		this.maxDelayTime = maxDelayTime;
		super.setDelTime(this.maxDelayTime);
	}

	protected float getAmplitudeFactor() {
		return amplitudeFactor;
	}

	protected void setAmplitudeFactor(float amplitudeFactor) {
		this.amplitudeFactor = amplitudeFactor;
		super.setDelAmp(this.amplitudeFactor);
	}

	@Override
	public AbstractEffect clone() {
		DelayEffect clone = new DelayEffect(this.maxDelayTime, this.amplitudeFactor, this.feedBackOn, this.passAudioOn);
		this.linkClonedObserver(clone);
		return clone;
	}
	
	@Override
	public synchronized void attach(EffectObserver observer) {
		this.observers.add((DelayEffectObserver)observer);
	}

	@Override
	public synchronized void notifyAllObservers() {
		synchronized (observers) {
		for (EffectObserver observer : observers)
			observer.update();
		}
	}
	
	@Override
	public synchronized void notifyAllObservers(String updatedParameter) {
		synchronized (observers) {
		for (EffectObserver observer : observers)
			observer.update(updatedParameter);
		}
	}
	
	private void linkClonedObserver(DelayEffect clone) {
		new DelayEffectObserver(this, clone);
	}

	public synchronized void unlinkOldObservers() {
		synchronized (observers) {
		for (int i = observers.size() - 1; i >= 0; i--)
			if (observers.get(i).isClosed())
				this.observers.remove(i);
		}
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
