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

		if (type.equalsIgnoreCase("INTERVAL")) {
			//if (parameters.length == 1)
			//	aug = createRelativeInterval(parameters);
			//else
				aug = createInterval(parameters);
		}

		if (type.equalsIgnoreCase("CHORD")) {
			//if (parameters.length == 1)
			//	aug = createRelativeChord(parameters);
			//else
				aug = createChord(parameters);
		}

		return aug;
	}

	public static void updateAugmenter(AbstractAugmenter aug, String[] parameters) {

		if (aug instanceof NoteAugmenter)
			updateNoteAugmenter((NoteAugmenter) aug, parameters);
		//if (aug instanceof RelativeIntervalAugmenter)
		//	updateRelativeIntervalAugmenter((RelativeIntervalAugmenter) aug, parameters);
		if (aug instanceof IntervalAugmenter)
			updateIntervalAugmenter((IntervalAugmenter) aug, parameters);
		//if (aug instanceof RelativeChordAugmenter)
		//	updateRelativeChordAugmenter((RelativeChordAugmenter) aug, parameters);
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
		return new NoteAugmenter(pitch, velocity);
	}

	private static void updateNoteAugmenter(NoteAugmenter aug, String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		aug.setPitch(pitch);
		aug.setVelocity(velocity);
	}

	// relative interval
	@Deprecated
	private static AbstractAugmenter createRelativeInterval(String[] parameters) {
		String type = parameters[0];
		return new RelativeIntervalAugmenter(type);
	}

	@Deprecated
	private static void updateRelativeIntervalAugmenter(RelativeIntervalAugmenter aug, String[] parameters) {
		String type = parameters[0];
		aug.setType(type);
	}

	// interval
	private static AbstractAugmenter createInterval(String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		String type = parameters[2];
		return new IntervalAugmenter(root, velocity, type);
	}

	private static void updateIntervalAugmenter(IntervalAugmenter aug, String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		String type = parameters[2];
		aug.setRoot(root);
		aug.setVelocity(velocity);
		aug.setType(type);
	}

	// relative chord
	@Deprecated
	private static AbstractAugmenter createRelativeChord(String[] parameters) {
		String type = parameters[0];
		return new RelativeChordAugmenter(type);
	}

	@Deprecated
	private static void updateRelativeChordAugmenter(RelativeChordAugmenter aug, String[] parameters) {
		String type = parameters[0];
		aug.setType(type);
	}

	// chord
	private static AbstractAugmenter createChord(String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		String type = parameters[2];
		return new ChordAugmenter(root, velocity, type);
	}

	private static void updateChordAugmenter(ChordAugmenter aug, String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		int velocity = Integer.parseInt(parameters[1]);
		String type = parameters[2];
		aug.setRoot(root);
		aug.setVelocity(velocity);
		aug.setType(type);
	}
}

