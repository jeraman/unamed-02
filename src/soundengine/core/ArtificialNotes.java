package soundengine.core;

import java.util.ArrayList;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;
import ddf.minim.ugens.Summer;
import soundengine.augmenters.AbstractAugmenter;
import soundengine.util.MusicTheory;

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
	
	protected void addArtificialNote (DecoratedNote baseline, int newNotePitch, int newVelocity, int newDuration) {
		if (newPitchMeetsInsertionCriteria(baseline, newNotePitch)) {
			DecoratedNote newNote = baseline.cloneInADifferentPitchAndVelocityAndDuration(newNotePitch, newVelocity, newDuration);
			artificialNotes.add(newNote);
		}
	}

	public void addAugmenter(DecoratedNote baseline, AbstractAugmenter aug) {
		Note[] notes = aug.getNotes(baseline.getPitch(), baseline.getVelocity());
		for (Note n : notes) 
			addArtificialNote(baseline, (int)n.getValue(), Byte.toUnsignedInt(n.getOnVelocity()), aug.getDuration());
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
