package soundengine.augmenters;

import java.util.ArrayList;
import org.jfugue.theory.Chord;

import soundengine.generators.Generator;
import soundengine.generators.GeneratorFactory;

public class AugmentedNoteMemory {
	private ArrayList<AugmentedNote> memory;
	private ArrayList<Integer> removalLine;
	
	private static final int CONCURRENT_NOTES_LIMIT = 5;

	public AugmentedNoteMemory() {
		memory = new ArrayList<AugmentedNote>();
		removalLine = new ArrayList<Integer>();
	}
	
//	public synchronized void put(int channel, int note, int velocity) {
//		this.put(channel, note, velocity);
//	}

	public synchronized void put(int channel, int note, int velocity) {
		AugmentedNote newNote = new AugmentedNote(channel, note, velocity);
		this.put(newNote);
	}
	
	public synchronized void put (AugmentedNote aug) {		
		controlsNumberOfConcurrentNotes();
		memory.add(aug);
	}

	public synchronized AugmentedNote remove(int note) {
		int noteIndex = getElementIndex(note);
		AugmentedNote result = null;

		if (noteIndex < 0) {
			System.out.println("adding: " + note +  " to the removal line");
			System.out.println("memory.size(): " + memory.size());
			System.out.println("memory: " + memory);
			
			if (!isQueuedToBeDeleted(note))
				removalLine.add(note);
			
			return null;
		}

		else 
			result = memory.remove(noteIndex);
		
		return result;
	}
	
	public synchronized void update() {
		tryToClearMemory();
	}
	
	public synchronized void tryToClearMemory() {
		for (int note:removalLine) 
			this.removeAndNoteOff(note);
	}
	
	private synchronized AugmentedNote removeAndNoteOff(int note) {
		AugmentedNote n = this.remove(note);
		n.noteOff();
		n.close();
		System.out.println("removing and killing note " + note);
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

	public synchronized AugmentedNote get(int index) {
		if (index >= 0 && index < memory.size())
			return memory.get(index);
		else
			return null;
	}

	public synchronized AugmentedNote getStoredNotebyNoteValue(int wantedNote) {
		int noteIndex = getElementIndex(wantedNote);

		if (noteIndex == -1)
			return null;
		else
			return memory.get(noteIndex);
	}

	public synchronized String identifyWhatUserIsPlaying() {
		int size = memory.size();
		String text;

		if (size == 0) // if no notes are being played
			text = "no notes";
		else if (size == 1) // if one note
			text = "note: " + MusicTheory.noteFromMIDI(memory.get(0).getPitch());
		else if (size == 2) // if an interval
			text = "interval: " + MusicTheory.identifyIntervalFromMIDI(memory.get(0).getPitch(), memory.get(1).getPitch());
		else { // if a chord
			int[] notes = this.getNoteArray();
			Chord c = MusicTheory.identifyChordFromMIDI(notes);

			if (c != null)
				text = "chord: " + c;
			else
				text = "intervals: " + MusicTheory.identifyIntervalFromMIDI(notes);
		}
		
		return text;
	}
}