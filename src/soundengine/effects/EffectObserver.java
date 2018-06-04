package soundengine.effects;

import soundengine.Observer;

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
	
	public boolean isClosed() {
		if (this.updatable.isClosed())
			return true;
		else
			return false;
	}
}
