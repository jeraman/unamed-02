package soundengine.augmenters;

import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import soundengine.util.MusicTheory;

public class ChordAugmenter extends AbstractAugmenter {
	private int root;
	private String type;
	
	public ChordAugmenter(int root, String type) {
		this.root = root;
		this.type = type;
	}
	
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (parts[0].trim().equalsIgnoreCase("root"))
			this.setRoot(Integer.parseInt(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("type"))
			this.setType(parts[1].trim());
	}
	
	protected int getRoot() {
		return root;
	}

	protected void setRoot(int root) {
		this.root = root;
	}
	
	protected String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}

	@Override
	public Note[] getNotes(int uselessRoot) {
		Chord result = MusicTheory.generateChordFromMIDI(root, type);
		return result.getNotes();
	}
	
}
