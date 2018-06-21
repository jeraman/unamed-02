package soundengine.augmenters;

import org.jfugue.theory.Note;


//is there any need for this class?
@Deprecated
public class RelativeNoteAugmenter extends AbstractAugmenter {

	private int pitch;
	private int velocity;

	public RelativeNoteAugmenter(int pitch) {
		this(pitch, -1);
	}
	public RelativeNoteAugmenter(int pitch, int velocity) {
		this.pitch = pitch;
		this.velocity = velocity;
	}
	
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (parts[0].trim().equalsIgnoreCase("pitch"))
			this.setPitch(Integer.parseInt(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("velocity"))
			this.setVelocity(Integer.parseInt(parts[1].trim()));
	}
	
	protected int getPitch() {
		return pitch;
	}

	protected void setPitch(int pitch) {
		this.pitch = pitch;
	}
	
	protected int getVelocity() {
		return velocity;
	}
	
	protected void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	@Override
	public Note[] getNotes(int dynamicNote) {
		this.pitch = dynamicNote;
		Note n = new Note(pitch);
		n.setOnVelocity((byte)velocity);
		return new Note[]{n};
	}

}
