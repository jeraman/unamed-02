package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.effects.BandPass;

public class BandPassFilterEffect extends BandPass implements AbstractEffect {
	
	private float centerFreq;
	private float bandWidth;
	
	private List<BandPassFilterEffectObserver> observers;
	private boolean closed;

	@Deprecated
	public BandPassFilterEffect() {
		this(0, 0, 0);
	}

	public BandPassFilterEffect(float centerFreq, float bandWidth, float sampleRate) {
		super(centerFreq, bandWidth, sampleRate);

		this.centerFreq = centerFreq;
		this.bandWidth = bandWidth;
		
		this.observers = new ArrayList<BandPassFilterEffectObserver>();
		this.closed = false;
	}
	
	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");

		if (parts[0].trim().equalsIgnoreCase("centerFreq"))
			this.setCenterFreq(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("bandWidth"))
			this.setBandWidth(Float.parseFloat(parts[1].trim()));
	}

	
	protected float getCenterFreq() {
		return centerFreq;
	}

	protected void setCenterFreq(float centerFreq) {
		this.centerFreq = centerFreq;
		super.setFreq(this.centerFreq);
	}

	public float getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(float bandWidth) {
		this.bandWidth = bandWidth;
		super.setBandWidth(bandWidth);
	}

	public AbstractEffect clone() {
		BandPassFilterEffect clone = new BandPassFilterEffect(this.frequency(), this.getBandWidth(), this.sampleRate());
		this.linkClonedObserver(clone);
		return clone;
	}

	@Override
	public synchronized void attach(EffectObserver observer) {
		this.observers.add((BandPassFilterEffectObserver) observer);
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

	private void linkClonedObserver(BandPassFilterEffect clone) {
		new BandPassFilterEffectObserver(this, clone);
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
