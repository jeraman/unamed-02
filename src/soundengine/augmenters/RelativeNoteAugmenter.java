package soundengine.augmenters;

import org.jfugue.theory.Note;


//is there any need for this class?

public class RelativeNoteAugmenter extends Augmenter {

	int pitch;

	public RelativeNoteAugmenter(int pitch) {
		this.pitch = pitch;
	}
	
	protected int getPitch() {
		return pitch;
	}

	protected void setPitch(int pitch) {
		this.pitch = pitch;
	}

	@Override
	public Note[] getNotes(int dynamicNote) {
		this.pitch = dynamicNote;
		return new Note[]{new Note(pitch)};
	}

}
