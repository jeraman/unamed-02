package generators;

import ddf.minim.AudioOutput;
import ddf.minim.UGen;


public interface Generator {

	public UGen patchEffect(UGen effect);
	
	public void patchOutput(AudioOutput out);
	
	public void unpatchEffect(UGen effect);
	
	public void unpatchOutput(AudioOutput out);
	
	public void noteOn();
	
	public void noteOff();
	
	public void close();
		
	public Generator cloneInADifferentPitch(int newPitch); 
	
	public void noteOffAfterDuration(int duration);
	
//	public void run();
}

