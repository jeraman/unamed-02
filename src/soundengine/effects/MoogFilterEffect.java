package soundengine.effects;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.ugens.MoogFilter;

public class MoogFilterEffect extends MoogFilter implements Effect {
	private float freq;
	private float res;
	private String filterType;
	
	private List<MoogFilterEffectObserver> observers;
	private boolean closed;

	@Deprecated
	public MoogFilterEffect() {
		this(0, 0, "LP");
	}

	public MoogFilterEffect(float frequencyInHz, float normalizedResonance, String filterType) {
		super(frequencyInHz, normalizedResonance, convertType(filterType));
		this.freq = frequencyInHz;
		this.res = normalizedResonance;
		this.filterType = filterType;
		
		this.observers = new ArrayList<MoogFilterEffectObserver>();
		this.closed = false;
	}
	
	@Override
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");

		if (parts[0].trim().equalsIgnoreCase("centerFreq"))
			this.setFrequency(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("resonance"))
			this.setResonance(Float.parseFloat(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("filterType"))
			this.setType(parts[1].trim());
	}
	
	public float getFrequency() {
		return this.freq;
	}
	
	public void setFrequency(float f) {
		this.freq = f;
		this.frequency.setLastValue(this.freq);
	}
	
	public float getResonance() {
		return this.res;
	}
	
	public void setResonance(float r) {
		this.res = r;
		this.resonance.setLastValue(this.res);
	}
	
	public String getType() {
		return this.filterType;
	}
	
	public void setType(String t) {
		this.filterType = t;
		this.type = convertType(this.filterType);
	}

	@Override
	public Effect clone() {
		MoogFilterEffect clone = new MoogFilterEffect(this.freq, this.res, this.filterType);
		this.linkClonedObserver(clone);
		return clone;
	}

	private static Type convertType(String type) {
		Type result = null;
		if (type.trim().equalsIgnoreCase("LP"))
			result = Type.LP;
		if (type.trim().equalsIgnoreCase("HP"))
			result = Type.HP;
		if (type.trim().equalsIgnoreCase("BP"))
			result = Type.BP;

		return result;
	}
	
	@Override
	public void attach(EffectObserver observer) {
		this.observers.add((MoogFilterEffectObserver)observer);
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
	
	private void linkClonedObserver(MoogFilterEffect clone) {
		new MoogFilterEffectObserver(this, clone);
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
