package frontend.tasks.effects;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUI;
import processing.core.PApplet;
import soundengine.SoundEngine;

public class BitChrushFxTask  extends AbstractFxTask {

	private ComputableIntegerTextfieldUI resolution; 
	
	public BitChrushFxTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
		this.resolution = new ComputableIntegerTextfieldUI(5, 4, 10);
		
		Main.log.countBitchrushFxTask();
	}
	
	public void addToEngine() {
		this.eng.addEffect(this.get_gui_id(), "BITCHRUSH", getDefaultParameters());
	}

	@Override
	protected String[] getDefaultParameters() {
		//return new String[] { "5"};
		return new String[] { 
				 this.resolution.getValueAsInt()+""
				 };
	}

	private void processResolutionChange() {
		if (resolution.update())
			this.eng.updateEffect(this.get_gui_id(), "resolution : " + resolution.getValueAsInt());
	}
	
	@Override
	protected void processAllParameters() {
		processResolutionChange();		
	}

	@Override
	public Task clone_it() {
		BitChrushFxTask clone = new BitChrushFxTask(this.p, this.cp5, this.name, this.eng);
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
	
}
