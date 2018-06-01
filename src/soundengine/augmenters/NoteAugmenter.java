package soundengine.augmenters;

import org.jfugue.theory.Note;

public class NoteAugmenter extends Augmenter {

	int pitch;

	public NoteAugmenter(int pitch) {
		this.pitch = pitch;
	}

	@Override
	public Note[] getNotes(int dynamicNote) {
		this.pitch = dynamicNote;
		return new Note[]{new Note(pitch)};
	}

}
