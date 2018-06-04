package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.effects.BandPass;

public class BandPassFilterEffect extends BandPass implements Effect {
	
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

	public Effect clone() {
		BandPassFilterEffect clone = new BandPassFilterEffect(this.frequency(), this.getBandWidth(), this.sampleRate());
		this.linkClonedObserver(clone);
		return clone;
	}

	@Override
	public void attach(EffectObserver observer) {
		this.observers.add((BandPassFilterEffectObserver) observer);
	}

	@Override
	public void notifyAllObservers() {
		for (EffectObserver observer : observers)
			observer.update();
	}

	private void linkClonedObserver(BandPassFilterEffect clone) {
		new BandPassFilterEffectObserver(this, clone);
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
