package musicalTasksTest;

import java.util.ArrayList;

public class StoredNote {
	private int note;
	private int velocity;
	private int channel;
	private ArrayList<Integer> artificialNote;
	private Generator generator;

	
	public StoredNote(int channel, int note, int velocity) {
		this(channel, note, velocity, null, null);
	}
	
	public StoredNote(int channel, int note, int velocity, ArrayList<Integer> artificialNote) {
		this(channel, note, velocity, artificialNote, null);
	}
	
	public StoredNote(int channel, int note, int velocity, ArrayList<Integer> artificialNote, Generator generator) {
		this.channel = channel;
		this.note = note;
		this.velocity = velocity;
		this.artificialNote = artificialNote;
		this.generator = generator;
	}

	int getNote() {
		return note;
	}

	boolean isNoteEquals(int wantedNote) {
		return (this.note == wantedNote);
	}

	ArrayList<Integer> getArtificialNotes() {
		return artificialNote;
	}

	void noteOn() {
		if (this.generator != null)
			this.generator.noteOn();
		else
			MidiIO.outputNoteOn(this.channel, this.note, this.velocity);
	}

	void noteOff() {
		if (this.generator != null)
			this.generator.noteOff();
		else
			MidiIO.outputNoteOff(this.channel, this.note, this.velocity);
	}
	
	public Generator getGenerator() {
		return generator;
	}
}