package musicalTasksTest;

import java.util.ArrayList;
import org.jfugue.theory.Chord;

public class StoredNoteMemory {
	private ArrayList<StoredNote> memory;
	private ArrayList<Integer> removalLine;
	private static final int CONCURRENT_NOTES_LIMIT = 5;

	public StoredNoteMemory() {
		// memory = new CopyOnWriteArrayList<StoredNote>();
		memory = new ArrayList<StoredNote>();
		removalLine = new ArrayList<Integer>();
	}
	
	public void put(int channel, int note, int velocity) {
		this.put(channel, note, velocity, null);
	}

	public void put(int channel, int note, int velocity, Generator g) {
		this.put(channel, note, velocity, new ArrayList<Integer>(), g);
	}

	public void put(int channel, int note, int velocity, ArrayList<Integer> artificialNotes, Generator g) {
		controlsNumberOfConcurrentNotes();
		memory.add(new StoredNote(channel, note, velocity, artificialNotes, g));
	}

	public StoredNote remove(int note) {
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
			this.removeAndKillNote(note);
	}
	
	private StoredNote removeAndKillNote(int note) {
		StoredNote n = this.remove(note);
		GeneratorFactory.noteOffGen(n.getGenerator());
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
			this.removeAndKillNote(memory.get(0).getNote());
		}
	}

	public int size() {
		return memory.size();
	}

	private int getElementIndex(int wantedNote) {
		int result = -1;
		for (int i = 0; i < memory.size(); i++)
			if (memory.get(i).isNoteEquals(wantedNote))
				result = i;
		return result;
	}

	public int[] getNoteArray() {
		int[] individualNotes = new int[memory.size()];
		for (int i = 0; i < memory.size(); i++)
			individualNotes[i] = memory.get(i).getNote();
		return individualNotes;
	}

	public int[] getToBeDeletedArray() {
		int[] individualNotes = new int[removalLine.size()];
		for (int i = 0; i < removalLine.size(); i++)
			individualNotes[i] = removalLine.get(i);
		return individualNotes;
	}

	public StoredNote get(int index) {
		if (index >= 0 && index < memory.size())
			return memory.get(index);
		else
			return null;
	}

	public StoredNote getStoredNotebyNoteValue(int wantedNote) {
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
			text = "note: " + MusicTheory.noteFromMIDI(memory.get(0).getNote());
		else if (size == 2) // if an interval
			text = "interval: " + MusicTheory.identifyingIntervalFromMIDI(memory.get(0).getNote(), memory.get(1).getNote());
		else { // if a chord
			int[] notes = this.getNoteArray();
			Chord c = MusicTheory.identifyingChordFromMIDI(notes);

			if (c != null)
				text = "chord: " + c;
			else
				text = "intervals: " + MusicTheory.identifyingIntervalFromMIDI(notes);
		}

		return text;

	}
}