package frontend.tasks.meta;



import controlP5.*;
import frontend.Expression;
import frontend.Main;
import frontend.State;
import frontend.tasks.RemoteOSCTask;
import frontend.tasks.Task;
import frontend.tasks.generators.OscillatorGenTask;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ToggleUi;
import processing.core.PApplet;


public class DMXTask extends Task {
	private ComputableIntegerTextfieldUI channel;
	private ComputableIntegerTextfieldUI intensity;
	private ComputableIntegerTextfieldUI rate;
	private ComputableIntegerTextfieldUI duration;
	private ToggleUi shouldRepeat;

	public DMXTask(PApplet p, ControlP5 cp5, String id) {
		super(p, cp5, id);

		this.channel = new ComputableIntegerTextfieldUI(0);
		this.intensity = new ComputableIntegerTextfieldUI(255);
		this.duration = new ComputableIntegerTextfieldUI(255);
		this.rate = new ComputableIntegerTextfieldUI(255);
		this.shouldRepeat = new ToggleUi();

	}

	@Override
	protected String[] getDefaultParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	private void processChannelChange() {
		if (channel.update())
			System.out.println(this.get_gui_id() + " changes channel " + channel.getValueAsInt());
	}
	private void processIntensityChange() {
		if (intensity.update())
			System.out.println(this.get_gui_id() + " changes intensity " + intensity.getValueAsInt());
	}
	private void processRateChange() {
		if (rate.update())
			System.out.println(this.get_gui_id() + " changes rate " + rate.getValueAsInt());
	}
	private void processDurationChange() {
		if (duration.update())
			System.out.println(this.get_gui_id() + " changes duration " + duration.getValueAsInt());
	}

	protected void processAllParameters() {
		this.processChannelChange();
		this.processIntensityChange();
		this.processRateChange();
		this.processDurationChange();
	}
	
	public void run() {
		boolean wasFirstTime = first_time;
		super.run();
		if (shouldRepeat.getValue() || wasFirstTime)
			sendDmxMessage();
	}

	private void sendDmxMessage() {
		// TODO Auto-generated method stub
		System.out.println("stub sendDmxMessage method");
	}

	@Override
	public Task clone_it() {
		DMXTask clone = new DMXTask(this.p, this.cp5, this.name);
		clone.channel = this.channel;
		clone.intensity = this.intensity;
		clone.rate = this.rate;
		clone.duration = this.duration;
		return clone;
	}

	@Override
	public void reset_gui_fields() {
		// TODO Auto-generated method stub
		
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "DMX Light";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 5.2);
		g.setBackgroundHeight(backgroundheight);
		
		channel.createUI(id, "channel", localx, localy + (0 * localoffset), width, g);
		intensity.createUI(id, "intensity", localx, localy + (1 * localoffset), width, g);
		rate.createUI(id, "rate", localx, localy + (2 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + (3 * localoffset), width, g);
		shouldRepeat.createUI(id, "once - repeat", localx, localy + (4 * localoffset), width, g);
		
		return g;
	}
	
	
}

/*
////////////////////////////////////////
//implementing a task for OSC messages
public class ControlRemoteDMXTask extends RemoteOSCTask {

  Object channel;
  Object intensity;
  Object rate;
  Object duration;

  //contructor loading the file
  public ControlRemoteDMXTask (PApplet p, ControlP5 cp5, String id) {
    super(p, cp5, id);

    this.message   = "/dmx/control";
    this.channel  = new Expression("1");
    this.intensity = new Expression("0");
    this.duration  = new Expression("0");
    this.rate      = new Expression("0");

    update_content();
  }

  //contructor loading the file
  public ControlRemoteDMXTask (PApplet p, ControlP5 cp5, String id, String m, Object c, Object i, Object d, Object r, boolean repeat) {
	  super(p, cp5, id);
	  
	  this.message   = m;
	  this.channel   = c;
	  this.intensity = i;
	  this.duration  = d;
	  this.rate      = r;
	  this.repeat	 = repeat;
	  
	  update_content();
  }

  public ControlRemoteDMXTask clone_it () {
    return new ControlRemoteDMXTask(this.p, this.cp5, this.name, this.message, this.channel, this.intensity, this.duration, this.rate, this.repeat);
  }

  void update_content () {
    this.content  = new Object[] {this.channel, this.intensity, this.rate, this.duration};
  }

  void update_channel (String u) {
    this.channel = new Expression(u);
    update_content();
  }

  void update_intensity (String i) {
    this.intensity = new Expression(i);
    update_content();
  }

  void update_rate (String r) {
    this.rate = new Expression(r);
    update_content();
  }

  void update_duration(String d) {
    this.duration = new Expression(d);
    update_content();
  }


  //UI config
  public Group load_gui_elements(State s) {
	  Group g					= super.load_gui_elements(s);
	  CallbackListener cb_enter = generate_callback_enter();
	  String g_name			  	= this.get_gui_id();
	  int w 					= g.getWidth()-(localx*2);
	  
	  textlabel 	 			= "Control DMX";
	  backgroundheight 			= (int)(font_size* 16.5);
	    
	  g.setBackgroundHeight(backgroundheight);
	  g.setLabel(textlabel);

    
    cp5.addTextfield(g_name+ "/channel")
      .setPosition(localx, localy)
      .setSize(w, (int)(font_size*1.25))
      .setGroup(g)
      .setAutoClear(false)
      .setLabel("channel")
      .setText(this.channel.toString())
      .align(ControlP5.CENTER, ControlP5.CENTER,ControlP5.CENTER, ControlP5.CENTER)
      .onChange(cb_enter)
      .onReleaseOutside(cb_enter)
      .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
    ;

    cp5.addTextfield(g_name+ "/intensity")
      .setPosition(localx, localy+(1*localoffset))
      .setSize(w, (int)(font_size*1.25))
      .setGroup(g)
      .setAutoClear(false)
      .setLabel("intensity")
      .setText(this.intensity.toString())
      .align(ControlP5.CENTER, ControlP5.CENTER,ControlP5.CENTER, ControlP5.CENTER)
      .onChange(cb_enter)
      .onReleaseOutside(cb_enter)
      .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
    ;

    cp5.addTextfield(g_name+ "/rate")
      .setPosition(localx, localy+(2*localoffset))
      .setSize(w, (int)(font_size*1.25))
      .setGroup(g)
      .setAutoClear(false)
      .setLabel("rate")
      .setText(this.rate.toString())
      .align(ControlP5.CENTER, ControlP5.CENTER,ControlP5.CENTER, ControlP5.CENTER)
      .onChange(cb_enter)
      .onReleaseOutside(cb_enter)
      .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
    ;

    cp5.addTextfield(g_name+ "/duration")
      .setPosition(localx, localy+(3*localoffset))
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

    createGuiToggle(localx, localy+(4*localoffset), w, g, callbackRepeatToggle());

    return g;
  }

  public CallbackListener generate_callback_enter() {
    return new CallbackListener() {
          public void controlEvent(CallbackEvent theEvent) {

            //if this group is not open, returns...
            if (!((Group)cp5.get(get_gui_id())).isOpen()) return;

            String s = theEvent.getController().getName();

            if (s.equals(get_gui_id() + "/channel")) {
                String nv = theEvent.getController().getValueLabel().getText();
                if (nv.trim().equals("")) {
                  nv="0";
                  ((Textfield)cp5.get(get_gui_id()+ "/channel")).setText(nv);
                }
                update_channel(nv);
            }

            if (s.equals(get_gui_id() + "/intensity")) {
                String nv = theEvent.getController().getValueLabel().getText();
                if (nv.trim().equals("")) {
                  nv="0";
                  ((Textfield)cp5.get(get_gui_id()+ "/intensity")).setText(nv);
                }
                update_intensity(nv);
            }

            if (s.equals(get_gui_id() + "/rate")) {
                String nv = theEvent.getController().getValueLabel().getText();
                if (nv.trim().equals("")) {
                  nv="0";
                  ((Textfield)cp5.get(get_gui_id()+ "/rate")).setText(nv);
                }
                update_rate(nv);
            }

            if (s.equals(get_gui_id() + "/duration")) {
                String nv = theEvent.getController().getValueLabel().getText();
                if (nv.trim().equals("")) {
                  nv="0";
                  ((Textfield)cp5.get(get_gui_id()+ "/duration")).setText(nv);
                }
                update_duration(nv);
            }

          }
    };
  }

  public void reset_gui_fields() {
    String g_name = this.get_gui_id();
    String nv;

    //if this group is not open, returns...
    if (!((Group)cp5.get(get_gui_id())).isOpen()) return;

    nv = ((Textfield)cp5.get(g_name+"/channel")).getText();
    update_channel(nv);
    nv = ((Textfield)cp5.get(g_name+"/intensity")).getText();
    update_intensity(nv);
    nv = ((Textfield)cp5.get(g_name+"/rate")).getText();
    update_rate(nv);
    nv = ((Textfield)cp5.get(g_name+"/duration")).getText();
    update_duration(nv);
  }
  
	@Override
	protected void processAllParameters() {
		// TODO Auto-generated method stub
	}
	
	protected String[] getDefaultParameters(){
		// TODO Auto-generated method stub
		return null;
	}

}

*/
