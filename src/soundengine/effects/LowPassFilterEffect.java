package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.effects.*;

public class LowPassFilterEffect extends LowPassFS implements AbstractEffect {

	private List<LowPassFilterEffectObserver> observers;
	private boolean closed;
	
	private float cutOffFreq;
	
	@Deprecated
	public LowPassFilterEffect() {
		this(0, 0);
	}

	public LowPassFilterEffect(float freq, float sampleRate) {
		super(freq, sampleRate);
		
		this.cutOffFreq = freq;
		
		this.observers = new ArrayList<LowPassFilterEffectObserver>();
		this.closed = false;
	}
	
	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");

		if (parts[0].trim().equalsIgnoreCase("cutoff"))
			this.setCutOffFreq(Float.parseFloat(parts[1].trim()));
	}
	
	public float getCutOffFreq() {
		return cutOffFreq;
	}

	public void setCutOffFreq(float cutOffFreq) {
		this.cutOffFreq = cutOffFreq;
		super.setFreq(this.cutOffFreq);
	}

	public AbstractEffect clone() {
		LowPassFilterEffect clone = new LowPassFilterEffect(this.frequency(), this.sampleRate());
		this.linkClonedObserver(clone);
		return clone;
	}
	
	@Override
	public void attach(EffectObserver observer) {
		this.observers.add((LowPassFilterEffectObserver)observer);
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
	
	private void linkClonedObserver(LowPassFilterEffect clone) {
		new LowPassFilterEffectObserver(this, clone);
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
