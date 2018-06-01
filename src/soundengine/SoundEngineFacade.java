package soundengine;

/**
 * Interface for sound-related services available to the UI.
 * @author jeraman.info
 *
 */
public interface SoundEngineFacade {
	
	public void addGenerator(String id, String type, String[] parameters);
	public void updateGenerator(String id, String[] parameters);
	public void removeGenerator(String id);
	
	public void addEffect(String id, String type, String[] parameters);
	public void updateEffect(String id, String[] parameters);
	public void removeEffect(String id);

	public void addArtificialNote(int newNotePitch);
	public void addArtificialInterval(String intervalType);
	public void addArtificialInterval(int newPitch, String intervalType);
	public void addArtificialChord(String chordType);
	public void addArtificialChord(int newRoot, String chordType);
	public void removeArtificialNote(int newNotePitch);
	public void removeArtificialInterval(String intervalType);
	public void removeArtificialInterval(int newPitch, String intervalType);
	public void removeArtificialChord(String chordType);
	public void removeArtificialChord(int newRoot, String chordType);
	
	public void noteOn(int channel, int pitch, int velocity);
	public void noteOff(int channel, int pitch, int velocity);
	
}
