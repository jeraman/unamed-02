package soundengine.augmenters;

import org.jfugue.theory.Note;

import soundengine.util.MusicTheory;

public class IntervalAugmenter extends Augmenter {

	private int root;
	private String type;
	
	public IntervalAugmenter(int root, String type) {
		this.root = root;
		this.type = type;
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
	public Note[] getNotes(int dynamicRoot) {
		return MusicTheory.generateInterval(root, type);
	}

}
