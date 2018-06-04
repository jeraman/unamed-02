package soundengine.generators;

import soundengine.Observer;

public abstract class GeneratorObserver extends Observer {

	Generator original;
	Generator updatable;

	public GeneratorObserver(Generator original, Generator updatable) {
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
