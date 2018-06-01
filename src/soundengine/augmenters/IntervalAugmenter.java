package soundengine.augmenters;

import org.jfugue.theory.Note;

import soundengine.MusicTheory;

public class IntervalAugmenter extends Augmenter {

	private int root;
	private String type;
	
	public IntervalAugmenter(int root, String type) {
		this.root = root;
		this.type = type;
	}
	
	@Override
	public Note[] getNotes(int dynamicRoot) {
		return MusicTheory.generateInterval(root, type);
	}

}
