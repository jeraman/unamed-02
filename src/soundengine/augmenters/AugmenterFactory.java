package soundengine.augmenters;

import soundengine.effects.AbstractEffect;

/**
 * Singleton class used to create custom augmenters
 * 
 * @author jeraman.info
 *
 */
public class AugmenterFactory {

	// this class is a singleton
	private AugmenterFactory() {
	}

	public static AbstractAugmenter createAugmenter(String type, String[] parameters) {
		AbstractAugmenter aug = null;

		if (type.equalsIgnoreCase("NOTE"))
			aug = createNote(parameters);

		if (type.equalsIgnoreCase("INTERVAL")) 
			aug = createInterval(parameters);
		
		if (type.equalsIgnoreCase("CHORD")) 
			aug = createChord(parameters);
		
		return aug;
	}

	public static void updateAugmenter(AbstractAugmenter aug, String[] parameters) {

		if (aug instanceof NoteAugmenter)
			updateNoteAugmenter((NoteAugmenter) aug, parameters);
		if (aug instanceof IntervalAugmenter)
			updateIntervalAugmenter((IntervalAugmenter) aug, parameters);
		if (aug instanceof ChordAugmenter)
			updateChordAugmenter((ChordAugmenter) aug, parameters);
	}
	
	public static void updateAugmenter(AbstractAugmenter aug, String singleParameter) {
		aug.updateParameterFromString(singleParameter);
	}

	// note
	private static AbstractAugmenter createNote(String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		int duration = Integer.parseInt(parameters[2]);
		return new NoteAugmenter(pitch, velocity, duration);
	}

	private static void updateNoteAugmenter(NoteAugmenter aug, String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		int duration = Integer.parseInt(parameters[2]);
		aug.setPitch(pitch);
		aug.setVelocity(velocity);
		aug.setDuration(duration);
	}

	// interval
	private static AbstractAugmenter createInterval(String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		int duration = Integer.parseInt(parameters[2]);
		String type = parameters[3];
		return new IntervalAugmenter(root, velocity, duration, type);
	}

	private static void updateIntervalAugmenter(IntervalAugmenter aug, String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		int duration = Integer.parseInt(parameters[2]);
		String type = parameters[3];
		aug.setRoot(root);
		aug.setVelocity(velocity);
		aug.setDuration(duration);
		aug.setType(type);
	}

	// chord
	private static AbstractAugmenter createChord(String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		int duration = Integer.parseInt(parameters[2]);
		String type = parameters[3];
		return new ChordAugmenter(root, velocity, duration, type);
	}

	private static void updateChordAugmenter(ChordAugmenter aug, String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		int duration = Integer.parseInt(parameters[2]);
		String type = parameters[3];
		aug.setRoot(root);
		aug.setVelocity(velocity);
		aug.setDuration(duration);
		aug.setType(type);
	}
}

