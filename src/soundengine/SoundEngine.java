package soundengine;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.spi.AudioStream;
import soundengine.augmenters.AugmentedNote;
import soundengine.augmenters.AugmentedNoteMemory;
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
	
	private AugmentedNoteMemory memory;
	private LinkedHashMap<String, Generator> activeGenerators;
	private LinkedHashMap<String, Effect> activeEffects;
	
	public static Minim minim;
	public static AudioOutput out;
	public static AudioStream in;
	
	public SoundEngine(Minim minim) {
		this.memory 		  = new AugmentedNoteMemory();
		this.activeGenerators = new LinkedHashMap<String, Generator>();
		this.activeEffects 	  = new LinkedHashMap<String, Effect>();
		
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
		// TODO Auto-generated method stub
		Generator gen = this.activeGenerators.get(id);
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

//	@Override
//	public void addEffect(String id, String type) {
//		Effect fx = null;
//
//		if (type.equalsIgnoreCase("ADSR"))
//			fx = new AdsrEffect();
//		if (type.equalsIgnoreCase("BANDPASS"))
//			fx = new BandPassFilterEffect();
//		if (type.equalsIgnoreCase("BITCHRUSH"))
//			fx = new BitChrushEffect();
//		if (type.equalsIgnoreCase("DELAY"))
//			fx = new DelayEffect();
//		if (type.equalsIgnoreCase("FLANGER"))
//			fx = new FlangerEffect();
//		if (type.equalsIgnoreCase("HIGHPASS"))
//			fx = new HighPassFilterEffect();
//		if (type.equalsIgnoreCase("LOWPASS"))
//			fx = new LowPassFilterEffect();
//		if (type.equalsIgnoreCase("MOOGFILTER"))
//			fx = new MoogFilterEffect();
//
//		System.out.println("inserting " + id + "," + type + " as effect " + fx);
//		this.activeEffects.put(id, fx);
//	}

	@Override
	public void updateEffect(String id, String[] parameters) {
		// TODO Auto-generated method stub
		Effect fx = this.activeEffects.get(id);
		System.out.println("updating generator " + fx + " (id: "+  id + ") with the following parameters: "  + parameters);
	}

	@Override
	public void removeEffect(String id) {
		// TODO Auto-generated method stub
		Effect fx = this.activeEffects.remove(id);
		System.out.println("removing generator " + fx + " (id: "+  id + ")");
	}

	@Override
	public void addArtificialNote(int newNotePitch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addArtificialInterval(String intervalType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addArtificialInterval(int newPitch, String intervalType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addArtificialChord(String chordType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addArtificialChord(int newRoot, String chordType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeArtificialNote(int newNotePitch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeArtificialInterval(String intervalType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeArtificialInterval(int newPitch, String intervalType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeArtificialChord(String chordType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeArtificialChord(int newRoot, String chordType) {
		// TODO Auto-generated method stub
		
	}
	
	public void attachGenerators(AugmentedNote targetNote) {
		
		synchronized (activeGenerators) {
			for (Entry<String, Generator> pair : activeGenerators.entrySet()) {
				Generator gen = pair.getValue();
				// TODO: remember to add the observers
				Generator cloned = gen.clone(targetNote.getPitch(), targetNote.getVelocity());
				targetNote.addGenerator(cloned);
			}
		}
	}
	
	@Override
	public void noteOn(int channel, int pitch, int velocity) {
		AugmentedNote newNote = new AugmentedNote(channel, pitch, velocity);
		
		this.attachGenerators(newNote);
		//TODO: load up all effects
		//TODO: load up all augmenters
		
		newNote.noteOn();
		memory.put(newNote);
	}

	@Override
	public void noteOff(int channel, int pitch, int velocity) {
		AugmentedNote n = memory.remove(pitch);
		if (n == null) return;
		n.noteOff();
	}

}
