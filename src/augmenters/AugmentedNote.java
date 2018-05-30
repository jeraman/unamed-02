package augmenters;

import java.util.ArrayList;

import ddf.minim.UGen;
import effects.Effect;
import generators.Generator;
import util.MidiIO;

/**
 * Augments BasicNote (MIDI only) with Artificial Notes, Generators and Effects.
 * 
 * @author jeraman.info
 *
 */

public class AugmentedNote extends BasicNote {
	private ArtificialNotes artificialNotes;
	private ArrayList<Generator> generators;
	private ArrayList<Effect> effects;
	// private Effect effect;

	public AugmentedNote(int channel, int pitch, int velocity) {
		this(channel, pitch, velocity, new ArrayList<Generator>());
	}

	public AugmentedNote(int channel, int pitch, int velocity, ArrayList<Generator> generators) {
		this(channel, pitch, velocity, generators, new ArrayList<Effect>());
	}

	// public AugmentedNote(int channel, int pitch, int velocity,
	// ArrayList<Generator> generators, Effect effect) {
	// super(channel, pitch, velocity);
	// this.artificialNotes = new ArtificialNotes();
	// this.generators = new ArrayList<Generator>();
	// this.generators.add(generator);
	// this.effect = effect;
	// }

	public AugmentedNote(int channel, int pitch, int velocity, ArrayList<Generator> generators,
			ArrayList<Effect> effects) {
		super(channel, pitch, velocity);
		this.artificialNotes = new ArtificialNotes();
		this.generators = generators;
		this.effects = effects;
	}

	protected boolean isPitchEquals(int wantedPitch) {
		return (this.isNoteEquals(wantedPitch));
	}

	protected ArtificialNotes getArtificialNotes() {
		return artificialNotes;
	}

	public boolean thereIsAGenerator() {
		return (this.generators.size() > 0);
	}

	public boolean thereIsAEffect() {
		return (this.effects.size() > 0);
	}

	public void patchEffects() {
		if (thereIsAEffect() && thereIsAGenerator())
			for (Generator g : generators)
				for (Effect e : effects)
					g.patchEffect((UGen) e.clone());
	}

	public void unpatchEffects() {
		if (thereIsAEffect() && thereIsAGenerator())
			for (Generator g : generators)
				for (Effect e : effects)
					g.unpatchEffect((UGen) e);
	}

	public void noteOn() {
		this.patchEffects();

		this.artificialNotes.noteOn();

		if (this.thereIsAGenerator())
			for (Generator g : generators)
				g.noteOn();
		else
			MidiIO.outputNoteOn(this.getChannel(), this.getPitch(), this.getVelocity());

	}

	public void noteOff() {
		this.unpatchEffects();

		this.artificialNotes.noteOff();

		if (this.thereIsAGenerator())
			for (Generator g : generators)
				g.noteOff();
		else
			MidiIO.outputNoteOff(this.getChannel(), this.getPitch(), this.getVelocity());

	}
	
	protected AugmentedNote cloneInADifferentPitch(int newNotePitch) {
		return new AugmentedNote(this.getChannel(), newNotePitch, this.getVelocity(), cloneGenerators(newNotePitch),
				cloneEffects());
	}

	public ArrayList<Generator> getGenerators() {
		return generators;
	}

	public void close() {
		for (Generator g : generators)
			g.close();
		this.generators.clear();
		this.generators = null;
		this.artificialNotes.close();
		this.artificialNotes = null;
	}

	/////////////////////////////
	// augmenters methods
	/////////////////////////////
	public void addArtificialNote(int newNotePitch) {
		this.artificialNotes.addArtificialNote(this, newNotePitch);
	}

	public void addArtificialInterval(String intervalType) {
		this.artificialNotes.addArtificialInterval(this, intervalType);
	}

	public void addArtificialChord(String chordType) {
		this.artificialNotes.addArtificialChord(this, chordType);
	}

	public void addArtificialInterval(int newPitch, String intervalType) {
		this.artificialNotes.addArtificialInterval(this, newPitch, intervalType);
	}

	public void addArtificialChord(int newRoot, String chordType) {
		this.artificialNotes.addArtificialChord(this, newRoot, chordType);
	}

	/////////////////////////////
	// generators methods
	/////////////////////////////
	public void addGenerator(Generator g) {
		this.generators.add(g);
	}
	
	private ArrayList<Generator> cloneGenerators(int newNotePitch) {
		ArrayList<Generator> gens = new ArrayList<Generator>();
		
		if (this.thereIsAGenerator())
			for (Generator g : generators)
				gens.add(g.cloneInADifferentPitch(newNotePitch));
		return gens;
	}
	
	/////////////////////////////
	// effects methods
	/////////////////////////////
	public void addEffect(Effect e) {
		this.effects.add(e);
	}

	private ArrayList<Effect> cloneEffects() {
		ArrayList<Effect> fxs = new ArrayList<Effect>();
		
		if (this.thereIsAEffect())
			for (Effect e : effects)
				fxs.add(e.clone());

		return fxs;
	}
}