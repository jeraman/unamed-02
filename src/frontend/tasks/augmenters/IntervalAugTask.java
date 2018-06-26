package frontend.tasks.augmenters;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.tasks.Task;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUIWithUserInput;
import processing.core.PApplet;

public class IntervalAugTask extends AbstractAugTask {

	private ComputableIntegerTextfieldUI root;
	private ComputableIntegerTextfieldUI interval;
	
	public IntervalAugTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);

		this.root = new ComputableIntegerTextfieldUI(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault,-1);
		this.interval = new ComputableIntegerTextfieldUI(5);
		
		this.musicActioner = new IntervalActioner(this.root.getDefaultValueAsInt(), 5, this.velocity.getDefaultValueAsInt(),(int) this.duration.getValue());

		addOnEngine();
	}
	
	@Override
	protected String[] getDefaultParameters() {
		return new String[] { "-1","-1", "-1", "5"};
	}
	
	@Override
	protected void setModeUserInput() {
		this.root.resetDefaults(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault, -1);
		super.setModeUserInput();
	}

	@Override
	protected void setModePlayOnce() {
		this.root.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 60);
		super.setModePlayOnce();
	}
	
	@Override
	protected void setModeRepeat() {
		this.root.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 60);
		super.setModeRepeat();
	}

	@Override
	protected void addOnEngine() {
		Main.eng.addAugmenter(this.get_gui_id(), "INTERVAL", getDefaultParameters());
	}
	
	private void processRootChange() {
		if (root.update()) {
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "root : " + root.getValueAsInt());
			if (isModePlayOnce() || isModeRepeat())
				((IntervalActioner)this.musicActioner).setRoot(root.getValueAsInt());
		}
	}
	
	private void processIntervalChange() {
		if (interval.update()) {
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "type : " + interval.getValueAsInt());
			if (isModePlayOnce() || isModeRepeat())
				((IntervalActioner)this.musicActioner).setInterval(root.getValueAsInt()+interval.getValueAsInt());
		}
	}
	
	@Override
	protected void processAllParameters() {
		processRootChange();
		processIntervalChange();
		super.processAllParameters();
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Interval Augmenter";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 5.5);
		g.setBackgroundHeight(backgroundheight);
		
		root.createUI(id, "root", localx, localy + (0 * localoffset), width, g);
		interval.createUI(id, "interval", localx, localy + (1 * localoffset), width, g);
		velocity.createUI(id, "velocity", localx, localy + (2 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + (3 * localoffset), width, g);
		mode.createUI(id, "          mode", localx, localy + (4 * localoffset), width, g);

		return g;
	}

	@Override
	public Task clone_it() {
		IntervalAugTask clone = new IntervalAugTask(this.p, this.cp5, this.name);
		clone.root = this.root;
		clone.interval = this.interval;
		clone.velocity = this.velocity;
		clone.duration = this.duration;
		clone.mode = this.mode;
		return clone;
	}

}
