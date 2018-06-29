package frontend.tasks.blackboard;



import controlP5.*;
import frontend.State;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.TextfieldUi;
import processing.core.PApplet;


public class RandomBBTask extends AbstractBBTask {

	private ComputableFloatTextfieldUI min;
	private ComputableFloatTextfieldUI max;
	
	public RandomBBTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		this.min = new ComputableFloatTextfieldUI(0f);
		this.max = new ComputableFloatTextfieldUI(1f);
		this.value = new TextfieldUi("math.random()");
	}
	
	private void updateValue() {
		this.value = new TextfieldUi("math.random() * (" + max.getValue() + " - "  + min.getValue() + ") + " + min.getValue());
	}
	
	private void processMinimumChange() {
		min.update();
		updateValue();
	}
	
	private void processMaximumChange() {
		max.update();
		updateValue();
	}
	
	@Override
	protected void processAllParameters() {
		super.processAllParameters();
		processMinimumChange();
		processMaximumChange();
	}

	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Random Variable";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 4.5);
		g.setBackgroundHeight(backgroundheight);

		variableName.createUI(id, "name", localx, localy + (0 * localoffset), width, g);
		min.createUI(id, "minimum", localx, localy + (1 * localoffset), width, g);
		max.createUI(id, "maximum", localx, localy + (2 * localoffset), width, g);
		shouldRepeat.createUI(id, "once - repeat", localx, localy + (3 * localoffset), width, g);

		return g;
	}
	
	public RandomBBTask clone_it() {
		  RandomBBTask clone = new RandomBBTask(this.p, this.cp5, this.name);
		  
		  clone.variableName	= this.variableName;
		  clone.min 			= this.min;
		  clone.max 			= this.max;
		  clone.value 			= this.value;
		  clone.timerMilestone 	= this.timerMilestone;
		  clone.timer          	= this.timer;
		  
		  return clone;
	  }
}
