package soundengine.augmenters;

import soundengine.effects.Effect;

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

	public static Augmenter createAugmenter(String type, String[] parameters) {
		Augmenter aug = null;

		if (type.equalsIgnoreCase("NOTE"))
			aug = createNote(parameters);

		if (type.equalsIgnoreCase("INTERVAL")) {
			if (parameters.length == 1)
				aug = createRelativeInterval(parameters);
			else
				aug = createInterval(parameters);
		}

		if (type.equalsIgnoreCase("CHORD")) {
			if (parameters.length == 1)
				aug = createRelativeChord(parameters);
			else
				aug = createChord(parameters);
		}

		return aug;
	}

	public static void updateAugmenter(Augmenter aug, String[] parameters) {

		if (aug instanceof NoteAugmenter)
			updateNoteAugmenter((NoteAugmenter) aug, parameters);
		if (aug instanceof RelativeIntervalAugmenter)
			updateRelativeIntervalAugmenter((RelativeIntervalAugmenter) aug, parameters);
		if (aug instanceof IntervalAugmenter)
			updateIntervalAugmenter((IntervalAugmenter) aug, parameters);
		if (aug instanceof RelativeChordAugmenter)
			updateRelativeChordAugmenter((RelativeChordAugmenter) aug, parameters);
		if (aug instanceof ChordAugmenter)
			updateChordAugmenter((ChordAugmenter) aug, parameters);
	}

	// note
	private static Augmenter createNote(String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		return new NoteAugmenter(pitch);
	}

	private static void updateNoteAugmenter(NoteAugmenter aug, String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		aug.setPitch(pitch);
	}

	// relative interval
	private static Augmenter createRelativeInterval(String[] parameters) {
		String type = parameters[0];
		return new RelativeIntervalAugmenter(type);
	}

	private static void updateRelativeIntervalAugmenter(RelativeIntervalAugmenter aug, String[] parameters) {
		String type = parameters[0];
		aug.setType(type);
	}

	// interval
	private static Augmenter createInterval(String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		String type = parameters[1];
		return new IntervalAugmenter(root, type);
	}

	private static void updateIntervalAugmenter(IntervalAugmenter aug, String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		String type = parameters[1];
		aug.setRoot(root);
		aug.setType(type);
	}

	// relative chord
	private static Augmenter createRelativeChord(String[] parameters) {
		String type = parameters[0];
		return new RelativeChordAugmenter(type);
	}

	private static void updateRelativeChordAugmenter(RelativeChordAugmenter aug, String[] parameters) {
		String type = parameters[0];
		aug.setType(type);
	}

	// chord
	private static Augmenter createChord(String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		String type = parameters[1];
		return new ChordAugmenter(root, type);
	}

	private static void updateChordAugmenter(ChordAugmenter aug, String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		String type = parameters[1];
		aug.setRoot(root);
		aug.setType(type);
	}
}
