package soundengine.core;

import java.io.Serializable;
import java.util.ArrayList;
import org.jfugue.theory.Chord;

import soundengine.generators.AbstractGenerator;
import soundengine.generators.GeneratorFactory;
import soundengine.util.MusicTheory;

/**
 * Stores all notes currently played on the MIDI input device,
 * @author jeraman.info
 *
 */
public class DecoratedNoteMemory {
	private ArrayList<DecoratedNote> memory;
	private ArrayList<Integer> removalLine;
	private String lastNote;
	
	private static final int CONCURRENT_NOTES_LIMIT = 5;

	public DecoratedNoteMemory() {
		memory = new ArrayList<DecoratedNote>();
		removalLine = new ArrayList<Integer>();
		lastNote = "";
	}
	
//	public synchronized void put(int channel, int note, int velocity) {
//		this.put(channel, note, velocity);
//	}

	public synchronized void put(int channel, int note, int velocity) {
		DecoratedNote newNote = new DecoratedNote(channel, note, velocity);
		this.put(newNote);
	}
	
	public synchronized void put (DecoratedNote aug) {		
		controlsNumberOfConcurrentNotes();
		memory.add(aug);
		this.updateLastNote();
	}

	public synchronized DecoratedNote remove(int note) {
		int noteIndex = getElementIndex(note);
		DecoratedNote result = null;

		this.updateLastNote();
		
		if (noteIndex < 0) {
			
//			System.out.println("adding: " + note +  " to the removal line");
//			System.out.println("memory.size(): " + memory.size());
//			System.out.println("memory: " + memory);

			if (!isQueuedToBeDeleted(note))
				removalLine.add(note);
			return null;
		}
		else 
			result = memory.remove(noteIndex);
		
		return result;
	}
	
	@Deprecated
	public synchronized void update() {
		tryToClearMemory();
	}
	
	@Deprecated
	public synchronized void tryToClearMemory() {
		for (int note:removalLine) 
			this.removeAndNoteOff(note);
	}
	
	private synchronized DecoratedNote removeAndNoteOff(int note) {
		DecoratedNote n = this.remove(note);
		n.noteOff();
		n.close();
//		System.out.println("removing and killing note " + note);
		return n;
	}
	
	public synchronized boolean isQueuedToBeDeleted (int note) {
		return removalLine.contains(note);
	}

	private synchronized void controlsNumberOfConcurrentNotes() {
		if (isBiggerThanNoteLimit())
			removesOldestNote();
	}

	private synchronized boolean isBiggerThanNoteLimit() {
		return (this.size() > CONCURRENT_NOTES_LIMIT);
	}

	private synchronized void removesOldestNote() {
		if (size() > 0) {
			this.removeAndNoteOff(memory.get(0).getPitch());
		}
	}

	public synchronized int size() {
		return memory.size();
	}

	private synchronized int getElementIndex(int wantedNote) {
		int result = -1;
		for (int i = 0; i < memory.size(); i++)
			if (memory.get(i).isPitchEquals(wantedNote))
				result = i;
		return result;
	}

	public synchronized int[] getNoteArray() {
		int[] individualNotes = new int[memory.size()];
		for (int i = 0; i < memory.size(); i++)
			individualNotes[i] = memory.get(i).getPitch();
		return individualNotes;
	}

	public synchronized int[] getToBeDeletedArray() {
		int[] individualNotes = new int[removalLine.size()];
		for (int i = 0; i < removalLine.size(); i++)
			individualNotes[i] = removalLine.get(i);
		return individualNotes;
	}

	public synchronized DecoratedNote get(int index) {
		if (index >= 0 && index < memory.size())
			return memory.get(index);
		else
			return null;
	}

	public synchronized DecoratedNote getStoredNotebyNoteValue(int wantedNote) {
		int noteIndex = getElementIndex(wantedNote);

		if (noteIndex == -1)
			return null;
		else
			return memory.get(noteIndex);
	}

	private synchronized void updateLastNote() {
		if (memory.size() > 0)
			this.lastNote = MusicTheory.noteFromMIDI(memory.get(memory.size()-1).getPitch());
	}
	
	public String getLastPlayedNote() {
		return this.lastNote;
	}
	
	public boolean thereIsKeyDown() {
		return (memory.size() > 0);
	}
	
	public boolean thereIsKeyReleased() {
		return !this.thereIsKeyDown();
	}
	
	public synchronized String identifyWhatUserIsPlaying() {
		int size = memory.size();
		String text;

		if (size == 0) // if no notes are being played
			text = "nothing";
		else if (size == 1) // if one note
			text = "note:" + MusicTheory.noteFromMIDI(memory.get(0).getPitch());
		else if (size == 2) // if an interval
			text = "interval:" + MusicTheory.identifyIntervalFromMIDI(memory.get(0).getPitch(), memory.get(1).getPitch());
		else { // if a chord
			int[] notes = this.getNoteArray();
			Chord c = MusicTheory.identifyChordFromMIDI(notes);

			if (c != null)
				text = "chord:" + c;
			else
				text = "intervals:" + MusicTheory.identifyIntervalFromMIDI(notes);
		}
		
		return text;
	}
}