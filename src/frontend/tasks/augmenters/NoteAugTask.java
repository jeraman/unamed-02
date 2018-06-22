package frontend.tasks.augmenters;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.tasks.Task;
import frontend.ui.ComputableIntegerTextfieldUI;
import processing.core.PApplet;

public class NoteAugTask extends AbstractAugTask {
	
	private ComputableIntegerTextfieldUI pitch;
	
	public NoteAugTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);

		this.pitch = new ComputableIntegerTextfieldUI(60.0f);
		this.noteKiller = new NoteMaker(this.pitch.getDefaultValueAsInt(), this.velocity.getDefaultValueAsInt(),(int) this.duration.getValue());

		addOnEngine();
	}

	@Override
	protected String[] getDefaultParameters() {
		return new String[] { "60","-1", "-1" };
	}

	@Override
	protected void addOnEngine() {
		Main.eng.addAugmenter(this.get_gui_id(), "NOTE", getDefaultParameters());
	}

	private void processPitchChange() {
		if (pitch.update()) {
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "pitch : " + pitch.getValueAsInt());
			if (isModePlayOnce() || isModeRepeat())
				this.noteKiller.setPitch(pitch.getValueAsInt());
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
		pitch.createUI(id, "pitch", localx, localy + (0 * localoffset), width, g);
		velocity.createUI(id, "velocity", localx, localy + (1 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + (2 * localoffset), width, g);
		mode.createUI(id, "          mode", localx, localy + (3 * localoffset), width, g);

		return g;
	}

	@Override
	public Task clone_it() {
		NoteAugTask clone = new NoteAugTask(this.p, this.cp5, this.name);
		clone.pitch = this.pitch;
		clone.velocity = this.velocity;
		clone.duration = this.duration;
		clone.mode = this.mode;
		return clone;
	}
}
