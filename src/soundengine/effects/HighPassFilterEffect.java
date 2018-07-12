package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.effects.HighPassSP;

public class HighPassFilterEffect extends HighPassSP implements AbstractEffect {

	private List<HighPassFilterEffectObserver> observers;
	private boolean closed;
	
	private float cutOffFreq;
	
	@Deprecated
	public HighPassFilterEffect() {
		this(0,0);
	}
	
	public HighPassFilterEffect(float freq, float sampleRate) {
		super(freq, sampleRate);
		
		this.cutOffFreq = freq;
		
		this.observers = new ArrayList<HighPassFilterEffectObserver>();
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
		HighPassFilterEffect clone = new HighPassFilterEffect(this.frequency(), this.sampleRate());
		this.linkClonedObserver(clone);
		return clone;
	}
	
	@Override
	public synchronized void attach(EffectObserver observer) {
		this.observers.add((HighPassFilterEffectObserver)observer);
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
	
	private void linkClonedObserver(HighPassFilterEffect clone) {
		new HighPassFilterEffectObserver(this, clone);
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
