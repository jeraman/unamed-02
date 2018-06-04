package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.ugens.BitCrush;
import soundengine.SoundEngine;

public class BitChrushEffect extends BitCrush implements Effect {

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
	
	
	protected int getBitResolution() {
		return bitResolution;
	}

	protected void setBitResolution(int bitResolution) {
		this.bitResolution = bitResolution;
		super.setBitRes(this.bitResolution);
	}

	public Effect clone() {
		BitChrushEffect clone = new BitChrushEffect(bitResolution, sampleRate);
		this.linkClonedObserver(clone);
		return clone;
	}
	
	@Override
	public void attach(EffectObserver observer) {
		this.observers.add((BitChrushEffectObserver)observer);
	}

	@Override
	public void notifyAllObservers() {
		for (EffectObserver observer : observers)
			observer.update();
	}
	
	private void linkClonedObserver(BitChrushEffect clone) {
		new BitChrushEffectObserver(this, clone);
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
