package soundengine.effects;

public interface Effect {
	public void updateParameterFromString(String singleParameter);
	public Effect clone();
	public void notifyAllObservers();
	public void notifyAllObservers(String updatedParameter);
	public void attach(EffectObserver obs);
	public void close();
	public boolean isClosed();
//	private void linkClonedObserver();
	public void unlinkOldObservers();
}
