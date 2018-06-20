package frontend.tasks;


import java.io.Serializable;
import processing.core.PApplet;
import controlP5.*;
import frontend.Blackboard;
import frontend.Expression;
import frontend.Main;
import frontend.State;
import frontend.StateMachine;
import frontend.Status;
import frontend.tasks.blackboard.SetBBTask;
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
  public String name; //@TODO THIS SHOULD BE AN ID INSTEAD!!!
  protected String group_gui_id;
  protected boolean repeat;
  protected boolean first_time;
  
//  public static final String userInputAsDefault = "(USER INPUT)";
  public static final int defaultColor = ControlP5Constants.THEME_CP52014.getBackground();
  
  //UI variables
  protected String textlabel;  
  protected int backgroundheight;// 	= (int)(font_size* 12.5);
  protected int localoffset;// 		= 3*font_size;
  protected int localx;// 			= 10;
  protected int localy;// 			= (int)(font_size);
  
  @Deprecated
  static protected int font_size;//			= (int)(((ZenStates)p).get_font_size());
  
  //transient variables
  @Deprecated
  transient protected PApplet  p;
  @Deprecated
  transient protected ControlP5 cp5;

  public Task (PApplet p, ControlP5 cp5, String taskname) {
    this.p = p;
    this.cp5 = cp5;
    this.name   = taskname;
    this.repeat = true;
    this.status = Status.INACTIVE;
    this.group_gui_id = UUID.randomUUID().toString();
    this.first_time = true;
    
    if (((Main)p).debug())
    	System.out.println("task " + this.toString() + " created!");
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

  public boolean evaluateAsBoolean (Object o) throws ScriptException {
	  return Boolean.parseBoolean(this.evaluateAsString(o));
  }
  
  public float evaluateAsFloat (Object o) throws ScriptException {
	  return Float.parseFloat(this.evaluateAsString(o));
  }
  
  public int evaluateAsInteger (Object o) throws ScriptException {
	  return Integer.parseInt(this.evaluateAsString(o));
  }
  
  public String evaluateAsString (Object o) throws ScriptException {
	  return this.evaluate_value(o).toString();
  }
  
  //function that tries to evaluates the value (if necessary) and returns the real value
  public Object evaluate_value (Object o) throws ScriptException {
    Object ret = o;
    Blackboard board = Main.instance().board();

    // If added an expression, process it and save result in blackboard.
    if (o instanceof Expression) 
        ret = ((Expression)o).eval(board);

    return ret;
  }
  
  @Deprecated
  public Object old_evaluate_value (Object o){
    Object ret = o;
    Blackboard board = Main.instance().board();

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
  public abstract Task clone_it();

  
  public void closeTask() {
	  p.println("removing task " + get_gui_id());
	  cp5.getGroup(get_gui_id()).remove();
  }

  //////////////////////////////
	// gui commands
  @Deprecated
	protected void check_repeat_toggle(String s, CallbackEvent theEvent) {
		System.out.println("repeat callback!");

		float value = theEvent.getController().getValue();
		
		if (value == 0.0) {
			this.repeat = false; // once
			this.first_time = true;
		} else
			this.repeat = true; // repeat
	}
  
@Deprecated
  protected CallbackListener callbackRepeatToggle() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				check_repeat_toggle(theEvent.getController().getName(), theEvent);
			}
		};
  }
  
  @Deprecated
  private CallbackListener callbackEmptyWhenUsingUserInput(String target) {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {

				// if this group is not open, returns...
				if (!((Group) cp5.get(get_gui_id())).isOpen())
					return;

				String content = theEvent.getController().getValueLabel().getText();
				
				if (content.trim().equals(ComputableFloatTextfieldUIWithUserInput.userInputAsDefault)) 
					((Textfield) cp5.get(get_gui_id() + "/" + target)).setText("");
			}
		};
	}
  
  @Deprecated
  protected Textfield createGuiTextField(String target, int localx, int localy, int w, Group g, CallbackListener callback) {
		return (cp5.addTextfield(get_gui_id() + "/" + target)
		.setPosition(localx, localy)
		.setSize(w, (int) (font_size * 1.25))
		.setGroup(g)
		.setAutoClear(false)
		.setLabel(target)
		.align(ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
		.onClick(callbackEmptyWhenUsingUserInput(target))
		.onChange(callback)
		.onReleaseOutside(callback));
	}
  
  @Deprecated
  protected ScrollableList createScrollableList(String name, List list, int localx, int localy, int w, Group g, CallbackListener callback) {
		return cp5.addScrollableList(get_gui_id() + "/" + name)
		.setPosition(localx, localy)
		.setLabel(name)
		.setSize(w, 100)
		.setGroup(g)
		.setDefaultValue(2)
		.close()
		.setValue(1)
		.setBarHeight(20)
		.setItemHeight(20)
		.align(ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
		.onChange(callback)
		.setType(ControlP5.DROPDOWN)
		.addItems(list);
	}


  @Deprecated
  protected void createGuiToggle (int x, int y, int w, Group g, CallbackListener callback) {
    cp5.addToggle(get_gui_id()+"/repeat")
       .setPosition(x, y)
       .setSize(w, (int)(font_size*1.25))
       .setGroup(g)
       .setMode(ControlP5.SWITCH)
       .setLabel("repeat -  once")
       .setValue(this.repeat)
       .onChange(callback)
       .onReleaseOutside(callback)
       .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
       ;
  }
  
  
  public Group load_gui_elements(State s) {

		setup_ui_variables();
		
	    String g_name = this.get_gui_id();

	    Group g = cp5.addGroup(g_name)
	    .setHeight((int) (font_size*1.5f))
	    //.setWidth((10*((Main)p).FONT_SIZE))
	    .setWidth(10*font_size)
	    .setColorBackground(p.color(255, 50)) //color of the task
	    .setBackgroundColor(p.color(255, 25)) //color of task when openned
	    ;

	    
	    
	    g.setLabel(textlabel);
	    g.getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);
	    
	    return g;
  }
  
  void setup_ui_variables() {
	  font_size			= (int)(((Main)p).get_font_size());
	  localoffset 		= (int) (4*font_size);
	  localx 			= 10;
	  localy 			= (int)(font_size);
  }

  //abstract CallbackListener generate_callback_leave(){}
  public abstract CallbackListener generate_callback_enter();
  public abstract void reset_gui_fields();
}
