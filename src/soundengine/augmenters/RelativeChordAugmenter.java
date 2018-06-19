package soundengine.augmenters;

import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import soundengine.util.MusicTheory;

public class RelativeChordAugmenter extends AbstractAugmenter {

	public String type;
	
	public RelativeChordAugmenter(String type) {
		this.type = type;
	}
	
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (parts[0].trim().equalsIgnoreCase("type"))
			this.setType(parts[1].trim());
	}
	
	protected String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}

	@Override
	public Note[] getNotes(int dynamicRoot) {
		Chord result = MusicTheory.generateChordFromMIDI(dynamicRoot, type);
		return result.getNotes();
	}

}
