package soundengine.augmenters;

import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import soundengine.util.MusicTheory;

@Deprecated
public class RelativeChordAugmenter extends AbstractAugmenter {

	private int velocity;
	private String type;
	
	public RelativeChordAugmenter(String type) {
		this(type, -1);
	}
	
	public RelativeChordAugmenter(String type, int velocity) {
		this.type = type;
		this.velocity = velocity;
	}
	
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (parts[0].trim().equalsIgnoreCase("type"))
			this.setType(parts[1].trim());
		if (parts[0].trim().equalsIgnoreCase("velocity"))
			this.setVelocity(Integer.parseInt(parts[1].trim()));
	}
	
	protected int getVelocity() {
		return velocity;
	}
	
	protected void setVelocity(int velocity) {
		this.velocity = velocity;
	}
	
	protected String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}

	@Override
	public Note[] getNotes(int dynamicRoot) {
		Chord result = MusicTheory.generateChordFromMIDI(dynamicRoot, velocity, type);
		return result.getNotes();
	}

}
