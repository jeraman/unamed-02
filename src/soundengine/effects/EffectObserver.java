package soundengine.effects;

import soundengine.core.Observer;

public abstract class EffectObserver extends Observer{

	Effect original;
	Effect updatable;

	public EffectObserver(Effect original, Effect updatable) {
		this.original = original;
		this.updatable = updatable;
		
		this.original.attach(this);
	}
	
	public void forwardUpdatesToUpdatable() {
		this.updatable.notifyAllObservers();
	}
	
	public void forwardUpdatesToUpdatable(String singleParameter) {
		this.updatable.notifyAllObservers(singleParameter);
	}
	
	public boolean isClosed() {
		if (this.updatable.isClosed())
			return true;
		else
			return false;
	}
	
	public void update(String singleParameter) {
		System.out.println("observer " + this + " updating " + updatable + " based on " + original);
		updatable.updateParameterFromString(singleParameter);
		System.out.println("Forwarding changes to children...");
		this.forwardUpdatesToUpdatable(singleParameter);
	}

}
