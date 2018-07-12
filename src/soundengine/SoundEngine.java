package soundengine;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.spi.AudioStream;
import soundengine.augmenters.AbstractAugmenter;
import soundengine.augmenters.AugmenterFactory;
import soundengine.core.DecoratedNote;
import soundengine.core.DecoratedNoteMemory;
import soundengine.effects.AbstractEffect;
import soundengine.effects.EffectFactory;
import soundengine.generators.AbstractGenerator;
import soundengine.generators.GeneratorFactory;

/**
 * Implements sound-related services available to the UI as described on SoundEngineFacade interface
 * @author jeraman.info
 *
 */
public class SoundEngine implements SoundEngineFacade {
	
	private DecoratedNoteMemory memory;
	private LinkedHashMap<String, AbstractGenerator> activeGenerators;
	private LinkedHashMap<String, AbstractEffect> activeEffects;
	private LinkedHashMap<String, AbstractAugmenter> activeAugmenters;
	
	public static Minim minim;
	public static AudioOutput out;
	public static AudioStream in;
	
	public SoundEngine(Minim minim) {
		this.memory 		  = new DecoratedNoteMemory();
		this.activeGenerators = new LinkedHashMap<String, AbstractGenerator>();
		this.activeEffects 	  = new LinkedHashMap<String, AbstractEffect>();
		this.activeAugmenters = new LinkedHashMap<String, AbstractAugmenter>();
		
		SoundEngine.minim = minim;
		SoundEngine.out = minim.getLineOut(Minim.MONO, 256);
		SoundEngine.in  = minim.getInputStream(Minim.MONO, out.bufferSize(), out.sampleRate(),
				out.getFormat().getSampleSizeInBits());
		
		in.open();
	}
	
	public void close() {
		out.close();
		in.close();
		minim.stop();
	}
	
	public void addGenerator(String id, String type, String[] parameters) {
		AbstractGenerator gen = GeneratorFactory.createGenerator(type, parameters);
		System.out.println("inserting " + id + ","  + type + " as generator " + gen);
		this.activeGenerators.put(id, gen);
	}

	@Override
	public void updateGenerator(String id, String[] parameters) {
		AbstractGenerator gen = this.activeGenerators.get(id);
		GeneratorFactory.updateGenerator(gen, parameters);
		System.out.println("updating generator " + gen + " (id: "+  id + ") with the following parameters: "  + parameters.toString());
	}
	
	public void updateGenerator(String id, String singleParameter) {
		AbstractGenerator gen = this.activeGenerators.get(id);
		GeneratorFactory.updateGenerator(gen, singleParameter.trim());
		System.out.println("updating generator " + gen + " (id: "+  id + ") with "  + singleParameter);
	}

	@Override
	public void removeGenerator(String id) {
		AbstractGenerator gen = this.activeGenerators.remove(id);
		System.out.println("removing generator " + gen + " (id: "+  id + ")");
	}

	@Override
	public void addEffect(String id, String type, String[] parameters) {
		AbstractEffect fx = EffectFactory.createEffect(type, parameters);
		System.out.println("inserting " + id + ","  + type + " as effect " + fx);
		this.activeEffects.put(id, fx);
	}

	@Override
	public void updateEffect(String id, String[] parameters) {
		AbstractEffect fx = this.activeEffects.get(id);
		EffectFactory.updateEffect(fx, parameters);
		System.out.println("updating effect " + fx + " (id: "+  id + ") with the following parameters: "  + parameters.toString());
	}
	
	public void updateEffect(String id, String singleParameter) {
		AbstractEffect fx = this.activeEffects.get(id);
		EffectFactory.updateEffect(fx, singleParameter);
		System.out.println("updating effect " + fx +  " (id: "+  id + ") with "  + singleParameter);
	}

	@Override
	public void removeEffect(String id) {
		AbstractEffect fx = this.activeEffects.remove(id);
		System.out.println("removing effect " + fx + " (id: "+  id + ")");
	}
	
	@Override
	public void addAugmenter(String id, String type, String[] parameters) {
		AbstractAugmenter aug = AugmenterFactory.createAugmenter(type, parameters);
		System.out.println("inserting " + id + ","  + type + " as augmenter " + aug);
		this.activeAugmenters.put(id, aug);
	}

	@Override
	public void updateAugmenter(String id, String[] parameters) {
		AbstractAugmenter aug = this.activeAugmenters.get(id);
		AugmenterFactory.updateAugmenter(aug, parameters);
		System.out.println("updating augmenter " + aug + " (id: "+  id + ") with the following parameters: "  + parameters.toString());
	}
	
	@Override
	public void updateAugmenter(String id, String singleParameter) {
		AbstractAugmenter aug = this.activeAugmenters.get(id);
		AugmenterFactory.updateAugmenter(aug, singleParameter);
		System.out.println("updating augmenter " + aug + " (id: "+  id + ") with "  + singleParameter);
	}

	@Override
	public void removeAugmenter(String id) {
		AbstractAugmenter aug = this.activeAugmenters.remove(id);
		System.out.println("removing augmenter " + aug + " (id: "+  id + ")");		
	}
	
	private void attachGenerators(DecoratedNote targetNote) {
		synchronized (activeGenerators) {
			for (Entry<String, AbstractGenerator> pair : activeGenerators.entrySet()) {
				AbstractGenerator gen = pair.getValue();
				AbstractGenerator cloned = gen.cloneWithNewPitchVelocityIfUnlocked(targetNote.getPitch(), targetNote.getVelocity());
				targetNote.addGenerator(cloned);
			}
		}
	}
	
	private void attachEffects(DecoratedNote targetNote) {
		synchronized (activeEffects) {
			for (Entry<String, AbstractEffect> pair : activeEffects.entrySet()) {
				AbstractEffect fx = pair.getValue();
				AbstractEffect cloned = fx.clone();
				targetNote.addEffect(cloned);
			}
		}
	}
	
	private void attachAugmenters(DecoratedNote targetNote) {
		synchronized (activeAugmenters) {
			for (Entry<String, AbstractAugmenter> pair : activeAugmenters.entrySet()) {
				AbstractAugmenter aug = pair.getValue();
				targetNote.addAugmenter(aug);
				System.out.println("add " + aug);
			}
		}
	}
	
	private void cleanOldObservers() {
		cleanOldGeneratorObservers();
		cleanOldEffectObservers();
	}
	
	private void cleanOldGeneratorObservers() {
		synchronized (activeGenerators) {
			for (Entry<String, AbstractGenerator> pair : activeGenerators.entrySet()) {
				AbstractGenerator gen = pair.getValue();
				gen.unlinkOldObservers();
			}
		}
	}
	
	private void cleanOldEffectObservers() {
		synchronized (activeEffects) {
			for (Entry<String, AbstractEffect> pair : activeEffects.entrySet()) {
				AbstractEffect fx = pair.getValue();
				fx.unlinkOldObservers();
			}
		}
	}
	
	@Override
	public void noteOnWithoutAugmenters(int channel, int pitch, int velocity) {
		DecoratedNote newNote = new DecoratedNote(channel, pitch, velocity);
		
		this.attachGenerators(newNote);
		this.attachEffects(newNote);
		
		newNote.noteOn();
		memory.put(newNote);
	}
	
	public void noteOn(int channel, int pitch, int velocity) {
		DecoratedNote newNote = new DecoratedNote(channel, pitch, velocity);
		
		this.attachGenerators(newNote);
		this.attachEffects(newNote);
		this.attachAugmenters(newNote);
		
		newNote.noteOn();
		memory.put(newNote);
	}

	@Override
	public void noteOff(int channel, int pitch, int velocity) {
		DecoratedNote n = memory.remove(pitch);
		
		if (n == null) 
			return;
		
		n.noteOff();
		this.cleanOldObservers();
	}
	
	public String whatUserIsPlaying() {
		return this.memory.identifyWhatUserIsPlaying();
	}
	
	public String getLastPlayedNote() {
		return this.memory.getLastPlayedNote();
	}
	
	public boolean thereIsKeyDown() {
		return this.memory.thereIsKeyDown();
	}
	
	public boolean thereIsKeyReleased() {
		return this.memory.thereIsKeyReleased();
	}
	
	public int numberOfKeyPressed() {
		return this.memory.size();
	}
}
