package frontend.tasks.blackboard;


import controlP5.*;
import frontend.Main;
import frontend.core.Blackboard;
import frontend.core.Expression;
import frontend.core.State;
import frontend.core.Status;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.TextfieldUi;
import processing.core.PApplet;
import soundengine.SoundEngine;


public class RampBBTask extends AbstractBBTask {
	private ComputableFloatTextfieldUI origin; 
	private ComputableFloatTextfieldUI destination; 
	private ComputableFloatTextfieldUI duration; 
	
	public RampBBTask (PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
		this.origin = new ComputableFloatTextfieldUI(0f);
		this.destination = new ComputableFloatTextfieldUI(1f);
		this.duration = new ComputableFloatTextfieldUI(1f);
		this.value = new TextfieldUi("0");
		
		Main.log.countRampBBTask();
	}
	
	private void updateValue() {
		float delta = origin.getValue() - destination.getValue();
		float absDelta = Math.abs(delta);
		float dest = destination.getValue();
		float orig = origin.getValue();
		float dur = Math.abs(duration.getValue());
		
		if (dest > orig)
			this.value = new TextfieldUi(absDelta + " * math.abs(("+timer+"/"+ dur+") % 1) + " + orig);
		else
			this.value = new TextfieldUi("(" + absDelta + " - (" + absDelta + " * math.abs(("+timer+"/"+dur+") % 1))) + " + (int)(orig-delta) );
	}
	
	protected boolean isFirstCycle() {
		float threshold;
		boolean result;
		float dest = destination.getValue();
		float orig = origin.getValue();
		
		if (dest > orig) {
			threshold = (dest/(30f*duration.getValue()));
			result =  ((dest - this.value.evaluateAsFloat()) > threshold);
		} else {
			threshold = (orig/(30f*duration.getValue()));
			result =  ((this.value.evaluateAsFloat() - dest) > threshold);
		}
		
		System.out.println("value: " + this.value.evaluateAsFloat());
		System.out.println("duration: " + this.duration.getValue());
		System.out.println("dest - value: " + Math.abs(this.destination.getValue() - this.value.evaluateAsFloat()));
		System.out.println("threshold: " + threshold);
		System.out.println("destination: " + this.destination.getValue());
		System.out.println("done? " + result);
		return result || first_time;
	}	
	
	private void processOriginChange() {
		origin.update();
		updateValue();
	}
	
	private void processDestinationChange() {
		destination.update();
		updateValue();
	}
	
	private void processDurationChange() {
		duration.update();
		updateValue();
	}
	
	@Override
	protected void processAllParameters() {
		super.processAllParameters();
		processOriginChange();
		processDestinationChange();
		processDurationChange();
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Ramp Variable";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 5.5);
		g.setBackgroundHeight(backgroundheight);

		variableName.createUI(id, "name", localx, localy + (0 * localoffset), width, g);
		origin.createUI(id, "origin", localx, localy + (1 * localoffset), width, g);
		destination.createUI(id, "destination", localx, localy + (2 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + (3 * localoffset), width, g);
		shouldRepeat.createUI(id, "once - repeat", localx, localy + (4 * localoffset), width, g);

		return g;
	}
	
	public RampBBTask clone_it() {
		RampBBTask clone = new RampBBTask(this.p, this.cp5, this.name, this.eng);

		clone.variableName = this.variableName;
		clone.origin = this.origin;
		clone.destination = this.destination;
		clone.duration = this.duration;
		clone.shouldRepeat = this.shouldRepeat;
		clone.value = this.value;
		clone.timerMilestone = this.timerMilestone;
		clone.timer = this.timer;

		return clone;
	}
}