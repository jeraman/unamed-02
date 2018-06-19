package soundengine.generators;

import soundengine.core.Observer;

public abstract class GeneratorObserver extends Observer {

	AbstractGenerator original;
	AbstractGenerator updatable;

	public GeneratorObserver(AbstractGenerator original, AbstractGenerator updatable) {
		this.original = original;
		this.updatable = updatable;

		this.original.attach(this);
	}

	public void forwardUpdatesToUpdatable() {
		if (!isClosed())
			this.updatable.notifyAllObservers();
	}
	
	public void forwardUpdatesToUpdatable(String singleParameter) {
		if (!isClosed())
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
