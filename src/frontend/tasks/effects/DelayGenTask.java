package frontend.tasks.effects;

import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.tasks.Task;
import frontend.tasks.generators.FMGenTask;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableFloatTextfieldUIWithUserInput;
import processing.core.PApplet;

public class DelayGenTask extends Task {
	
	private ComputableFloatTextfieldUI delayTime;
	private ComputableFloatTextfieldUI amplitudeFactor;
	
	public DelayGenTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		
		this.delayTime = new ComputableFloatTextfieldUI(0.02f);
		this.amplitudeFactor = new ComputableFloatTextfieldUI(1f);
		
		Main.eng.addEffect(this.get_gui_id(), "DELAY", getDefaultParameters());
	}
	
	private String[] getDefaultParameters(){
		return new String[] { "0.02", "1", "true", "true" };
	}
	
	private void processDelayLengthChange() {
		if (delayTime.update())
			Main.eng.updateEffect(this.get_gui_id(), "delayTime : " + delayTime.getValue());
	}
	private void processLfoRateChange() {
		if (amplitudeFactor.update())
			Main.eng.updateEffect(this.get_gui_id(), "amplitudeFactor : " + amplitudeFactor.getValue());
	}
	
	protected void processAllParameters() {
		this.processDelayLengthChange();
		this.processLfoRateChange();
	}

	@Override
	public Task clone_it() {
		DelayGenTask clone = new DelayGenTask(this.p, this.cp5, this.name);
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
		
		delayTime.createUI(id, "Delay time", localx, localy +  (0 * localoffset), width, g);
		amplitudeFactor.createUI(id, "Amplitude", localx, localy + (1 * localoffset), width, g);

		return g;
	}
	
	/////////////////////////////////////
	// methods to be carried to super or to be deleted
	public void closeTask() {
		Main.eng.removeEffect(this.get_gui_id());
		super.closeTask();
	}
	
	@Override
	public CallbackListener generate_callback_enter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset_gui_fields() {
		// TODO Auto-generated method stub	
	}

	@Override
	public void update_status() {
		// TODO Auto-generated method stub
		
	}

}
