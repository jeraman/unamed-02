package soundengine.augmenters;

import java.io.Serializable;

import org.jfugue.theory.Note;

import soundengine.util.Util;

public abstract class AbstractAugmenter {
	
	private int duration;
	
	public AbstractAugmenter(int duration) {
		this.duration = duration;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

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
