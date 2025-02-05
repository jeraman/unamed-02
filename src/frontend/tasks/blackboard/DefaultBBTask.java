package frontend.tasks.blackboard;

import javax.script.ScriptException;

import controlP5.*;
import frontend.Main;
import frontend.ZenStates;
import frontend.core.Blackboard;
import frontend.core.Expression;
import frontend.core.State;
import frontend.core.Status;
import frontend.tasks.Task;
import frontend.tasks.generators.OscillatorGenTask;
import frontend.ui.TextfieldUi;
import frontend.ui.ToggleUi;
import processing.core.PApplet;
import soundengine.SoundEngine;
import soundengine.util.Util;


public class DefaultBBTask extends AbstractBBTask {

	public DefaultBBTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
		this.value = new TextfieldUi("0");
		
		Main.log.countDefaultBBTask();
	}
	
	@Override
	public Task clone_it() {
		DefaultBBTask clone = new DefaultBBTask(this.p, this.cp5, this.name, this.eng);
		clone.variableName = this.variableName;
		clone.value = this.value;
		clone.timer = this.timer;
		clone.timerMilestone = this.timerMilestone;
		return clone;
	}
	
	public void updateVariable() {
		Blackboard board = ZenStates.board();
		board.put(variableName.getValue(), value.evaluate());
	}
	
	public boolean isFirstCycle() {
		return first_time;
	}

	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Blackboard Variable";
		
		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);
		
		this.backgroundheight = (int) (localoffset * 3.5);
		g.setBackgroundHeight(backgroundheight);

		variableName.createUI(id, "name", localx, localy + (0 * localoffset), width, g);
		value.createUI(id, "value", localx, localy + (1 * localoffset), width, g);
		shouldRepeat.createUI(id, "once - repeat", localx, localy + (2 * localoffset), width, g);
		
		return g;
	}
}