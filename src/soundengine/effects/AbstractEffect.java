package soundengine.effects;

public interface AbstractEffect {
	public void updateParameterFromString(String singleParameter);
	public AbstractEffect clone();
	public void notifyAllObservers();
	public void notifyAllObservers(String updatedParameter);
	public void attach(EffectObserver obs);
	public void close();
	public boolean isClosed();
//	private void linkClonedObserver();
	public void unlinkOldObservers();
}
