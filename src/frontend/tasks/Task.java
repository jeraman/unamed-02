package frontend.tasks;

import java.io.Serializable;
import processing.core.PApplet;
import soundengine.SoundEngine;
import controlP5.*;
import frontend.Main;
import frontend.core.Blackboard;
import frontend.core.Expression;
import frontend.core.State;
import frontend.core.StateMachine;
import frontend.core.Status;
import frontend.tasks.blackboard.DefaultBBTask;
import frontend.tasks.meta.OSCTask;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableFloatTextfieldUIWithUserInput;

import javax.script.*;

import java.util.List;
import java.util.UUID;

////////////////////////////////////////
//this is the abstract class every task needs to implement
public abstract class Task implements Serializable {
	protected Status status;
	public String name; // @TODO THIS SHOULD BE AN ID INSTEAD!!!
	protected String group_gui_id;
	protected boolean repeat;
	protected boolean first_time;

	public static final int defaultColor = ControlP5Constants.THEME_CP52014.getBackground();

	// UI variables
	protected String textlabel;
	protected int backgroundheight;// = (int)(font_size* 12.5);
	protected int localoffset;// = 3*font_size;
	protected int localx;// = 10;
	protected int localy;// = (int)(font_size);
	
	transient public SoundEngine eng;
	transient private Group group;

	@Deprecated
	public static int font_size;// = (int)(((ZenStates)p).get_font_size());

	// transient variables
	transient protected PApplet p;
	transient protected ControlP5 cp5;

	public Task(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		this.p = p;
		this.cp5 = cp5;
		this.name = taskname;
		this.repeat = true;
		this.status = Status.INACTIVE;
		this.group_gui_id = UUID.randomUUID().toString();
		this.first_time = true;
		this.eng = eng;

		if (((Main) p).debug())
			System.out.println("task " + this.toString() + " created!");
	}
	
	public void build(PApplet p, ControlP5 cp5, SoundEngine eng) {
		this.p = p;
		this.cp5 = cp5;
		this.eng = eng;
	}

	public void set_name(String newname) {
		this.name = newname;
	}

	public void reset_group_id() {
		this.group_gui_id = UUID.randomUUID().toString();
	}

	public String get_name() {
		return this.name;
	}

	public String get_gui_id() {
		return this.group_gui_id;
	}

	public Status get_status() {
		return this.status;
	}


	public void reset_first_time() {
		this.first_time = true;
	}

	@Deprecated
	public void refresh() {
		//this.stop();
	}
	
	@Deprecated
	public void interrupt() {
		this.stop();
		this.reset_first_time();
		this.status = Status.DONE;
	}

	public String get_prefix() {
		String result = "[TASK]";

		if (this instanceof DefaultBBTask)
			result = "[B_B]";
		if (this instanceof OSCTask)
			result = "[OSC]";
		if (this instanceof StateMachine)
			result = "[S_M]";

		// create other according to the type of the task

		return result;
	}

	// check if this task should be executed repeatedly or only once
	public boolean should_run() {

		boolean result = true;
		
		// checkes if this should be executed only once
		if (!repeat) {
			// if this is the first time, go on
			if (first_time)
				first_time = false;
			// if it's not the first time, do not execute anything
			else {
				result = false;
				this.status = Status.DONE;
			}
		} else
			if (first_time)
				first_time = false;

		return result;
	}

	// these method should be reimplemented
	public void run() {
		if (!should_run())
			return;
		processAllParameters();
	}

	protected abstract String[] getDefaultParameters();

	protected abstract void processAllParameters();
	
	public abstract void start();
	
	public abstract void stop();

	public abstract Task clone_it();
	
	public void removeElementUi() {
		System.out.println("removing task " + get_gui_id());
		cp5.getGroup(get_gui_id()).remove();
	}

	public void closeTask() {
		removeElementUi();
		this.stop();
	}

	//////////////////////////////
	// gui commands

	public Group load_gui_elements(State s) {
		setup_ui_variables();

		String g_name = this.get_gui_id();

		this.group = cp5.addGroup(g_name).setHeight((int) (font_size * 1.5f))
				.setWidth(10 * font_size)
				.setColorBackground(p.color(255, 50))
				.setBackgroundColor(p.color(255, 25));

		this.group.setLabel(textlabel);
		this.group.getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);
	
		return this.group;
	}

	private void setup_ui_variables() {
		font_size = (int) (((Main) p).get_font_size());
		localoffset = (int) (4 * font_size);
		localx = 10;
		localy = (int) (font_size);
	}

	@Deprecated
	public abstract void reset_gui_fields();

}
