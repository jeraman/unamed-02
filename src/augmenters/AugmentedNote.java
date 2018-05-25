package augmenters;

import generators.Generator;
import util.MidiIO;

/**
 * Augments BasicNote (MIDI only) with Artificial Notes, Generators and Effects.
 * @author jeraman.info
 *
 */
		
public class AugmentedNote extends BasicNote {
	private ArtificialNotes artificialNotes;
	private Generator generator;

//	public AugmentedNote(int channel, int pitch, int velocity) {
//		this(channel, pitch, velocity, null, null);
//	}
	
	public AugmentedNote(int channel, int pitch, int velocity) {
		this(channel, pitch, velocity, null);
	}
	
	public AugmentedNote(int channel, int pitch, int velocity, Generator generator) {
		super(channel, pitch, velocity);
		this.artificialNotes = new ArtificialNotes();
		this.generator = generator;	
	}
	
//	public AugmentedNote(int channel, int pitch, int velocity, ArtificialNotes artificialNote, Generator generator) {
//		super(channel, pitch, velocity);
//		this.artificialNotes = artificialNote;
//		this.generator = generator;
//	}

	protected boolean isPitchEquals(int wantedPitch) {
		return (this.isNoteEquals(wantedPitch));
	}

	ArtificialNotes getArtificialNotes() {
		return artificialNotes;
	}

	void noteOn() {
		this.artificialNotes.noteOn();

		if (this.generator == null)
			MidiIO.outputNoteOn(this.getChannel(), this.getPitch(), this.getVelocity());
		else
			this.generator.noteOn();
		
	}

	void noteOff() {
		this.artificialNotes.noteOff();

		if (this.generator == null)
			MidiIO.outputNoteOff(this.getChannel(), this.getPitch(), this.getVelocity());
		else 
			this.generator.noteOff();
		
	}
	
	void addArtificialNote (int newNotePitch) {
		this.artificialNotes.addArtificialNote(this, newNotePitch);
	}
	
	void addArtificialInterval (String intervalType) {
		this.artificialNotes.addArtificialInterval(this, intervalType);
	}
	
	void addArtificialChord (String chordType) {
		this.artificialNotes.addArtificialChord(this, chordType);
	}
	
	private Generator cloneGenerator(int newNotePitch) {
		Generator gen = null;
		if (this.generator!=null)
			gen = this.generator.cloneInADifferentPitch(newNotePitch);
		return gen;
	}
	
	protected AugmentedNote cloneInADifferentPitch(int newNotePitch) {
		return new AugmentedNote(this.getChannel(), newNotePitch, this.getVelocity(), cloneGenerator(newNotePitch));
	}
	
	public Generator getGenerator() {
		return generator;
	}
	
	public void close() {
		this.generator.close();
		this.artificialNotes.close();
		this.artificialNotes = null;
		this.generator = null;
	}
}