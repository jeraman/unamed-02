package soundengine.core;

import java.io.Serializable;
import java.util.ArrayList;

import ddf.minim.UGen;
import ddf.minim.ugens.Summer;
import soundengine.SoundEngine;
import soundengine.augmenters.AbstractAugmenter;
import soundengine.effects.AdsrEffect;
import soundengine.effects.AbstractEffect;
import soundengine.generators.AbstractGenerator;
import soundengine.generators.GeneratorFactory;
import soundengine.util.MidiIO;
import soundengine.util.Util;

/**
 * Augments BasicNote (MIDI only) with Artificial Notes, Generators and Effects.
 * 
 * @author jeraman.info
 *
 */

public class DecoratedNote extends BasicNote implements Runnable {
	private ArtificialNotes artificialNotes;
	private ArrayList<AbstractGenerator> generators;
	private ArrayList<AbstractEffect> effects;
	private Summer mixer;
	private UGen outputChain;
	private AdsrEffect envelope;

	private boolean containsADSR;
	private boolean closed;

	private static final float fadeOutTime = 0.01f;

	public DecoratedNote(int channel, int pitch, int velocity) {
		this(channel, pitch, velocity, new ArrayList<AbstractGenerator>());
	}

	public DecoratedNote(int channel, int pitch, int velocity, ArrayList<AbstractGenerator> generators) {
		this(channel, pitch, velocity, generators, new ArrayList<AbstractEffect>());
	}

	public DecoratedNote(int channel, int pitch, int velocity, ArrayList<AbstractGenerator> generators,
			ArrayList<AbstractEffect> effects) {
		super(channel, pitch, velocity);
		this.artificialNotes = new ArtificialNotes();
		this.generators = generators;
		this.effects = effects;

		this.mixer = new Summer();
		this.outputChain = this.mixer;

		this.containsADSR = this.checkIfContainsADSREffect();
		this.closed = false;

		this.envelope = new AdsrEffect(1f, 0.001f, 1f, 1f, fadeOutTime, 0f, 0f);
	}

	public boolean isPitchEquals(int wantedPitch) {
		return (this.isNoteEquals(wantedPitch));
	}

	protected ArtificialNotes getArtificialNotes() {
		return artificialNotes;
	}

	public boolean thereIsAGenerator() {
		return (this.generators != null && this.generators.size() > 0);
	}

	public boolean thereIsAEffect() {
		return (this.effects != null || this.effects.size() > 0);
	}

	public void loadUpAllGenerators(Summer s) {
		this.artificialNotes.loadUpAllGenerators(s);

		if (thereIsAGenerator())
			for (AbstractGenerator g : generators)
				g.patchEffect(s);
	}

	private void loadUpAllGenerators() {
		this.loadUpAllGenerators(this.mixer);
	}

	public void patchEffects() {
		this.loadUpAllGenerators();

		if (thereIsAEffect())
			for (AbstractEffect e : effects)
				this.outputChain = this.outputChain.patch((UGen) e);
	}

	public synchronized void unpatchEffects() {
		if (this.closed)
			return;

		synchronized (effects) {
			if (thereIsAEffect())
				for (AbstractEffect e : effects) {
					outputChain.unpatch((UGen) e);
					mixer.unpatch((UGen) e);
				}
		}

		synchronized (generators) {
			if (thereIsAGenerator()) {
				for (AbstractGenerator g : generators)
					g.unpatchEffect(mixer);
			}
		}

	}

	/*
	 * public void removeGlitchKillerToEffects() {
	 * this.effects.remove(this.envelope); }
	 */

	public void noteOn() {
		this.addGlitchKillerToEffects();
		this.patchEffects();

		if (!this.thereIsAGenerator()) {
			this.artificialNotes.noteOn();

			MidiIO.outputNoteOn(this.getChannel(), this.getPitch(), this.getVelocity());
		} else
			this.outputChain.patch(SoundEngine.out);

	}

	public void addGlitchKillerToEffects() {
		if (!this.containsADSR)
			this.addEffect(this.envelope);
	}

	public void noteOff() {

		if (this.containsADSR)
			noteOffUsingADSR();

		else
			defaultNoteOff();
	}

	public synchronized void noteOffUsingADSR() {
		// System.out.println("need to note off using ADSR!");

		this.artificialNotes.noteOffUsingADSR();

		for (AbstractEffect e : effects)
			if (e instanceof AdsrEffect)
				((AdsrEffect) e).noteOff();

		// setting a time to note off everything!
		Runnable r = this;
		new Thread(r).start();
	}

	public synchronized void defaultNoteOff() {
		if (this.closed)
			return;

		this.unpatchEffects();

		if (!this.thereIsAGenerator()) {
			if (this.artificialNotes != null)
				this.artificialNotes.noteOff();
			MidiIO.outputNoteOff(this.getChannel(), this.getPitch(), this.getVelocity());
		} else {
			this.outputChain.unpatch(SoundEngine.out);
			this.mixer.unpatch(SoundEngine.out);
		}

		this.close();
	}

	private boolean checkIfContainsADSREffect() {
		for (AbstractEffect e : effects)
			if (e instanceof AdsrEffect)
				return true;
		return false;
	}

	private synchronized float getLongestReleaseTime() {
		float longestReleaseTime = 0;

		// for (Effect e : clonedFxs)
		synchronized (effects) {
		for (AbstractEffect e : effects)
			if (e instanceof AdsrEffect && ((AdsrEffect) e).getRelTime() > longestReleaseTime)
				longestReleaseTime = ((AdsrEffect) e).getRelTime();
		}
		
		return longestReleaseTime * 1000;
	}

	public void run() {
		float longestReleaseTime = getLongestReleaseTime();
		Util.delay((int) longestReleaseTime);
//		System.out.println("ok to fully note off: " + this);
		this.defaultNoteOff();
	}

	// @Deprecated
	// public DecoratedNote cloneInADifferentPitchAndVelocity(int newNotePitch,
	// int newVelocity) {
	// return new DecoratedNote(this.getChannel(), newNotePitch, newVelocity,
	// this.cloneGenerators(newNotePitch, newVelocity),
	// this.cloneEffects());
	// }

	public DecoratedNote cloneInADifferentPitchAndVelocityAndDuration(int newNotePitch, int newVelocity,
			int newDuration) {
		return new DecoratedNote(this.getChannel(), newNotePitch, newVelocity,
				this.cloneGenerators(newNotePitch, newVelocity, newDuration), this.cloneEffects());
	}

	// @Deprecated
	// protected DecoratedNote cloneInADifferentPitch(int newNotePitch) {
	// return new DecoratedNote(this.getChannel(), newNotePitch,
	// this.getVelocity(), this.cloneGenerators(newNotePitch),
	// this.cloneEffects());
	// }

	public ArrayList<AbstractGenerator> getGenerators() {
		return generators;
	}

	public void close() {
		if (this.closed)
			return;

		this.closed = true;

		synchronized (generators) {
			for (AbstractGenerator g : generators)
				g.close();
			this.generators.clear();
			this.generators = null;
		}

		synchronized (effects) {
			for (AbstractEffect e : effects)
				e.close();
			this.effects.clear();
			this.effects = null;
		}

		synchronized (artificialNotes) {
			this.artificialNotes.close();
			this.artificialNotes = null;
		}

	}

	/////////////////////////////
	// augmenters methods
	/////////////////////////////
	public void addAugmenter(AbstractAugmenter aug) {
		this.artificialNotes.addAugmenter(this, aug);
	}

	/////////////////////////////
	// generators methods
	/////////////////////////////
	public void addGenerator(AbstractGenerator g) {
		this.generators.add(g);
	}

	// @Deprecated
	// private ArrayList<AbstractGenerator> cloneGenerators(int newNotePitch,
	// int newVelocity) {
	// ArrayList<AbstractGenerator> gens = new ArrayList<AbstractGenerator>();
	//
	// if (this.thereIsAGenerator())
	// for (AbstractGenerator g : generators)
	// gens.add(g.clone(newNotePitch, newVelocity));
	//
	// // gens.add(g.cloneWithPitchAndVelocity(newNotePitch, newVelocity));
	// return gens;
	// }

	private ArrayList<AbstractGenerator> cloneGenerators(int newNotePitch, int newVelocity, int newDuration) {
		ArrayList<AbstractGenerator> gens = new ArrayList<AbstractGenerator>();

		if (this.thereIsAGenerator())
			for (AbstractGenerator g : generators)
				gens.add(g.cloneWithNewPitchVelocityAndDuration(newNotePitch, newVelocity, newDuration));
		return gens;
	}

	// @Deprecated
	// private ArrayList<AbstractGenerator> cloneGenerators(int newNotePitch) {
	// ArrayList<AbstractGenerator> gens = new ArrayList<AbstractGenerator>();
	//
	// if (this.thereIsAGenerator())
	// for (AbstractGenerator g : generators)
	// gens.add(g.cloneWithPitch(newNotePitch));
	// return gens;
	// }

	public void addEffect(AbstractEffect e) {
		this.effects.add(e);
		if (e instanceof AdsrEffect) {
			this.containsADSR = true;
			if (e != this.envelope)
				this.removeEnvelope();
		}
	}

	private void removeEnvelope() {
		this.effects.remove(this.envelope);
	}

	private ArrayList<AbstractEffect> cloneEffects() {
		ArrayList<AbstractEffect> fxs = new ArrayList<AbstractEffect>();

		if (this.thereIsAEffect())
			for (AbstractEffect e : effects)
				fxs.add(e.clone());

		return fxs;
	}
}