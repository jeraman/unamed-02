package soundengine.effects;

public interface Effect {
	public Effect clone();
	public void notifyAllObservers();
	public void attach(EffectObserver obs);
	public void close();
	public boolean isClosed();
//	private void linkClonedObserver();
	public void unlinkOldObservers();
}
