package soundengine.augmenters;

import org.jfugue.theory.Note;

public abstract class AbstractAugmenter {
	
//	public abstract Note[] getNotes();
	
	public static int getOldValueIfOldIsLessThanZero(int oldValue, int newValue) {
		if (oldValue < 0)
			return newValue;
		else
			return oldValue;
	}
	
	public static int getTheRightVel(int oldVel, int newVel) {
		return getOldValueIfOldIsLessThanZero(oldVel, newVel);
	}
	
	public static int getTheRightPitch(int oldPitch, int newPitch) {
		return getOldValueIfOldIsLessThanZero(oldPitch, newPitch);
	}
	
	public abstract Note[] getNotes(int dynamicRoot, int dynamicVel);
	public abstract void updateParameterFromString(String singleParameter); 
}
