package soundengine.augmenters;

import org.jfugue.theory.Note;

import soundengine.util.Util;

public class NoteAugmenter extends AbstractAugmenter {

	private int pitch;
	private int velocity;

	public NoteAugmenter(int pitch, int duration) {
		this(pitch, duration, -1);
	}
	public NoteAugmenter(int pitch, int duration, int velocity) {
		super(duration);
		this.pitch = pitch;
		this.velocity = velocity;
	}
	
	public void updateParameterFromString(String singleParameter) {
		String[] parts = singleParameter.split(":");
		
		if (parts[0].trim().equalsIgnoreCase("pitch"))
			this.setPitch(Integer.parseInt(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("velocity"))
			this.setVelocity(Integer.parseInt(parts[1].trim()));
		if (parts[0].trim().equalsIgnoreCase("duration"))
			this.setDuration((int)Float.parseFloat(parts[1].trim()));
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
	public Note[] getNotes(int newPitch, int newVel) {
		int rightPitch = getTheRightPitch(pitch, newPitch);
		int rightVel = getTheRightVel(velocity, newVel);
		Note n = new Note(rightPitch);
		n.setOnVelocity(Util.parseIntToByte(rightVel));
		return new Note[]{n};
	}

}
