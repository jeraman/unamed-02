package frontend.tasks.generators;

import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUIWithUserInput;
import frontend.ui.FileOpenerTextfieldUI;
import frontend.ui.ToggleUi;
import processing.core.PApplet;
import soundengine.SoundEngine;

public class SampleGenTask extends AbstractGenTask {
	
	private FileOpenerTextfieldUI filename;
	private ComputableIntegerTextfieldUIWithUserInput pitch;
	private ComputableIntegerTextfieldUIWithUserInput velocity;
	private ComputableIntegerTextfieldUIWithUserInput duration;
	private ToggleUi loopStatus;
	
	private static final String defaultSoundFile = "123go.mp3";
	
	public SampleGenTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
		
		this.filename = new FileOpenerTextfieldUI(defaultSoundFile);
		this.pitch = new ComputableIntegerTextfieldUIWithUserInput();
		this.velocity = new ComputableIntegerTextfieldUIWithUserInput();
		this.duration = new ComputableIntegerTextfieldUIWithUserInput();
		this.loopStatus = new ToggleUi();
		
//		this.addToEngine();
	}
	
	public void addToEngine() {
		this.eng.addGenerator(this.get_gui_id(), "SAMPLE", getDefaultParameters());
	}
	
	protected String[] getDefaultParameters(){
		//return new String[] { defaultSoundFile, "-1", "-1", "true", "-1"};
		return new String[] { 
				 this.filename.getValue()+"", 
				 this.pitch.getValueAsInt()+"", 
				 this.velocity.getValueAsInt()+"", 
				 this.loopStatus.getValue()+"",
				 this.duration.getValueAsInt()+""
				 };
	}
	
	private void processFilenameChange() {
		if (filename.update()) {
			System.out.println("update filename " + filename.getValue());
			this.eng.updateGenerator(this.get_gui_id(), "filename : " + filename.getValue());
		}
	}
	private void processPitchChange() {
		if (pitch.update()) {
			System.out.println("update pitch " + pitch.getValueAsInt());
			this.eng.updateGenerator(this.get_gui_id(), "pitch : " + pitch.getValueAsInt());
		}
	}
	
	private void processVelocityChange() {
		if (velocity.update())
			this.eng.updateGenerator(this.get_gui_id(), "velocity : " + velocity.getValueAsInt());
	}
	
	private void processDurationChange() {
		if (duration.update()) {
			System.out.println("update duration " + duration.getValueAsInt());
			this.eng.updateGenerator(this.get_gui_id(), "duration : " + duration.getValueAsInt());
		}
	}
	
	private void processLoopChange() {
		if (loopStatus.update()) {
			System.out.println("update loop " + loopStatus.getValue());
			this.eng.updateGenerator(this.get_gui_id(), "loop : " + loopStatus.getValue());
		}
	}
	
	protected void processAllParameters() {
		this.processFilenameChange();
		this.processPitchChange();
		this.processVelocityChange();
		this.processDurationChange();
		this.processLoopChange();
	}
	
	@Override
	public Task clone_it() {
		SampleGenTask clone = new SampleGenTask(this.p, this.cp5, this.name, this.eng);
		clone.pitch = this.pitch;
		clone.velocity = this.velocity;
		clone.duration = this.duration;
		clone.filename = this.filename;
		clone.loopStatus = this.loopStatus;
		return clone;
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

	
//
//	@Override
//	public void reset_gui_fields() {
//		String g_name = this.get_gui_id();
//		String nv;
//		// if this group is not open, returns...
//		if (!((Group) cp5.get(get_gui_id())).isOpen())
//			return;
//
//		pitch.updateValueExpression();
//		velocity.updateValueExpression();
//		duration.updateValueExpression();
//	}

	
}
