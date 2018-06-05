package soundengine.augmenters;

import org.jfugue.theory.Note;

public class NoteAugmenter extends Augmenter {

	private int pitch;

	public NoteAugmenter(int pitch) {
		this.pitch = pitch;
	}
	
	protected int getPitch() {
		return pitch;
	}

	protected void setPitch(int pitch) {
		this.pitch = pitch;
	}

	@Override
	public Note[] getNotes(int uselessRoot) {
		return new Note[]{new Note(pitch)};
	}

}
