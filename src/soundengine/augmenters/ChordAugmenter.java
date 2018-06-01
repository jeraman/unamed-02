package soundengine.augmenters;

import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import soundengine.MusicTheory;

public class ChordAugmenter extends Augmenter {
	private int root;
	private String type;
	
	public ChordAugmenter(int root, String type) {
		this.root = root;
		this.type = type;
	}

	@Override
	public Note[] getNotes(int uselessRoot) {
		Chord result = MusicTheory.generateChordFromMIDI(root, type);
		return result.getNotes();
	}
	
}
