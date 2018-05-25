package augmenters;

import java.util.ArrayList;
import org.jfugue.theory.Chord;

import generators.Generator;
import generators.GeneratorFactory;

public class AugmentedNoteMemory {
	private ArrayList<AugmentedNote> memory;
	private ArrayList<Integer> removalLine;
	
	private static final int CONCURRENT_NOTES_LIMIT = 5;

	public AugmentedNoteMemory() {
		memory = new ArrayList<AugmentedNote>();
		removalLine = new ArrayList<Integer>();
	}
	
	public void put(int channel, int note, int velocity) {
		this.put(channel, note, velocity, null);
	}

	public void put(int channel, int note, int velocity, Generator g) {
//		this.put(channel, note, velocity, new ArtificialNotes(), g);
		AugmentedNote newNote =new AugmentedNote(channel, note, velocity, g);
		this.put(newNote);
	}
	
	public void put (AugmentedNote aug) {		
		controlsNumberOfConcurrentNotes();
		memory.add(aug);
	}

//	public void put(int channel, int note, int velocity, ArtificialNotes artificialNotes, Generator g) {
//		controlsNumberOfConcurrentNotes();
//		memory.add(new AugmentedNote(channel, note, velocity, artificialNotes, g));
//	}

	public AugmentedNote remove(int note) {
		int noteIndex = getElementIndex(note);

		if (noteIndex < 0) {
			System.out.println("adding: " + note +  " to the removal line");
			System.out.println("memory.size(): " + memory.size());
			System.out.println("memory: " + memory);
			
			if (!isQueuedToBeDeleted(note))
				removalLine.add(note);
			
			return null;
		}

		return memory.remove(noteIndex);
	}
	
	public void update() {
		tryToClearMemory();
	}
	
	public void tryToClearMemory() {
		for (int note:removalLine) 
			this.removeAndNoteOff(note);
	}
	
	private AugmentedNote removeAndNoteOff(int note) {
		AugmentedNote n = this.remove(note);
		//GeneratorFactory.noteOff(n.getGenerator());
		n.noteOff();
		System.out.println("removing and killing note " + note);
		return n;
	}
	
	public boolean isQueuedToBeDeleted (int note) {
		return removalLine.contains(note);
	}
	

	private void controlsNumberOfConcurrentNotes() {
		if (isBiggerThanNoteLimit())
			removesOldestNote();
	}

	private boolean isBiggerThanNoteLimit() {
		return (this.size() > CONCURRENT_NOTES_LIMIT);
	}

	private void removesOldestNote() {
		if (size() > 0) {
			this.removeAndNoteOff(memory.get(0).getPitch());
		}
	}

	public int size() {
		return memory.size();
	}

	private int getElementIndex(int wantedNote) {
		int result = -1;
		for (int i = 0; i < memory.size(); i++)
			if (memory.get(i).isPitchEquals(wantedNote))
				result = i;
		return result;
	}

	public int[] getNoteArray() {
		int[] individualNotes = new int[memory.size()];
		for (int i = 0; i < memory.size(); i++)
			individualNotes[i] = memory.get(i).getPitch();
		return individualNotes;
	}

	public int[] getToBeDeletedArray() {
		int[] individualNotes = new int[removalLine.size()];
		for (int i = 0; i < removalLine.size(); i++)
			individualNotes[i] = removalLine.get(i);
		return individualNotes;
	}

	public AugmentedNote get(int index) {
		if (index >= 0 && index < memory.size())
			return memory.get(index);
		else
			return null;
	}

	public AugmentedNote getStoredNotebyNoteValue(int wantedNote) {
		int noteIndex = getElementIndex(wantedNote);

		if (noteIndex == -1)
			return null;
		else
			return memory.get(noteIndex);
	}

	public String identifyWhatUserIsPlaying() {
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