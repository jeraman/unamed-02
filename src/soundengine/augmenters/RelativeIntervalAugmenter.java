package soundengine.augmenters;

import org.jfugue.theory.Note;

import soundengine.util.MusicTheory;

@Deprecated
public class RelativeIntervalAugmenter extends AbstractAugmenter {
	
	private int velocity;
	private String type;
	
	public RelativeIntervalAugmenter(String type) {
		this(type, -1);
	}
	
	public RelativeIntervalAugmenter(String type, int velocity) {
		this.type = type;
		this.velocity = velocity;
	}
	
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (parts[0].trim().equalsIgnoreCase("type"))
			this.setType(parts[1].trim());
		if (parts[0].trim().equalsIgnoreCase("velocity"))
			this.setVelocity(Integer.parseInt(parts[1].trim()));
	}
	
	protected String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}
	
	protected int getVelocity() {
		return velocity;
	}
	
	protected void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	@Override
	public Note[] getNotes(int dynamicRoot) {
		return MusicTheory.generateInterval(dynamicRoot, velocity, type);
	}
}
