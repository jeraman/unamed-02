package frontend.tasks.effects;

import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.tasks.Task;
import frontend.ui.ComputableFloatTextfieldUI;
import processing.core.PApplet;

public class FlangerFxTask extends AbstractFxTask {

	private ComputableFloatTextfieldUI delayLength; // delay length in
													// milliseconds ( clamped to
													// [0,100] )
	private ComputableFloatTextfieldUI lfoRate; // lfo rate in Hz ( clamped at
												// low end to 0.001 )
	private ComputableFloatTextfieldUI delayDepth; // delay depth in
													// milliseconds ( minimum of
													// 0 )
	private ComputableFloatTextfieldUI feedbackAmplitude; // amount of feedback
															// ( clamped to
															// [0,1] )
	private ComputableFloatTextfieldUI dryAmplitude; // amount of dry signal (
														// clamped to [0,1] )
	private ComputableFloatTextfieldUI wetAmplitude; // amount of wet signal (
														// clamped to [0,1] )

	public FlangerFxTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		this.delayLength = new ComputableFloatTextfieldUI(1f);
		this.lfoRate = new ComputableFloatTextfieldUI(0.5f);
		this.delayDepth = new ComputableFloatTextfieldUI(1f);
		this.feedbackAmplitude = new ComputableFloatTextfieldUI(0.5f);
		this.dryAmplitude = new ComputableFloatTextfieldUI(0.5f);
		this.wetAmplitude = new ComputableFloatTextfieldUI(0.5f);

//		this.addToEngine();
	}
	
	public void addToEngine() {
		Main.eng.addEffect(this.get_gui_id(), "FLANGER", getDefaultParameters());
	}

	protected String[] getDefaultParameters() {
		//return new String[] { "1", "0.5", "1", "0.5", "0.5", "0.5" };
		return new String[] { 
				 this.delayLength.getValue()+"", 
				 this.lfoRate.getValue()+"", 
				 this.delayDepth.getValue()+"",
				 this.feedbackAmplitude.getValue()+"",
				 this.dryAmplitude.getValue()+"", 
				 this.wetAmplitude.getValue()+""
				 };
	}

	private void processDelayLengthChange() {
		if (delayLength.update())
			Main.eng.updateEffect(this.get_gui_id(), "delayLength : " + delayLength.getValue());
	}

	private void processLfoRateChange() {
		if (lfoRate.update())
			Main.eng.updateEffect(this.get_gui_id(), "lfoRate : " + lfoRate.getValue());
	}

	private void processDelayDepthChange() {
		if (delayDepth.update())
			Main.eng.updateEffect(this.get_gui_id(), "delayDepth : " + delayDepth.getValue());
	}

	private void processFeedbackChange() {
		if (feedbackAmplitude.update())
			Main.eng.updateEffect(this.get_gui_id(), "feedbackAmplitude : " + feedbackAmplitude.getValue());
	}

	private void processDryChange() {
		if (dryAmplitude.update())
			Main.eng.updateEffect(this.get_gui_id(), "dryAmplitude : " + dryAmplitude.getValue());
	}

	private void processWetChange() {
		if (wetAmplitude.update())
			Main.eng.updateEffect(this.get_gui_id(), "wetAmplitude : " + wetAmplitude.getValue());
	}

	@Override
	protected void processAllParameters() {
		this.processDelayLengthChange();
		this.processLfoRateChange();
		this.processDelayDepthChange();
		this.processFeedbackChange();
		this.processDryChange();
		this.processWetChange();
	}

	@Override
	public Task clone_it() {
		FlangerFxTask clone = new FlangerFxTask(this.p, this.cp5, this.name);
		clone.delayLength = this.delayLength;
		clone.lfoRate = this.lfoRate;
		clone.delayDepth = this.delayDepth;
		clone.feedbackAmplitude = this.feedbackAmplitude;
		clone.dryAmplitude = this.dryAmplitude;
		clone.wetAmplitude = this.wetAmplitude;
		return clone;
	}

	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Flanger Effect";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 6.2);
		g.setBackgroundHeight(backgroundheight);

		delayLength.createUI(id, "length", localx, localy + (0 * localoffset), width, g);
		lfoRate.createUI(id, "LFO Rate", localx, localy + (1 * localoffset), width, g);
		delayDepth.createUI(id, "Depth", localx, localy + (2 * localoffset), width, g);
		feedbackAmplitude.createUI(id, "Feedback", localx, localy + (3 * localoffset), width, g);
		dryAmplitude.createUI(id, "Dry", localx, localy + (4 * localoffset), width, g);
		wetAmplitude.createUI(id, "Wet", localx, localy + (5 * localoffset), width, g);

		return g;
	}
	
}
