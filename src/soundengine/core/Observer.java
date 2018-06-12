package soundengine.core;

public abstract class Observer {
	public abstract void update();
	public abstract void update(String singleParameter);
	public abstract void forwardUpdatesToUpdatable();
	public abstract void forwardUpdatesToUpdatable(String singleParameter);
}
