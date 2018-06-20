package frontend.tasks.effects;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.tasks.Task;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUI;
import processing.core.PApplet;

public class BitChrushGenTask  extends Task {

	private ComputableIntegerTextfieldUI resolution; 
	
	public BitChrushGenTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		this.resolution = new ComputableIntegerTextfieldUI(5);
		Main.eng.addEffect(this.get_gui_id(), "BITCHRUSH", getDefaultParameters());
	}

	@Override
	protected String[] getDefaultParameters() {
		return new String[] { "5"};
	}

	private void processResolutionChange() {
		if (resolution.update())
			Main.eng.updateEffect(this.get_gui_id(), "resolution : " + resolution.getValueAsInt());
	}
	
	@Override
	protected void processAllParameters() {
		processResolutionChange();		
	}

	@Override
	public Task clone_it() {
		BitChrushGenTask clone = new BitChrushGenTask(this.p, this.cp5, this.name);
		clone.resolution = this.resolution;
		return clone;
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "BitChrush Effect";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 1.3);
		g.setBackgroundHeight(backgroundheight);

		resolution.createUI(id, "Resolution", localx, localy + (0 * localoffset), width, g);

		return g;
	}
	
	/////////////////////////////////////
	// methods to be carried to super or to be deleted
	public void closeTask() {
		Main.eng.removeEffect(this.get_gui_id());
		super.closeTask();
	}

	@Override
	public void reset_gui_fields() {
		// TODO Auto-generated method stub
		
	}

}
