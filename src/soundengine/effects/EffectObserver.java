package soundengine.effects;

import soundengine.core.Observer;

public abstract class EffectObserver extends Observer{

	AbstractEffect original;
	AbstractEffect updatable;

	public EffectObserver(AbstractEffect original, AbstractEffect updatable) {
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
		updatable.updateParameterFromString(singleParameter);
		this.forwardUpdatesToUpdatable(singleParameter);
//		System.out.println("observer " + this + " updating " + updatable + " based on " + original);
//		System.out.println("Forwarding changes to children...");
	}

}
