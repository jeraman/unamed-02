package augmenters;

import ddf.minim.UGen;
import effects.Effect;
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
	private Effect effect;

	public AugmentedNote(int channel, int pitch, int velocity) {
		this(channel, pitch, velocity, null);
	}
	
	public AugmentedNote(int channel, int pitch, int velocity, Generator generator) {
		this(channel, pitch, velocity, generator, null);
	}
	
	public AugmentedNote(int channel, int pitch, int velocity, Generator generator, Effect effect) {
		super(channel, pitch, velocity);
		this.artificialNotes = new ArtificialNotes();
		this.generator = generator;	
		this.effect	   = effect;	
	}

	protected boolean isPitchEquals(int wantedPitch) {
		return (this.isNoteEquals(wantedPitch));
	}

	protected ArtificialNotes getArtificialNotes() {
		return artificialNotes;
	}
	
	
	public void patchEffects() {
		if (this.effect != null && this.generator != null)
			this.generator.patchEffect((UGen)effect);
	}
	
	public void unpatchEffects() {
		if (this.effect != null && this.generator != null)
			this.generator.unpatchEffect((UGen)effect);
	}
	
	
	public void noteOn() {
		this.patchEffects();
		
		this.artificialNotes.noteOn();

		if (this.generator == null)
			MidiIO.outputNoteOn(this.getChannel(), this.getPitch(), this.getVelocity());
		else
			this.generator.noteOn();
		
	}

	public void noteOff() {
		this.unpatchEffects();
		
		this.artificialNotes.noteOff();

		if (this.generator == null)
			MidiIO.outputNoteOff(this.getChannel(), this.getPitch(), this.getVelocity());
		else 
			this.generator.noteOff();
		
	}
	
	public void addArtificialNote (int newNotePitch) {
		this.artificialNotes.addArtificialNote(this, newNotePitch);
	}
	
	public void addArtificialInterval (String intervalType) {
		this.artificialNotes.addArtificialInterval(this, intervalType);
	}
	
	public void addArtificialChord (String chordType) {
		this.artificialNotes.addArtificialChord(this, chordType);
	}
		
	public void addArtificialInterval (int newPitch, String intervalType) {
		this.artificialNotes.addArtificialInterval(this, newPitch, intervalType);
	}
	
	public void addArtificialChord (int newRoot, String chordType) {
		this.artificialNotes.addArtificialChord(this, newRoot, chordType);
	}
	
	private Generator cloneGenerator(int newNotePitch) {
		Generator gen = null;
		if (this.generator!=null)
			gen = this.generator.cloneInADifferentPitch(newNotePitch);
		return gen;
	}
	
	private Effect cloneEffect() {
		if(this.effect==null) 
			return null;
		else
			return this.effect.clone();
	}

	protected AugmentedNote cloneInADifferentPitch(int newNotePitch) {
		return new AugmentedNote(this.getChannel(), newNotePitch, this.getVelocity(), cloneGenerator(newNotePitch), cloneEffect());
		//return new AugmentedNote(this.getChannel(), newNotePitch, this.getVelocity(), cloneGenerator(newNotePitch));
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