package frontend.tasks.augmenters;

import java.io.Serializable;

import frontend.Main;
import soundengine.SoundEngine;
import soundengine.util.Util;

public class NoteActioner extends AbstractMusicActioner {
	
	private int pitch;
	
	public NoteActioner(int pitch, int velocity, int duration, SoundEngine eng) {
		super(velocity, duration, eng);
		this.pitch = pitch;
	}
	
	protected int getPitch() {
		return pitch;
	}

	protected void setPitch(int pitch) {
		//if (!this.locked)
		if (this.pitch != pitch) {
			this.noteOffInSoundEngine();
			this.pitch = pitch;
		}
	}
	
	protected void noteOnInSoundEngine() {
		this.eng.noteOnWithoutAugmenters(0, pitch, velocity);
	}

	protected void noteOffInSoundEngine() {
		this.eng.noteOff(0, pitch, 0);
	}

}
