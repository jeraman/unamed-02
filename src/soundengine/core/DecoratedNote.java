package soundengine.core;

import java.util.ArrayList;

import ddf.minim.UGen;
import ddf.minim.ugens.Summer;
import soundengine.SoundEngine;
import soundengine.augmenters.Augmenter;
import soundengine.effects.AdsrEffect;
import soundengine.effects.Effect;
import soundengine.generators.Generator;
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
	private ArrayList<Generator> generators;
	private ArrayList<Effect> effects;
	private Summer mixer;
	private UGen outputChain;

	private boolean containsADSR;
	private boolean closed;

	public DecoratedNote(int channel, int pitch, int velocity) {
		this(channel, pitch, velocity, new ArrayList<Generator>());
	}

	public DecoratedNote(int channel, int pitch, int velocity, ArrayList<Generator> generators) {
		this(channel, pitch, velocity, generators, new ArrayList<Effect>());
	}

	public DecoratedNote(int channel, int pitch, int velocity, ArrayList<Generator> generators,
			ArrayList<Effect> effects) {
		super(channel, pitch, velocity);
		this.artificialNotes = new ArtificialNotes();
		this.generators = generators;
		this.effects = effects;

		this.mixer = new Summer();
		this.outputChain = this.mixer;

		this.containsADSR = this.checkIfContainsADSREffect();
		this.closed = false;
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
			for (Generator g : generators)
				g.patchEffect(s);
	}

	private void loadUpAllGenerators() {
		this.loadUpAllGenerators(this.mixer);
	}

	public void patchEffects() {
		this.loadUpAllGenerators();

		if (thereIsAEffect())
			for (Effect e : effects) 
				this.outputChain = this.outputChain.patch((UGen) e);
	}
	
	public synchronized void unpatchEffects() {
		if(this.closed)
			return;
		
		synchronized(effects) {
		if (thereIsAEffect())
			for (Effect e : effects) {
				outputChain.unpatch((UGen) e);
				mixer.unpatch((UGen) e);
			}
		}

		synchronized(generators) {
			if (thereIsAGenerator()) {
				for (Generator g : generators)
					g.unpatchEffect(mixer);
			}
		}

	}

	public void noteOn() {
		this.patchEffects();

		if (!this.thereIsAGenerator()) {
			this.artificialNotes.noteOn();
			MidiIO.outputNoteOn(this.getChannel(), this.getPitch(), this.getVelocity());
		} else
			this.outputChain.patch(SoundEngine.out);

	}

	public void noteOff() {

		if (this.containsADSR)
			noteOffUsingADSR();

		else
			defaultNoteOff();
	}

	public void noteOffUsingADSR() {
		System.out.println("need to note off using ADSR!");

		this.artificialNotes.noteOffUsingADSR();

		for (Effect e : effects)
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
		for (Effect e : effects)
			if (e instanceof AdsrEffect)
				return true;
		return false;
	}

	private int getLongestReleaseTime() {
		float longestReleaseTime = 0;

		// for (Effect e : clonedFxs)
		for (Effect e : effects)

			if (e instanceof AdsrEffect && ((AdsrEffect)e).getRelTime() > longestReleaseTime)
				longestReleaseTime = ((AdsrEffect) e).getRelTime();

		return (int) longestReleaseTime * 1000;
	}

	public void run() {
		int longestReleaseTime = getLongestReleaseTime();
		Util.delay(longestReleaseTime);
		System.out.println("ok to fully note off: " + this);
		this.defaultNoteOff();
	}

	
	protected DecoratedNote cloneInADifferentPitch(int newNotePitch) {
		return new DecoratedNote(this.getChannel(), newNotePitch, this.getVelocity(), this.cloneGenerators(newNotePitch),
				this.cloneEffects());
	}

	public ArrayList<Generator> getGenerators() {
		return generators;
	}

	public void close() {
		if (this.closed)
			return;

		this.closed = true;

		synchronized (generators) {
			for (Generator g : generators)
				g.close();
			this.generators.clear();
			this.generators = null;
		}
		
		synchronized (effects) {
			for (Effect e : effects)
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
	@Deprecated
	public void addArtificialNote(int newNotePitch) {
		this.artificialNotes.addArtificialNote(this, newNotePitch);
	}

	@Deprecated
	public void addArtificialInterval(String intervalType) {
		this.artificialNotes.addArtificialInterval(this, intervalType);
	}

	@Deprecated
	public void addArtificialChord(String chordType) {
		this.artificialNotes.addArtificialChord(this, chordType);
	}

	@Deprecated
	public void addArtificialInterval(int newPitch, String intervalType) {
		this.artificialNotes.addArtificialInterval(this, newPitch, intervalType);
	}
	
	@Deprecated
	public void addArtificialChord(int newRoot, String chordType) {
		this.artificialNotes.addArtificialChord(this, newRoot, chordType);
	}
	
	public void addAugmenter(Augmenter aug) {
		this.artificialNotes.addAugmenter(this, aug);
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
				gens.add(g.cloneWithPitch(newNotePitch));
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