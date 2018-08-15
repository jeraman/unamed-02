package soundengine.effects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ddf.minim.ugens.BitCrush;
import soundengine.SoundEngine;

public class BitChrushEffect extends BitCrush implements AbstractEffect {

	private int bitResolution;
	private float sampleRate;
	
	private List<BitChrushEffectObserver> observers;
	
	private boolean closed;
	
	@Deprecated
	public BitChrushEffect() {
		this(0,0);
	}
	
	public BitChrushEffect (int bitRes, float sampleRate) {
		super(bitRes, sampleRate);
		this.bitResolution = bitRes;
		this.sampleRate = sampleRate;
		
		this.observers = new ArrayList<BitChrushEffectObserver>();
		this.closed = false;
	}
	
	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (this.isClosed())
			return;

		if (parts[0].trim().equalsIgnoreCase("resolution"))
			this.setBitResolution(Integer.parseInt(parts[1].trim()));
	}
	
	protected int getBitResolution() {
		return bitResolution;
	}

	protected void setBitResolution(int bitResolution) {
		this.bitResolution = bitResolution;
		super.setBitRes(this.bitResolution);
	}

	public AbstractEffect clone() {
		BitChrushEffect clone = new BitChrushEffect(bitResolution, sampleRate);
		this.linkClonedObserver(clone);
		return clone;
	}
	
	@Override
	public synchronized void attach(EffectObserver observer) {
		this.observers.add((BitChrushEffectObserver)observer);
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
	
	private void linkClonedObserver(BitChrushEffect clone) {
		new BitChrushEffectObserver(this, clone);
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
