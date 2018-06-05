package soundengine;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.spi.AudioStream;
import soundengine.augmenters.Augmenter;
import soundengine.augmenters.AugmenterFactory;
import soundengine.core.DecoratedNote;
import soundengine.core.DecoratedNoteMemory;
import soundengine.effects.Effect;
import soundengine.effects.EffectFactory;
import soundengine.generators.Generator;
import soundengine.generators.GeneratorFactory;

/**
 * Implements sound-related services available to the UI as described on SoundEngineFacade interface
 * @author jeraman.info
 *
 */
public class SoundEngine implements SoundEngineFacade {
	
	private DecoratedNoteMemory memory;
	private LinkedHashMap<String, Generator> activeGenerators;
	private LinkedHashMap<String, Effect> activeEffects;
	private LinkedHashMap<String, Augmenter> activeAugmenters;
	
	public static Minim minim;
	public static AudioOutput out;
	public static AudioStream in;
	
	public SoundEngine(Minim minim) {
		this.memory 		  = new DecoratedNoteMemory();
		this.activeGenerators = new LinkedHashMap<String, Generator>();
		this.activeEffects 	  = new LinkedHashMap<String, Effect>();
		this.activeAugmenters = new LinkedHashMap<String, Augmenter>();
		
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
		Generator gen = GeneratorFactory.createGenerator(type, parameters);
		System.out.println("inserting " + id + ","  + type + " as generator " + gen);
		this.activeGenerators.put(id, gen);
	}

	@Override
	public void updateGenerator(String id, String[] parameters) {
		Generator gen = this.activeGenerators.get(id);
		GeneratorFactory.updateGenerator(gen, parameters);
		System.out.println("updating generator " + gen + " (id: "+  id + ") with the following parameters: "  + parameters);
	}

	@Override
	public void removeGenerator(String id) {
		Generator gen = this.activeGenerators.remove(id);
		System.out.println("removing generator " + gen + " (id: "+  id + ")");
	}

	@Override
	public void addEffect(String id, String type, String[] parameters) {
		Effect fx = EffectFactory.createEffect(type, parameters);
		System.out.println("inserting " + id + ","  + type + " as effect " + fx);
		this.activeEffects.put(id, fx);
	}

	@Override
	public void updateEffect(String id, String[] parameters) {
		Effect fx = this.activeEffects.get(id);
		EffectFactory.updateEffect(fx, parameters);
		System.out.println("updating effect " + fx + " (id: "+  id + ") with the following parameters: "  + parameters);
	}

	@Override
	public void removeEffect(String id) {
		Effect fx = this.activeEffects.remove(id);
		System.out.println("removing effect " + fx + " (id: "+  id + ")");
	}
	
	@Override
	public void addAugmenter(String id, String type, String[] parameters) {
		Augmenter aug = AugmenterFactory.createAugmenter(type, parameters);
		System.out.println("inserting " + id + ","  + type + " as augmenter " + aug);
		this.activeAugmenters.put(id, aug);
	}

	@Override
	public void updateAugmenter(String id, String[] parameters) {
		Augmenter aug = this.activeAugmenters.get(id);
		System.out.println("updating augmenter " + aug + " (id: "+  id + ") with the following parameters: "  + parameters);
	}

	@Override
	public void removeAugmenter(String id) {
		Augmenter aug = this.activeAugmenters.remove(id);
		System.out.println("removing augmenter " + aug + " (id: "+  id + ")");		
	}
	
	public void attachGenerators(DecoratedNote targetNote) {
		synchronized (activeGenerators) {
			for (Entry<String, Generator> pair : activeGenerators.entrySet()) {
				Generator gen = pair.getValue();
				Generator cloned = gen.clone(targetNote.getPitch(), targetNote.getVelocity());
				targetNote.addGenerator(cloned);
			}
		}
	}
	
	public void attachEffects(DecoratedNote targetNote) {
		synchronized (activeEffects) {
			for (Entry<String, Effect> pair : activeEffects.entrySet()) {
				Effect fx = pair.getValue();
				Effect cloned = fx.clone();
				targetNote.addEffect(cloned);
			}
		}
	}
	
	public void attachAugmenters(DecoratedNote targetNote) {
		synchronized (activeAugmenters) {
			for (Entry<String, Augmenter> pair : activeAugmenters.entrySet()) {
				Augmenter aug = pair.getValue();
				targetNote.addAugmenter(aug);
				System.out.println("add " + aug);
			}
		}
	}
	
	public void cleanOldObservers() {
		cleanOldGeneratorObservers();
		cleanOldEffectObservers();
	}
	
	public void cleanOldGeneratorObservers() {
		synchronized (activeGenerators) {
			for (Entry<String, Generator> pair : activeGenerators.entrySet()) {
				Generator gen = pair.getValue();
				gen.unlinkOldObservers();
			}
		}
	}
	
	public void cleanOldEffectObservers() {
		synchronized (activeEffects) {
			for (Entry<String, Effect> pair : activeEffects.entrySet()) {
				Effect fx = pair.getValue();
				fx.unlinkOldObservers();
			}
		}
	}
	
	@Override
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

}
