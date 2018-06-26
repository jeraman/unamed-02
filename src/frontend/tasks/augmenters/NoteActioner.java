package frontend.tasks.augmenters;

import java.io.Serializable;

import frontend.Main;
import soundengine.util.Util;

public class NoteActioner extends AbstractMusicActioner {
	
	private int pitch;
	
	public NoteActioner(int pitch, int velocity, int duration) {
		super(velocity, duration);
		this.pitch = pitch;
	}
	
	protected int getPitch() {
		return pitch;
	}

	protected void setPitch(int pitch) {
		if (!this.locked)
			this.pitch = pitch;
	}
	
	protected void noteOnInSoundEngine() {
		Main.eng.noteOnWithoutAugmenters(0, pitch, velocity);
	}

	protected void noteOffInSoundEngine() {
		Main.eng.noteOff(0, pitch, 0);
	}

}
