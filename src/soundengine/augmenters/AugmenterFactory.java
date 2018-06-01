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

	private static Augmenter createNote(String[] parameters) {
		int pitch = Integer.parseInt(parameters[0]);
		return new NoteAugmenter(pitch);
	}

	private static Augmenter createRelativeInterval(String[] parameters) {
		String type = parameters[0];
		return new RelativeIntervalAugmenter(type);
	}
	
	private static Augmenter createInterval(String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		String type = parameters[1];
		return new IntervalAugmenter(root, type);
	}

	private static Augmenter createRelativeChord(String[] parameters) {
		String type = parameters[0];
		return new RelativeChordAugmenter(type);
	}

	private static Augmenter createChord(String[] parameters) {
		int root = Integer.parseInt(parameters[0]);
		String type = parameters[1];
		return new ChordAugmenter(root, type);
	}

}
