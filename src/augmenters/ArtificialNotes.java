package augmenters;

import java.util.ArrayList;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;
import ddf.minim.UGen;
import ddf.minim.ugens.Summer;

/**
 * This class creates virtual music-theory-enriched MIDI notes (called extensions) to be played along 
 * with real MIDI notes.
 * @author jeraman.info
 * @date May 23 2018
 */
public class ArtificialNotes {
	
	private ArrayList<AugmentedNote> artificialNotes;
	
	public ArtificialNotes() {
		artificialNotes = new ArrayList<AugmentedNote>();
	}
	
	private boolean alreadyContainsAPitch(int pitch) {
		return this.artificialNotes.contains(pitch);
	}
	
	protected void addArtificialNote (AugmentedNote baseline, int newNotePitch) {
		if (newPitchMeetsInsertionCriteria(baseline, newNotePitch)) {
			AugmentedNote newNote = baseline.cloneInADifferentPitch(newNotePitch);
			artificialNotes.add(newNote);
		}
	}

	void addArtificialInterval (AugmentedNote baseline, int pitch, String intervalType) {		
		Note[] result = MusicTheory.generateInterval(pitch, intervalType);
		for (Note n:result)
			addArtificialNote(baseline, (int)n.getValue());
		
	}
	
	public void addArtificialInterval (AugmentedNote baseline, String intervalType) {
		this.addArtificialInterval(baseline, baseline.getPitch(), intervalType);
	}
	
	public void addArtificialChord (AugmentedNote baseline, int root, String chordType) {		
		Chord result = MusicTheory.generateChordFromMIDI(root, chordType);
		Note[] notes = result.getNotes();
		for (Note n : notes) 
			addArtificialNote(baseline, (int)n.getValue());
	}
	
	public void addArtificialChord (AugmentedNote baseline, String chordType) {
		this.addArtificialChord(baseline, baseline.getPitch(), chordType);
	}
	
	private boolean newPitchMeetsInsertionCriteria(AugmentedNote baseline, int newNotePitch) {
		return (!alreadyContainsAPitch(newNotePitch) && !isArtificialNoteEqualsAugmentedParent(baseline, newNotePitch));
	}
	
	private boolean isArtificialNoteEqualsAugmentedParent(AugmentedNote baseline, int newNotePitch) {
		return baseline.isPitchEquals(newNotePitch);
	}
		
	public void patchEffects() {
		for (AugmentedNote n: artificialNotes)
			n.patchEffects();
	}
	
	public void unpatchEffects() {
		for (AugmentedNote n: artificialNotes)
			n.unpatchEffects();
	}

	public void noteOn() {
		for (AugmentedNote n: artificialNotes)
			n.noteOn();
	}

	public void noteOff() {
		for (AugmentedNote n: artificialNotes)
			n.defaultNoteOff();
	}
	
	public void noteOffUsingADSR() {
		for (AugmentedNote n: artificialNotes)
			n.noteOffUsingADSR();
	}
	
	public void close() {
		for (AugmentedNote n: artificialNotes)
			n.close();
		artificialNotes = null;
	}

	public void loadUpAllGenerators(Summer s) {
		for (AugmentedNote n: artificialNotes)
			n.loadUpAllGenerators(s);
	}

}
