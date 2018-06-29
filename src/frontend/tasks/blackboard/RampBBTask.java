package frontend.tasks.blackboard;


import controlP5.*;
import frontend.Blackboard;
import frontend.Expression;
import frontend.Main;
import frontend.State;
import frontend.Status;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.TextfieldUi;
import processing.core.PApplet;


public class RampBBTask extends AbstractBBTask {
	private ComputableFloatTextfieldUI origin; 
	private ComputableFloatTextfieldUI destination; 
	private ComputableFloatTextfieldUI duration; 
	
	public RampBBTask (PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		this.origin = new ComputableFloatTextfieldUI(0f);
		this.destination = new ComputableFloatTextfieldUI(1f);
		this.duration = new ComputableFloatTextfieldUI(1f);
		this.value = new TextfieldUi("0");
		
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
		boolean result =  ((this.destination.getValue() - this.value.evaluateAsFloat()) > (1f/(30f*duration.getValue())));
//		System.out.println("value: " + this.value.evaluateAsFloat());
//		System.out.println("duration: " + this.duration.getValue());
//		System.out.println("1f/(10f*duration.getValue()): " + 1f/(10f*duration.getValue()));
//		System.out.println("destination: " + this.destination.getValue());
//		System.out.println("done? " + result);
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
		RampBBTask clone = new RampBBTask(this.p, this.cp5, this.name);

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