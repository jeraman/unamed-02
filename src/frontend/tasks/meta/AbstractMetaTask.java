package frontend.tasks.meta;

import controlP5.ControlP5;
import frontend.core.Status;
import frontend.tasks.Task;
import processing.core.PApplet;
import soundengine.SoundEngine;

abstract class AbstractMetaTask extends Task {

	public AbstractMetaTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
	}
	
	@Override
	protected String[] getDefaultParameters() {
		return null;
	}

	public void start() {
		this.status = Status.RUNNING;
		System.out.println("starting the following MetaTask: " + this);
	}

	public void stop() {
		this.status = Status.DONE;
		System.out.println("stopping the following DMX task " + this);
	}
	
	@Override
	public void reset_gui_fields() {
	}
	
}
