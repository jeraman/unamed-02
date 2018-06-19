package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.ugens.ADSR;
import soundengine.generators.FMGenerator;
import soundengine.generators.FMGeneratorObserver;
import soundengine.generators.GeneratorObserver;
import soundengine.generators.OscillatorGeneratorObserver;

public class AdsrEffect extends ADSR implements AbstractEffect {
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
	
	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");

		if (parts[0].trim().equalsIgnoreCase("maxAmp"))
			this.setMaxAmp(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("attTime"))
			this.setAttTime(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("decTime"))
			this.setDecTime(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("susLvl"))
			this.setSusLvl(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("relTime"))
			this.setRelTime(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("befAmp"))
			this.setBefAmp(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("aftAmp"))
			this.setAftAmp(Float.parseFloat(parts[1].trim()));
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
	
	protected void setMaxAmp(float maxAmp) {
		this.maxAmp = maxAmp;
		this.setParameters(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}

	protected void setAttTime(float attTime) {
		this.attTime = attTime;
		this.setParameters(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}

	protected void setDecTime(float decTime) {
		this.decTime = decTime;
		this.setParameters(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}

	protected void setSusLvl(float susLvl) {
		this.susLvl = susLvl;
		this.setParameters(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}

	protected void setRelTime(float relTime) {
		this.relTime = relTime;
		this.setParameters(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}

	protected void setBefAmp(float befAmp) {
		this.befAmp = befAmp;
		this.setParameters(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}

	protected void setAftAmp(float aftAmp) {
		this.aftAmp = aftAmp;
		this.setParameters(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
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
	public AbstractEffect clone() {
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

	@Override
	public void notifyAllObservers(String updatedParameter) {
		for (EffectObserver observer : observers)
			observer.update(updatedParameter);
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
