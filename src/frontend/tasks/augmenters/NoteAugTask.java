package frontend.tasks.augmenters;

import java.util.Arrays;
import java.util.List;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.tasks.Task;
import frontend.tasks.generators.OscillatorGenTask;
import frontend.ui.ComputableFloatTextfieldUIWithUserInput;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUIWithUserInput;
import frontend.ui.ScrollableListUI;
import processing.core.PApplet;

public class NoteAugTask extends Task {
	
	protected static final List<String> list = Arrays.asList("USER INPUT", "PLAY ONCE", "REPEAT");
	
	private ComputableIntegerTextfieldUI pitch;
	private ComputableIntegerTextfieldUIWithUserInput velocity;
	private ComputableFloatTextfieldUIWithUserInput duration;
	private ScrollableListUI mode;

	public NoteAugTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);

		this.pitch = new ComputableIntegerTextfieldUI(60);
		this.velocity = new ComputableIntegerTextfieldUIWithUserInput();
		this.duration = new ComputableFloatTextfieldUIWithUserInput();
		this.mode = new ScrollableListUI(list, 0);
		
		Main.eng.addAugmenter(this.get_gui_id(), "NOTE", getDefaultParameters());
	}

	@Override
	protected String[] getDefaultParameters() {
		return new String[] { "60", "-1", "-1"};
	}
	
	private void processPitchChange() {
		if (pitch.update())
			Main.eng.updateAugmenter(this.get_gui_id(), "pitch : " + pitch.getValueAsInt());
	}
	
	private void processVelocityChange() {
		if (velocity.update())
			Main.eng.updateAugmenter(this.get_gui_id(), "velocity : " + velocity.getValueAsInt());
	}
	
	private void processDurationChange() {
		if (duration.update())
			Main.eng.updateAugmenter(this.get_gui_id(), "duration : " + duration.getValue());
	}
	
	private void processModeChange() {
		if (mode.update())
		//	Main.eng.updateAugmenter(this.get_gui_id(), "duration : " + mode.getValue());
			System.out.println("changed mode!");
	}

	@Override
	protected void processAllParameters() {
		processPitchChange();		
		processVelocityChange();		
		processDurationChange();
		processModeChange();
	}

	@Override
	public Task clone_it() {
		NoteAugTask clone = new NoteAugTask(this.p, this.cp5, this.name);
		clone.pitch = this.pitch;
		clone.velocity = this.velocity;
		clone.duration = this.duration;
		return clone;
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

	/////////////////////////////////////
	// methods to be carried to super or to be deleted
	public void closeTask() {
		Main.eng.removeAugmenter(this.get_gui_id());
		super.closeTask();
	}

	@Override
	public void reset_gui_fields() {
		// TODO Auto-generated method stub
		
	}

}
