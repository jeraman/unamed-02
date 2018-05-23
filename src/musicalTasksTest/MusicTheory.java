package musicalTasksTest;

import org.jfugue.theory.*;
import java.util.Random;

/**
 * This classes encapsulates jFugue (http://www.jfugue.org/) for music theory methods.
 * @author jeraman.info
 * @date May 23 2018
 */
public class MusicTheory {

	// details on: http://www.jfugue.org/doc/org/jfugue/theory/Note.html
	public static String noteFromMIDI(int note) {
		return Note.getToneString((byte) note);
	}

	public static int midiFromNote(String note) {
		return (new Note(note)).getValue();
	}

	public static double freqFromMIDI(int note) {
		return Note.getFrequencyForNote(note);
	}

	public static double freqFromNote(String note) {
		return Note.getFrequencyForNote(note);
	}

	// details on: http://www.jfugue.org/doc/org/jfugue/theory/Chord.html
	public static Chord generatingChordFromMIDI(int rootMIDI, String chordType) {
		String note = noteFromMIDI(rootMIDI);

		System.out.println("The chord to be generated is:");
		String resultingChord = note + chordType;
		System.out.println(resultingChord);

		Chord chord = new Chord(resultingChord);
		Note[] notes = chord.getNotes();

		System.out.println("Notes from this chord are:");
		for (Note n : notes)
			System.out.print(n + " ");

		System.out.println();

		return chord;
	}

	public static Chord generatingRandomChordFromMIDI(int rootMIDI) {
		String[] names = Chord.getChordNames();

		System.out.println("All possible chord types are:");
		System.out.println(names);

		Random rand = new Random();
		int randomIndex = rand.nextInt(names.length);
		return generatingChordFromMIDI(rootMIDI, names[randomIndex]);
	}

	// details on: http://www.jfugue.org/doc/org/jfugue/theory/Chord.html
	public static Chord identifyingChordFromMIDI(int[] individualNotes) {
		String[] formatedNotes = new String[individualNotes.length];

		for (int i = 0; i < individualNotes.length; i++)
			formatedNotes[i] = noteFromMIDI(individualNotes[i]);

		return identifyingChordFromMIDI(formatedNotes);
	}

	// details on: http://www.jfugue.org/doc/org/jfugue/theory/Chord.html
	public static Chord identifyingChordFromMIDI(String[] individualNotes) {

		System.out.println("Incoming notes are: ");
		System.out.println(individualNotes);

		Chord result = null;
		try {
			// this method can return null if no chord is detected
			result = Chord.fromNotes(individualNotes);
			System.out.print("Chord detected: ");
			System.out.println(result);
		} catch (Exception e) {
			System.out.println("Unable to detect this chord");
		}

		return result;
	}

	// see details on:
	// http://www.jfugue.org/doc/org/jfugue/theory/Intervals.html
	public static Intervals identifyingIntervalFromMIDI(int midiNote1, int midiNote2) {
		String[] notes = new String[2];
		notes[0] = noteFromMIDI(midiNote1);
		notes[1] = noteFromMIDI(midiNote2);

		return identifyingIntervalFromMIDI(notes);
	}

	public static Intervals identifyingIntervalFromMIDI(int[] notes) {
		Note[] formatedNotes = new Note[notes.length];

		for (int i = 0; i < notes.length; i++)
			formatedNotes[i] = new Note(notes[i]);

		return identifyingIntervalFromMIDI(formatedNotes);
	}

	public static Intervals identifyingIntervalFromMIDI(String[] notes) {
		Note[] formatedNotes = new Note[notes.length];

		for (int i = 0; i < notes.length; i++)
			formatedNotes[i] = new Note(notes[i]);

		return identifyingIntervalFromMIDI(formatedNotes);
	}

	public static Intervals identifyingIntervalFromMIDI(Note[] notes) {
		System.out.println("Incoming notes are: ");
		System.out.println(notes);

		System.out.println("Interval detected: ");
		System.out.println(Intervals.createIntervalsFromNotes(notes));

		return Intervals.createIntervalsFromNotes(notes);
	}

	// details on: http://www.jfugue.org/doc/index.html?org/jfugue/theory/ChordProgression.html
	public static ChordProgression generatingChordProgression(int rootMIDI, String chordProgression) {
		// important: letter case represents if the chord if major or minor. for
		// example:
		// all major
		// String chordProgression = "I IV V";
		// two majors (I and V) and two minors (vi and ii)
		// String chordProgression = "I vi ii V";
		// it' possbile to add some modifiers such as 7th chords (7), or
		// diminished (d)
		// String chordProgression = "I vi ii V7 VIId";

		String myKey = Note.getToneString((byte) rootMIDI);

		ChordProgression cp = new ChordProgression(chordProgression);
		Chord[] chords = cp.setKey(myKey).getChords();

		for (Chord chord : chords) {
			System.out.print("Chord " + chord + " has these notes: ");
			Note[] notes = chord.getNotes();

			for (Note n : notes)
				System.out.print(n + " ");

			System.out.println();
		}

		return cp;
	}

}
