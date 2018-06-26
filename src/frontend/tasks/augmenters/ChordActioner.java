package frontend.tasks.augmenters;

import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import frontend.Main;
import soundengine.util.MusicTheory;

public class ChordActioner extends AbstractMusicActioner {
	
	private int root;
	private String type;
	
	public ChordActioner(int root, String type, int velocity, int duration) {
		super(velocity, duration);
		this.root = root;
		this.type = type;
	}
	
	protected int getRoot() {
		return root;
	}

	protected void setRoot(int root) {
		if (!this.locked)
			this.root = root;
	}

	protected String getChordType() {
		return type;
	}

	protected void setChordType(String type) {
		if (!this.locked)
			this.type = type;
	}

	@Override
	protected void noteOnInSoundEngine() {
		Note[] notes = MusicTheory.generateChordFromMIDI(root, velocity, type);
		for (Note n : notes) 
			Main.eng.noteOnWithoutAugmenters(0, n.getValue(), velocity);
	}

	@Override
	protected void noteOffInSoundEngine() {
		Note[] notes = MusicTheory.generateChordFromMIDI(root, velocity, type);
		for (Note n : notes) 
			Main.eng.noteOff(0, n.getValue(), velocity);
	}

}
