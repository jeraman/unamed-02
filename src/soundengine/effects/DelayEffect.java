package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.ugens.Delay;

public class DelayEffect extends Delay implements Effect{
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
	public Effect clone() {
		DelayEffect clone = new DelayEffect(this.maxDelayTime, this.amplitudeFactor, this.feedBackOn, this.passAudioOn);
		this.linkClonedObserver(clone);
		return clone;
	}
	
	@Override
	public void attach(EffectObserver observer) {
		this.observers.add((DelayEffectObserver)observer);
	}

	@Override
	public void notifyAllObservers() {
		for (EffectObserver observer : observers)
			observer.update();
	}
	
	private void linkClonedObserver(DelayEffect clone) {
		new DelayEffectObserver(this, clone);
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
