package frontend.tasks.effects;

import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.tasks.generators.FMGenTask;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableFloatTextfieldUIWithUserInput;
import processing.core.PApplet;
import soundengine.SoundEngine;

public class DelayFxTask extends AbstractFxTask {
	
	private ComputableFloatTextfieldUI delayTime;
	private ComputableFloatTextfieldUI amplitudeFactor;
	
	public DelayFxTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
		
		this.delayTime = new ComputableFloatTextfieldUI(0.02f);
		this.amplitudeFactor = new ComputableFloatTextfieldUI(1f);

//		this.addToEngine();
	}
	
	public void addToEngine() {
		this.eng.addEffect(this.get_gui_id(), "DELAY", getDefaultParameters());
	}
	
	protected String[] getDefaultParameters(){
		//return new String[] { "0.02", "1", "true", "true" };
		return new String[] { 
				 this.delayTime.getValue()+"", 
				 this.amplitudeFactor.getValue()+"", 
				 "true",
				 "true"
				 };
	}
	
	private void processDelayTimeChange() {
		if (delayTime.update())
			this.eng.updateEffect(this.get_gui_id(), "delayTime : " + delayTime.getValue());
	}
	private void processAmplitudeChange() {
		if (amplitudeFactor.update())
			this.eng.updateEffect(this.get_gui_id(), "amplitudeFactor : " + amplitudeFactor.getValue());
	}
	
	protected void processAllParameters() {
		this.processDelayTimeChange();
		this.processAmplitudeChange();
	}

	@Override
	public Task clone_it() {
		DelayFxTask clone = new DelayFxTask(this.p, this.cp5, this.name, this.eng);
		clone.delayTime = this.delayTime;
		clone.amplitudeFactor = this.amplitudeFactor;
		return clone;
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Delay Effect";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 2);
		g.setBackgroundHeight(backgroundheight);

		delayTime.createUI(id, "Delay time", localx, localy + (0 * localoffset), width, g);
		amplitudeFactor.createUI(id, "Amplitude", localx, localy + (1 * localoffset), width, g);

		return g;
	}
	
}
