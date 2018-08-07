package soundengine.util;

import org.jfugue.theory.*;
import java.util.List;
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

	public static float freqFromMIDI(int note) {
		if (note <=0)
			return -1;
		else
			return (float)Note.getFrequencyForNote(note);
	}

	public static float freqFromNote(String note) {
		return (float) Note.getFrequencyForNote(note);
	}

	// details on: http://www.jfugue.org/doc/org/jfugue/theory/Chord.html
	public static Chord identifyChordFromMIDI(int[] individualNotes) {
		String[] formatedNotes = new String[individualNotes.length];

		for (int i = 0; i < individualNotes.length; i++)
			formatedNotes[i] = noteFromMIDI(individualNotes[i]);

		return identifyChordFromMIDI(formatedNotes);
	}

	// details on: http://www.jfugue.org/doc/org/jfugue/theory/Chord.html
	private static Chord identifyChordFromMIDI(String[] individualNotes) {

		//System.out.println("Incoming notes are: ");
		//System.out.println(individualNotes);

		Chord result = null;
		try {
			// this method can return null if no chord is detected
			result = Chord.fromNotes(individualNotes);
			//System.out.print("Chord detected: ");
			//System.out.println(result);
		} catch (Exception e) {
//			System.out.println("Unable to detect this chord");
		}

		return result;
	}

	// see details on: http://www.jfugue.org/doc/org/jfugue/theory/Intervals.html
	public static Intervals identifyIntervalFromMIDI(int midiNote1, int midiNote2) {
		String[] notes = new String[2];
		notes[0] = noteFromMIDI(midiNote1);
		notes[1] = noteFromMIDI(midiNote2);

		return identifyIntervalFromMIDI(notes);
	}

	public static Intervals identifyIntervalFromMIDI(int[] notes) {
		Note[] formatedNotes = new Note[notes.length];

		for (int i = 0; i < notes.length; i++)
			formatedNotes[i] = new Note(notes[i]);

		return identifyIntervalFromMIDI(formatedNotes);
	}

	private static Intervals identifyIntervalFromMIDI(String[] notes) {
		Note[] formatedNotes = new Note[notes.length];

		for (int i = 0; i < notes.length; i++)
			formatedNotes[i] = new Note(notes[i]);

		return identifyIntervalFromMIDI(formatedNotes);
	}

	private static Intervals identifyIntervalFromMIDI(Note[] notes) {
//		System.out.println("Incoming notes are: ");
//		System.out.println(notes);

//		System.out.println("Interval detected: ");
//		System.out.println(Intervals.createIntervalsFromNotes(notes));

		return Intervals.createIntervalsFromNotes(notes);
	}
	
	public static Note[] generateInterval(int pitch, int velocity, String intervalType) {
		Intervals i = new Intervals("1 " + intervalType);
		i.setRoot(new Note(pitch));
		List<Note> notes = i.getNotes();
		Note[] result = new Note[notes.size()];
		result = notes.toArray(result);
		for (Note n : result)
			n.setOnVelocity(Util.parseIntToByte(velocity));
		return result;
	}
	

	// details on: http://www.jfugue.org/doc/org/jfugue/theory/Chord.html
	public static Note[] generateChordFromMIDI(int rootMIDI, int velocity, String chordType) {
		String note = MusicTheory.noteFromMIDI(rootMIDI);

//		System.out.println("The chord to be generated is:");
		String resultingChord = note + chordType;
//		System.out.println(resultingChord);

		Chord chord = new Chord(resultingChord);
		Note[] notes = chord.getNotes();
		
//		System.out.println("Notes from this chord are:");
		for (Note n : notes) {
			n.setOnVelocity(Util.parseIntToByte(velocity));
//			System.out.print(n + " ");
		}
		
//		System.out.println();
		return notes;
	}


	// details on: http://www.jfugue.org/doc/index.html?org/jfugue/theory/ChordProgression.html
	protected static ChordProgression generateChordProgression(int rootMIDI, String chordProgression) {
		// important: letter case represents if the chord if major or minor. for
		// example:
		// all major
		// String chordProgression = "I IV V";
		// two majors (I and V) and two minors (vi and ii)
		// String chordProgression = "I vi ii V";
		// it' possible to add some modifiers such as 7th chords (7), or
		// diminished (d)
		// String chordProgression = "I vi ii V7 VIId";

		String myKey = Note.getToneString((byte) rootMIDI);

		ChordProgression cp = new ChordProgression(chordProgression);
//		Chord[] chords = cp.setKey(myKey).getChords();
//		for (Chord chord : chords) {
//			System.out.print("Chord " + chord + " has these notes: ");
//			Note[] notes = chord.getNotes();
//			for (Note n : notes)
//				System.out.print(n + " ");
//			System.out.println();
//		}

		return cp;
	}
}
