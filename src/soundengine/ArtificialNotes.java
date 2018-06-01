package soundengine;

import java.util.ArrayList;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;
import ddf.minim.ugens.Summer;
import soundengine.augmenters.Augmenter;

/**
 * This class creates virtual music-theory-enriched MIDI notes (called extensions) to be played along 
 * with real MIDI notes.
 * @author jeraman.info
 * @date May 23 2018
 */
public class ArtificialNotes {
	
	private ArrayList<DecoratedNote> artificialNotes;
	
	public ArtificialNotes() {
		artificialNotes = new ArrayList<DecoratedNote>();
	}
	
	private boolean alreadyContainsAPitch(int pitch) {
		return this.artificialNotes.contains(pitch);
	}
	
	protected void addArtificialNote (DecoratedNote baseline, int newNotePitch) {
		if (newPitchMeetsInsertionCriteria(baseline, newNotePitch)) {
			DecoratedNote newNote = baseline.cloneInADifferentPitch(newNotePitch);
			artificialNotes.add(newNote);
		}
	}

	@Deprecated
	void addArtificialInterval (DecoratedNote baseline, int pitch, String intervalType) {		
		Note[] result = MusicTheory.generateInterval(pitch, intervalType);
		for (Note n:result)
			addArtificialNote(baseline, (int)n.getValue());
		
	}
	
	@Deprecated
	public void addArtificialInterval (DecoratedNote baseline, String intervalType) {
		this.addArtificialInterval(baseline, baseline.getPitch(), intervalType);
	}
	
	@Deprecated
	public void addArtificialChord (DecoratedNote baseline, int root, String chordType) {		
		Chord result = MusicTheory.generateChordFromMIDI(root, chordType);
		Note[] notes = result.getNotes();
		for (Note n : notes) 
			addArtificialNote(baseline, (int)n.getValue());
	}
	
	@Deprecated
	public void addArtificialChord (DecoratedNote baseline, String chordType) {
		this.addArtificialChord(baseline, baseline.getPitch(), chordType);
	}
	
	public void addAugmenter(DecoratedNote baseline, Augmenter aug) {
		Note[] notes = aug.getNotes(baseline.getPitch());
		for (Note n : notes) 
			addArtificialNote(baseline, (int)n.getValue());
	}
	
	private boolean newPitchMeetsInsertionCriteria(DecoratedNote baseline, int newNotePitch) {
		return (!alreadyContainsAPitch(newNotePitch) && !isArtificialNoteEqualsAugmentedParent(baseline, newNotePitch));
	}
	
	private boolean isArtificialNoteEqualsAugmentedParent(DecoratedNote baseline, int newNotePitch) {
		return baseline.isPitchEquals(newNotePitch);
	}
		
	public void patchEffects() {
		for (DecoratedNote n: artificialNotes)
			n.patchEffects();
	}
	
	public void unpatchEffects() {
		for (DecoratedNote n: artificialNotes)
			n.unpatchEffects();
	}

	public void noteOn() {
		for (DecoratedNote n: artificialNotes)
			n.noteOn();
	}

	public void noteOff() {
		for (DecoratedNote n: artificialNotes)
			n.defaultNoteOff();
	}
	
	public void noteOffUsingADSR() {
		for (DecoratedNote n: artificialNotes)
			n.noteOffUsingADSR();
	}
	
	public void close() {
		for (DecoratedNote n: artificialNotes)
			n.close();
		artificialNotes = null;
	}

	public void loadUpAllGenerators(Summer s) {
		for (DecoratedNote n: artificialNotes)
			n.loadUpAllGenerators(s);
	}

}
