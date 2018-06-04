package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.ugens.ADSR;
import soundengine.generators.FMGenerator;
import soundengine.generators.FMGeneratorObserver;
import soundengine.generators.GeneratorObserver;
import soundengine.generators.OscillatorGeneratorObserver;

public class AdsrEffect extends ADSR implements Effect {
	private float maxAmp; 
	private float attTime; 
	private float decTime;
	private float susLvl;
	private float relTime;
	private float befAmp; 
	private float aftAmp;
		
	private List<AdsrEffectObserver> observers;
	private boolean closed;
	
	@Deprecated
	public AdsrEffect() {
		this(0, 0, 0, 0, 0, 0, 0);
	}
	
	public AdsrEffect(float maxAmp, float attTime, float decTime, float susLvl, float relTime, float befAmp, float aftAmp) {
		super(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
		this.maxAmp = maxAmp;
		this.attTime = attTime;
		this.decTime = decTime;
		this.susLvl = susLvl;
		this.relTime = relTime;
		this.befAmp = befAmp;
		this.aftAmp = aftAmp;
		
		this.observers = new ArrayList<AdsrEffectObserver>();
		this.closed = false;
		
		this.noteOn();
	}
	
	
	public float getMaxAmp() {
		return maxAmp;
	}

	public float getAttTime() {
		return attTime;
	}

	public float getDecTime() {
		return decTime;
	}

	public float getSusLvl() {
		return susLvl;
	}

	public float getRelTime() {
		return relTime;
	}

	public float getBefAmp() {
		return befAmp;
	}

	public float getAftAmp() {
		return aftAmp;
	}

	@Override
	public void setParameters(float maxAmp, float attTime, float decTime, float susLvl, float relTime, float befAmp, float aftAmp) {
		this.maxAmp = maxAmp;
		this.attTime = attTime;
		this.decTime = decTime;
		this.susLvl = susLvl;
		this.relTime = relTime;
		this.befAmp = befAmp;
		this.aftAmp = aftAmp;
		super.setParameters(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}
	
	@Override
	public Effect clone() {
		AdsrEffect clone =  new AdsrEffect(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
		this.linkClonedObserver(clone);
		return clone;
	}

	@Override
	public void attach(EffectObserver observer) {
		this.observers.add((AdsrEffectObserver)observer);
	}

	@Override
	public void notifyAllObservers() {
		for (EffectObserver observer : observers)
			observer.update();
	}
	

	private void linkClonedObserver (AdsrEffect clone) {
		new AdsrEffectObserver(this, clone);
	}
	
	public void unlinkOldObservers () {
		for (int i = observers.size()-1; i >= 0; i--)
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
