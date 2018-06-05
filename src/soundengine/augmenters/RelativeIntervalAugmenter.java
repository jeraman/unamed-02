package soundengine.augmenters;

import org.jfugue.theory.Note;

import soundengine.util.MusicTheory;

public class RelativeIntervalAugmenter extends Augmenter {
	
	private String type;
	
	public RelativeIntervalAugmenter(String type) {
		this.type = type;
	}
	
	protected String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}

	@Override
	public Note[] getNotes(int dynamicRoot) {
		return MusicTheory.generateInterval(dynamicRoot, type);
	}
}
