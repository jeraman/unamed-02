package frontend.tasks.generators;

import controlP5.ControlP5;
import frontend.Main;
import frontend.core.Status;
import frontend.tasks.Task;
import processing.core.PApplet;
import soundengine.SoundEngine;

abstract class AbstractGenTask extends Task {

	public AbstractGenTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
	}
	
	public abstract void addToEngine();
	
	public void removeFromEngine() {
		this.eng.removeGenerator(this.get_gui_id());
	}
	
	public void closeTask() {
		this.stop();
		super.closeTask();
	}
	
	public void start() {
//		this.status = Status.RUNNING;
		this.addToEngine();
	}
	
	public void stop() {
//		this.status = Status.DONE;
		this.removeFromEngine();
	}
	
	public void reset_gui_fields() {
		
	}

}
