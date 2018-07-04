package frontend.tasks.augmenters;

import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import frontend.Main;
import soundengine.SoundEngine;
import soundengine.util.MusicTheory;

public class ChordActioner extends AbstractMusicActioner {

	private int root;
	private String type;

	public ChordActioner(int root, String type, int velocity, int duration, SoundEngine eng) {
		super(velocity, duration, eng);
		this.root = root;
		this.type = type;
	}

	protected int getRoot() {
		return root;
	}

	protected void setRoot(int root) {
		//if (!this.locked)
		if (this.root != root) {
			this.noteOffInSoundEngine();
			this.root = root;
		}
	}

	protected String getChordType() {
		return type;
	}

	protected void setChordType(String type) {
		//if (!this.locked)
		if (!this.type.equalsIgnoreCase(type)) {
			this.noteOffInSoundEngine();
			this.type = type;
		}
	}

	@Override
	protected void noteOnInSoundEngine() {
		if (root == -1) return;
		Note[] notes = MusicTheory.generateChordFromMIDI(root, velocity, type);
		for (Note n : notes)
			this.eng.noteOnWithoutAugmenters(0, n.getValue(), velocity);
	}

	@Override
	protected void noteOffInSoundEngine() {
		if (root == -1) return;
		Note[] notes = MusicTheory.generateChordFromMIDI(root, velocity, type);
		for (Note n : notes)
			this.eng.noteOff(0, n.getValue(), velocity);
	}

}
