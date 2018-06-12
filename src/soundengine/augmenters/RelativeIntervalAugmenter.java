package soundengine.augmenters;

import org.jfugue.theory.Note;

import soundengine.util.MusicTheory;

public class RelativeIntervalAugmenter extends Augmenter {
	
	private String type;
	
	public RelativeIntervalAugmenter(String type) {
		this.type = type;
	}
	
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (parts[0].trim().equalsIgnoreCase("type"))
			this.setType(parts[1].trim());
	}
	
	protected String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}

	@Override
	public Note[] getNotes(int dynamicRoot) {
		return MusicTheory.generateInterval(dynamicRoot, type);
	}
}
