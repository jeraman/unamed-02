package soundengine.augmenters;

import org.jfugue.theory.Note;

public abstract class AbstractAugmenter {
	
//	public abstract Note[] getNotes();
	public abstract Note[] getNotes(int dynamicRoot);
	public abstract void updateParameterFromString(String singleParameter); 
}