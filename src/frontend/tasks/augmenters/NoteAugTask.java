package frontend.tasks.augmenters;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.ui.ComputableIntegerTextfieldUI;
import processing.core.PApplet;
import soundengine.SoundEngine;

public class NoteAugTask extends AbstractAugTask {
	
	private ComputableIntegerTextfieldUI pitch;
	
	public NoteAugTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);

		this.pitch = new ComputableIntegerTextfieldUI(60.0f);
		this.musicActioner = new NoteActioner(this.pitch.getDefaultValueAsInt(), this.velocity.getDefaultValueAsInt(),(int) this.duration.getValue(), eng);
		
		Main.log.countNoteAugTask();
	}

	@Override
	protected String[] getDefaultParameters() {
		return new String[] { "60","-1", "-1" };
	}

	@Override
	protected void addOnEngine() {
		this.eng.addAugmenter(this.get_gui_id(), "NOTE", getDefaultParameters());
	}
	
	protected void resetMusicActioner() {
		((NoteActioner)musicActioner).setPitch(this.pitch.getValueAsInt());
		super.resetMusicActioner();
	}

	private void processPitchChange() {
		if (pitch.update()) {
			if (isModeUserInput())
				this.eng.updateAugmenter(this.get_gui_id(), "pitch : " + pitch.getValueAsInt());
			if (isModePlayOnce() || isModeRepeat())
				((NoteActioner)this.musicActioner).setPitch(pitch.getValueAsInt());
		}
	}

	@Override
	protected void processAllParameters() {
		processPitchChange();
		super.processAllParameters();
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Note Augmenter";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 4.5);
		g.setBackgroundHeight(backgroundheight);
		int miniOffsetDueToScrollList = 5;
		mode.createUI(id, "          mode", localx, localy + (0 * localoffset), width, g);
		pitch.createUI(id, "pitch", localx, localy + miniOffsetDueToScrollList + (1 * localoffset), width, g);
		velocity.createUI(id, "velocity", localx, localy + miniOffsetDueToScrollList  + (2 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + miniOffsetDueToScrollList + (3 * localoffset), width, g);

		return g;
	}

	@Override
	public Task clone_it() {
		NoteAugTask clone = new NoteAugTask(this.p, this.cp5, this.name, this.eng);
		clone.pitch = this.pitch;
		clone.velocity = this.velocity;
		clone.duration = this.duration;
		clone.mode = this.mode;
		return clone;
	}
}
