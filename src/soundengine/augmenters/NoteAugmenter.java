package soundengine.augmenters;

import org.jfugue.theory.Note;

public class NoteAugmenter extends Augmenter {

	private int pitch;

	public NoteAugmenter(int pitch) {
		this.pitch = pitch;
	}
	
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (parts[0].trim().equalsIgnoreCase("pitch"))
			this.setPitch(Integer.parseInt(parts[1].trim()));
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
