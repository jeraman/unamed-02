package soundengine;

/**
 * Interface for sound-related services available to the UI.
 * @author jeraman.info
 *
 */
public interface SoundEngineFacade {
	
	public void addGenerator(String id, String type, String[] parameters);
	public void updateGenerator(String id, String[] parameters);
	public void updateGenerator(String id, String singleParameter);
	public void removeGenerator(String id);
	
	public void addEffect(String id, String type, String[] parameters);
	public void updateEffect(String id, String[] parameters);
	public void updateEffect(String id, String singleParameter);
	public void removeEffect(String id);

	public void addAugmenter(String id, String type, String[] parameters);
	public void updateAugmenter(String id, String[] parameters);
	public void updateAugmenter(String id, String singleParameter);
	public void removeAugmenter(String id);

	public void noteOnWithoutAugmenters(int channel, int pitch, int velocity);
	public void noteOn(int channel, int pitch, int velocity);
	public void noteOff(int channel, int pitch, int velocity);
	
}
