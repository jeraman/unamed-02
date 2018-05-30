package augmenters;

import java.util.ArrayList;

import ddf.minim.UGen;
import effects.AdsrEffect;
import effects.Effect;
import generators.Generator;
import util.MidiIO;
import util.Util;

/**
 * Augments BasicNote (MIDI only) with Artificial Notes, Generators and Effects.
 * 
 * @author jeraman.info
 *
 */

public class AugmentedNote extends BasicNote implements Runnable {
	private ArtificialNotes artificialNotes;
	private ArrayList<Generator> generators;
	private ArrayList<Effect> effects;
	// private Effect effect;

	private boolean containsADSR;
	private boolean closed;
	private ArrayList<Effect> clonedFxs;

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

		this.containsADSR = this.checkIfContainsADSREffect();
		this.clonedFxs = new ArrayList<Effect>();
		this.closed = false;
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
				for (Effect e : effects) {
					Effect clonedFx = e.clone();
					g.patchEffect((UGen) clonedFx);
					clonedFxs.add(clonedFx);
				}
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

		if (this.containsADSR)
			noteOffUsingADSR();

		else
			defaultNoteOff();
	}

	public void noteOffUsingADSR() {
		System.out.println("need to note off using ADSR!");

		// for (int i = clonedFxs.size()-1; i > 0; i--) {
		// Effect e = clonedFxs.get(i);
		// if (e instanceof AdsrEffect)
		// ((AdsrEffect) e).noteOff();
		// clonedFxs.remove(i);
		// }
		
		this.artificialNotes.noteOffUsingADSR();
		
		for (Effect e : clonedFxs)
			if (e instanceof AdsrEffect)
				((AdsrEffect) e).noteOff();

		// setting a time to note off everything!
		Runnable r = this;
		new Thread(r).start();
	}

	private boolean checkIfContainsADSREffect() {
		for (Effect e : effects)
			if (e instanceof AdsrEffect)
				return true;
		return false;
	}

	private int getLongestReleaseTime() {
		float longestReleaseTime = 0;

		for (Effect e : clonedFxs)

			if (e instanceof AdsrEffect && ((AdsrEffect) e).relTime > longestReleaseTime)
				longestReleaseTime = ((AdsrEffect) e).relTime;

		return (int) longestReleaseTime * 1000;
	}


	public void run() {
		int longestReleaseTime = getLongestReleaseTime();
		Util.delay(longestReleaseTime);
		System.out.println("ok to fully note off!");
		this.defaultNoteOff();
	}

	public synchronized void defaultNoteOff() {
		if (this.closed) return;
		
		this.unpatchEffects();
		this.artificialNotes.noteOff();

		if (this.thereIsAGenerator())
			for (Generator g : generators)
				g.noteOff();
		else
			MidiIO.outputNoteOff(this.getChannel(), this.getPitch(), this.getVelocity());

		this.close();
	}

	protected AugmentedNote cloneInADifferentPitch(int newNotePitch) {
		return new AugmentedNote(this.getChannel(), newNotePitch, this.getVelocity(), cloneGenerators(newNotePitch),
				cloneEffects());
	}

	public ArrayList<Generator> getGenerators() {
		return generators;
	}

	public void close() {
		if (this.closed) return; 
		
		for (Generator g : generators)
			g.close();
		this.generators.clear();
		this.effects.clear();
		this.clonedFxs.clear();

		this.generators = null;
		this.effects = null;
		this.clonedFxs = null;

		this.artificialNotes.close();
		this.artificialNotes = null;
		
		this.closed = true;
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
		if (e instanceof AdsrEffect)
			this.containsADSR = true;
	}

	private ArrayList<Effect> cloneEffects() {
		ArrayList<Effect> fxs = new ArrayList<Effect>();

		if (this.thereIsAEffect())
			for (Effect e : effects)
				fxs.add(e.clone());

		return fxs;
	}
}