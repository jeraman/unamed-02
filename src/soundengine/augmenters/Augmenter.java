package soundengine.augmenters;

import org.jfugue.theory.Note;

public abstract class Augmenter {
	
//	public abstract Note[] getNotes();
	public abstract Note[] getNotes(int dynamicRoot);
}
