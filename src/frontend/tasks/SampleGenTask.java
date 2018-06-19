package frontend.tasks;

import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.FileOpenerTextfieldUI;
import frontend.ui.ToggleUi;
import processing.core.PApplet;

public class SampleGenTask extends Task {
	
	private FileOpenerTextfieldUI filename;
	private ComputableIntegerTextfieldUI pitch;
	private ComputableIntegerTextfieldUI velocity;
	private ComputableIntegerTextfieldUI duration;
	private ToggleUi loopStatus;
	
	public SampleGenTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		
		this.filename = new FileOpenerTextfieldUI();
		this.pitch = new ComputableIntegerTextfieldUI();
		this.velocity = new ComputableIntegerTextfieldUI();
		this.duration = new ComputableIntegerTextfieldUI();
		this.loopStatus = new ToggleUi();
		
		Main.eng.addGenerator(this.get_gui_id(), "SAMPLE", getDefaultParameters());
	}
	
	private String[] getDefaultParameters(){
		return new String[] { FileOpenerTextfieldUI.defaultSoundFile, "-1", "-1", "true", "-1"};
	}
	
	private void processFilenameChange() {
		if (filename.update()) {
			System.out.println("update filename " + filename.getValue());
			Main.eng.updateGenerator(this.get_gui_id(), "filename : " + filename.getValue());
		}
	}
	private void processPitchChange() {
		if (pitch.update()) {
			System.out.println("update pitch " + pitch.getValueAsInt());
			Main.eng.updateGenerator(this.get_gui_id(), "pitch : " + pitch.getValueAsInt());
		}
	}
	
	private void processlVelocityChange() {
		if (velocity.update())
			Main.eng.updateGenerator(this.get_gui_id(), "velocity : " + velocity.getValueAsInt());
	}
	
	private void processDurationChange() {
		if (duration.update()) {
			System.out.println("update duration " + duration.getValueAsInt());
			Main.eng.updateGenerator(this.get_gui_id(), "duration : " + duration.getValueAsInt());
		}
	}
	
	private void processLoopChange() {
		if (loopStatus.update()) {
			System.out.println("update loop " + duration.getValue());
			Main.eng.updateGenerator(this.get_gui_id(), "loop : " + loopStatus.getValue());
		}
	}
	
	private void processAllParameters() {
		this.processFilenameChange();
		this.processPitchChange();
		this.processlVelocityChange();
		this.processDurationChange();
		this.processLoopChange();
	}
	
	public void closeTask() {
		Main.eng.removeGenerator(this.get_gui_id());
		super.closeTask();
	}
	
	@Override
	public void run() {
		if (!should_run())
			return;

		processAllParameters();

	}
	
	@Override
	public Task clone_it() {
		SampleGenTask clone = new SampleGenTask(this.p, this.cp5, this.name);
		clone.pitch = this.pitch;
		clone.velocity = this.velocity;
		clone.duration = this.duration;
		clone.filename = this.filename;
		clone.loopStatus = this.loopStatus;
		return clone;
	}

	@Override
	public void build(PApplet p, ControlP5 cp5) {
		// TODO Auto-generated method stub
		this.p = p;
		this.cp5 = cp5;
	}

	@Override
	public void update_status() {
		// TODO Auto-generated method stub
	}
	
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Sampler Generator";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 5.2);
		g.setBackgroundHeight(backgroundheight);

		filename.createUI(id, "filename", localx, localy + (0 * localoffset), width, g);
		pitch.createUI(id, "pitch", localx, localy + (1 * localoffset), width, g);
		velocity.createUI(id, "velocity", localx, localy + (2 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + (3 * localoffset), width, g);
		loopStatus.createUI(id, "once - repeat", localx, localy + (4 * localoffset), width, g);

		// this.createGuiToggle(localx, localy + (4 * localoffset), width, g,

		return g;
	}

	@Override
	public CallbackListener generate_callback_enter() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public void reset_gui_fields() {
		String g_name = this.get_gui_id();
		String nv;
		// if this group is not open, returns...
		if (!((Group) cp5.get(get_gui_id())).isOpen())
			return;

		pitch.updateValueExpression();
		velocity.updateValueExpression();
		duration.updateValueExpression();
	}

	
}
