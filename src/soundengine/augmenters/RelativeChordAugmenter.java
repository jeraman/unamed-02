package soundengine.augmenters;

import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import soundengine.util.MusicTheory;

public class RelativeChordAugmenter extends Augmenter {

	public String type;
	
	public RelativeChordAugmenter(String type) {
		this.type = type;
	}

	@Override
	public Note[] getNotes(int dynamicRoot) {
		Chord result = MusicTheory.generateChordFromMIDI(dynamicRoot, type);
		return result.getNotes();
	}

}
