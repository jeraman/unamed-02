package soundengine.generators;

import ddf.minim.AudioOutput;
import ddf.minim.UGen;


public interface AbstractGenerator {

	public void patchEffect(UGen effect);
	
	public void patchOutput(AudioOutput out);
	
	public void unpatchEffect(UGen effect);
	
	public void unpatchOutput(AudioOutput out);
	
	public void updateParameterFromString(String singleParameter);
	
	public void noteOn();
	
	public void noteOff();

	public void noteOffAfterDuration(int duration);
	
	public void attach (GeneratorObserver observer);
	
	public void notifyAllObservers();
	
	public void notifyAllObservers(String updatedParameter);
	
	public void unlinkOldObservers();
	
	public boolean isClosed();

	public void close();
		
	public AbstractGenerator cloneWithPitchAndVelocityIfUnlocked(int newPitch, int newVelocity);
	
	public AbstractGenerator clone(int newPitch, int newVelocity, int newDuration); 
	
	public AbstractGenerator cloneWithPitchAndVelocity(int newPitch, int newVelocity); 

	public AbstractGenerator cloneWithPitch(int newPitch);	
	
}

