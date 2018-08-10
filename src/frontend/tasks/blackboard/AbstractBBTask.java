package frontend.tasks.blackboard;

import controlP5.ControlP5;
import frontend.Main;
import frontend.ZenStates;
import frontend.core.Blackboard;
import frontend.core.Status;
import frontend.tasks.Task;
import frontend.ui.TextfieldUi;
import frontend.ui.ToggleUi;
import processing.core.PApplet;
import soundengine.SoundEngine;
import soundengine.util.Util;



abstract class AbstractBBTask extends Task {
	
	protected TextfieldUi variableName;
	protected TextfieldUi value;
	protected ToggleUi shouldRepeat;
	float timer;
	float timerMilestone;
	
	public AbstractBBTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
		
		this.variableName = new TextfieldUi(taskname);
		this.shouldRepeat = new ToggleUi();
		
		this.timerMilestone = 0;
		this.timer = 0;
	}
	
	protected abstract boolean isFirstCycle();
	
	protected boolean debug() {
		return ZenStates.debug;
	}
	
	
	public void start() {
//		this.status = Status.RUNNING;
		if(debug())
			System.out.println("starting the following AbstractBBTask: " + this);
	}
	
	public void stop() {
//		this.status = Status.DONE;
		if(debug())
			System.out.println("stopping the following AbstractBBTask: " + this);
	}

	
	private void processNameChange() {
		variableName.update();
	}
	
	private void processValueChange() {
		value.update();
	}

	protected void processAllParameters() {
		processNameChange();
		processValueChange();
	}
	
	public void run() {
		
		boolean isFirstCycle  = this.isFirstCycle();

		if (first_time)
			reset_timer();
		
		super.run();	

		if (shouldRepeat.getValue() || isFirstCycle) {
			updateTimer();
			updateVariable();
		}
		
	}

	void updateTimer() {
		this.timer = ((float) Util.millis() / 1000f) - timerMilestone;
	}

	void reset_timer() {
		this.timerMilestone = (float) Util.millis() / 1000f;
		this.timer = 0;
	}

	public void updateVariable() {
		Blackboard board = ZenStates.board();
		board.put(variableName.getValue(), value.evaluateAsFloat());
	}
	
	@Override
	protected String[] getDefaultParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset_gui_fields() {
		// TODO Auto-generated method stub
		
	}
}
