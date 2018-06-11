package ui.tasks;


import java.io.Serializable;
import processing.core.PApplet;
import ui.Blackboard;
import ui.Expression;
import ui.State;
import ui.StateMachine;
import ui.Status;
import ui.ZenStates;
import controlP5.*;
import javax.script.*;

import java.util.UUID;

////////////////////////////////////////
//this is the abstract class every task needs to implement
public abstract class Task implements Serializable {
  protected Status status;
  public String name; //@TODO THIS SHOULD BE AN ID INSTEAD!!!
  protected String group_gui_id;
  protected boolean repeat;
  protected boolean first_time;
  
  
  //UI variables
  protected String textlabel;
  protected int font_size;//			= (int)(((ZenStates)p).get_font_size());
  protected int backgroundheight;// 	= (int)(font_size* 12.5);
  protected int localoffset;// 		= 3*font_size;
  protected int localx;// 			= 10;
  protected int localy;// 			= (int)(font_size);
  
  //transient variables
  transient protected PApplet  p;
  transient protected ControlP5 cp5;


  public Task (PApplet p, ControlP5 cp5, String taskname) {
    this.p = p;
    this.cp5 = cp5;
    this.name   = taskname;
    this.repeat = true;
    this.status = Status.INACTIVE;
    this.group_gui_id = UUID.randomUUID().toString();
    this.first_time = true;
    
    if (((ZenStates)p).debug())
    	System.out.println("task " + this.toString() + " created!");
  }
  /*
  public Task (PApplet p, String taskname) {
    //this.p = p;
    this.name   = taskname;
    this.status = Status.INACTIVE;

    println("task " + this.toString() + " created!");
  }
  */

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

  /*
  //method that generates a random name for the demo task
  String generate_random_group_id(State s) {
    this.group_gui_id = ("/" + s.get_name() + "/"+ this.getClass() + "/"+ ((int)p.random(-100, 100)));
    return this.group_gui_id;
  }

  String get_gui_id() {
    return this.group_gui_id;
  }

  void set_gui_id(String g_name) {
    this.group_gui_id = g_name;
  }
  */


  public Status get_status () {
    return this.status;
  }

  public void refresh() {
    this.stop();
  }

  public void reset_first_time() {
    this.first_time = true;
  }

  public void interrupt() {
    this.stop();
    this.reset_first_time();
    this.status = Status.DONE;
  }

  public String get_prefix() {
    String result = "[TASK]";

    if (this instanceof SetBBTask) result = "[B_B]";
    if (this instanceof OSCTask) result = "[OSC]";
    if (this instanceof StateMachine) result = "[S_M]";

    //create other according to the type of the task

    return result;
  }

  //function that tries to evaluates the value (if necessary) and returns the real value
  public Object evaluate_value (Object o) {
    Object ret = o;
    Blackboard board = ZenStates.instance().board();

    // If added an expression, process it and save result in blackboard.
    if (o instanceof Expression) {
      try {
        ret = ((Expression)o).eval(board);
      }
      catch (ScriptException e) {
        System.out.println("ScriptExpression thrown, unhandled update.");
      }
    }

    return ret;
  }

  //check if this task should be executed repeatedly or only once
  public boolean should_run () {

    boolean result = true;
    //checkes if this should be executed only once
    if (!repeat) {
      //if this is the first time, go on
      if (first_time) first_time = false;
      //if it's not the first time, do not execute anything
      else {
    	  result = false;
    	  this.status = Status.DONE;
      }
    } else
    	//if this is the first time, go on
        if (first_time) first_time = false;

    return result;
  }

  public void stop () {

  }
  //these method should be reimplemented
  public abstract void run();
  public abstract void build(PApplet p, ControlP5 cp5);
  public abstract void update_status();
  //abstract void stop();
  public abstract Task clone_it();


  //////////////////////////////
  //gui commands
  protected void check_repeat_toggle(String s, CallbackEvent theEvent) {
    if (s.equals(get_gui_id() + "/repeat")) {
        float value = theEvent.getController().getValue();
        if (value==0.0)  {
          this.repeat = false; //once
          this.first_time = true;
        } else             this.repeat = true;  //repeat
    }
  }

  protected void create_gui_toggle (int x, int y, int w, Group g, CallbackListener cb) {
	 int font_size 	 = (int)(((ZenStates)p).get_font_size());
	  
	// create a toggle
    cp5.addToggle(get_gui_id()+"/repeat")
       .setPosition(x, y)
       .setSize(w, (int)(font_size*1.25))
       .setGroup(g)
       .setMode(ControlP5.SWITCH)
       .setLabel("repeat -  once")
       .setValue(this.repeat)
       .onChange(cb)
       .onReleaseOutside(cb)
       .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
       ;
  }
  
  public Group load_gui_elements(State s) {

		setup_ui_variables();
		
	    String g_name = this.get_gui_id();

	    //String textlabel = "Control aura";
	    //int backgroundheight = (int)(font_size* 10.5);

	    Group g = cp5.addGroup(g_name)
	    .setHeight(font_size)
	    .setWidth((10*((ZenStates)p).FONT_SIZE))
	    //.setBackgroundHeight(backgroundheight)
	    .setColorBackground(p.color(255, 50)) //color of the task
	    .setBackgroundColor(p.color(255, 25)) //color of task when openned
	    //.setLabel(textlabel)
	    ;

	    g.getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);
	    
	    return g;
  }
  
  void setup_ui_variables() {
	  font_size			= (int)(((ZenStates)p).get_font_size());
	  localoffset 		= 3*font_size;
	  localx 			= 10;
	  localy 			= (int)(font_size);
  }

  //abstract CallbackListener generate_callback_leave(){}
  public abstract CallbackListener generate_callback_enter();
  public abstract void reset_gui_fields();
  //abstract void draw_gui();
}
