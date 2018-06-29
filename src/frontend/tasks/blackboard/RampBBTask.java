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
		this.value = new TextfieldUi("");
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

/*
public class RampBBTask extends DefaultBBTask {
  protected Object duration;
  protected Object amplitude;
  protected boolean is_up;

  public RampBBTask (PApplet p, ControlP5 cp5) {
    super(p, cp5, ("ramp_" + (int)p.random(0, 100)), new Expression("1"));

    this.is_up = true;

    update_duration("1");
    update_amplitude("1");
  }
  
  //clone function
  public RampBBTask clone_it() {
	  RampBBTask clone = new RampBBTask(this.p, this.cp5);
	  
	  clone.variableName	= this.variableName;
	  clone.duration 		= this.duration;
	  clone.amplitude 		= this.amplitude;
	  clone.is_up 			= this.is_up;
	  clone.value 			= this.value;
	  clone.timerMilestone 	= this.timerMilestone;
	  clone.timer          	= this.timer;
	  clone.repeat			= this.repeat;
	  
	  return clone;
  }
  

  void update_duration(String v) {
    this.duration = new Expression(v);
  }

  void update_amplitude(String v) {
    this.amplitude = new Expression(v);
  }
  
  
  public void run() { 
    if (!should_run()) return;
    
    String dur_val = old_evaluate_value(this.duration).toString();
    String amp_val = old_evaluate_value(this.amplitude).toString();

    Expression ne;
    
    if (is_up) ne = new Expression(amp_val+"*math.abs(("+timer+"/"+dur_val+") % 1)");
    else       ne = new Expression("math.abs("+amp_val+"-("+amp_val+"*(("+timer+"/"+dur_val+") % 1)))");

    Blackboard board = Main.instance().board();
    this.status = Status.RUNNING;
    
    Object result = old_evaluate_value(ne);
    
    board.put(variableName, result);
    
	this.status = Status.DONE;
	
    //if (is_up && timer >= (Float.parseFloat(amp_val))-0.1)
    //	this.status = Status.DONE;
    //if (!is_up && timer >= 0.1)
    //	this.status = Status.DONE;
    
  }
  
  public void stop() {
	  super.stop();
	  //this.reset_timer();
  }

  //UI config
  public Group load_gui_elements(State s) {

	  Group g					= super.load_gui_elements(s);
	  CallbackListener cb_enter = generate_callback_enter();
	  String g_name			  	= this.get_gui_id();
	  int w 					= g.getWidth()-(localx*2);
	  
	  textlabel 	 			= "Ramp variable";
	  backgroundheight 			= (int)(font_size* 16.5);
	    
	  g.setBackgroundHeight(backgroundheight);
	  g.setLabel(textlabel);


    cp5.addTextfield(g_name+ "/name")
      .setPosition(localx, localy)
      .setSize(w, (int)(font_size*1.25))
      .setGroup(g)
      .setAutoClear(false)
      .setLabel("name")
      .setText(this.variableName)
      .onChange(cb_enter)
      .onReleaseOutside(cb_enter)
      .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE);
    ;

    // create a toggle
    cp5.addToggle(get_gui_id()+"/type")
       .setPosition(localx, localy+(1*localoffset))
       .setSize(w, (int)(font_size*1.25))
       .setGroup(g)
       .setMode(ControlP5.SWITCH)
       .setLabel("up              down")
       .setValue(this.is_up)
       .onChange(cb_enter)
       .onReleaseOutside(cb_enter)
       .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
       ;

    cp5.addTextfield(g_name+ "/duration")
      .setPosition(localx, localy+(2*localoffset))
      .setSize(w, (int)(font_size*1.25))
      .setGroup(g)
      .setAutoClear(false)
      .setLabel("duration")
      .setText(this.duration.toString())
      .align(ControlP5.CENTER, ControlP5.CENTER,ControlP5.CENTER, ControlP5.CENTER)
      .onChange(cb_enter)
      .onReleaseOutside(cb_enter)
      .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
    ;

    cp5.addTextfield(g_name+ "/amplitude")
      .setPosition(localx, localy+(3*localoffset))
      .setSize(w, (int)(font_size*1.25))
      .setGroup(g)
      .setAutoClear(false)
      .setLabel("amplitude")
      .setText(this.amplitude.toString())
      .align(ControlP5.CENTER, ControlP5.CENTER,ControlP5.CENTER, ControlP5.CENTER)
      .onChange(cb_enter)
      .onReleaseOutside(cb_enter)
      .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
    ;

    createGuiToggle(localx, localy+(4*localoffset), w, g, callbackRepeatToggle());

    return g;
  }

  public CallbackListener generate_callback_enter() {
    return new CallbackListener() {
          public void controlEvent(CallbackEvent theEvent) {

            //if this group is not open, returns...
            if (!((Group)cp5.get(get_gui_id())).isOpen()) return;

            String s = theEvent.getController().getName();

            if (s.equals(get_gui_id() + "/name")) {
                String text = theEvent.getController().getValueLabel().getText();
                //if the name is empty, resets
                if (text.trim().equalsIgnoreCase("")) {
                  ((Textfield)cp5.get(get_gui_id() + "/name")).setText(name);
                  return;
                }
                update_variable_name(text);
                //System.out.println(s + " " + text);
            }

            if (s.equals(get_gui_id() + "/type")) {
                float value = theEvent.getController().getValue();
                if (value==0.0)  is_up = false; //once
                else             is_up = true;  //repeat
            }

            if (s.equals(get_gui_id() + "/duration")) {
                String nv = theEvent.getController().getValueLabel().getText();
                if (nv.trim().equals("")) {
                  nv="1";
                  ((Textfield)cp5.get(get_gui_id()+ "/duration")).setText(nv);
                }
                update_duration(nv);
            }

            if (s.equals(get_gui_id() + "/amplitude")) {
                String nv = theEvent.getController().getValueLabel().getText();
                if (nv.trim().equals("")) {
                  nv="1";
                  ((Textfield)cp5.get(get_gui_id()+ "/amplitude")).setText(nv);
                }
                update_amplitude(nv);
            }

          }
    };
  }
  

  public void reset_gui_fields() {
	  String g_name = this.get_gui_id();
	  String nv;

	  //if this group is not open, returns...
	  if (!((Group)cp5.get(get_gui_id())).isOpen()) return;

	  nv = ((Textfield)cp5.get(g_name+"/name")).getText();
	  update_variable_name(nv);
	  nv = ((Textfield)cp5.get(g_name+"/duration")).getText();
	  update_duration(nv);
	  nv = ((Textfield)cp5.get(g_name+"/amplitude")).getText();
	  update_amplitude(nv);

  }

}
*/
