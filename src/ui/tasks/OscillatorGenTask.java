package ui.tasks;

import java.util.Arrays;
import java.util.List;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textfield;
import processing.core.PApplet;
import ui.Expression;
import ui.Main;
import ui.State;
import ui.Status;

public class OscillatorGenTask extends Task {

	private static final List<String> list = Arrays.asList("PHASOR", "QUATERPULSE", "SAW", "SINE", "SQUARE",
			"TRIANGLE");

	private String wavetype;
	private Object frequency;
	private Object amplitude;
	private Object duration;

	
	
	public OscillatorGenTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		
		update_frequency(Task.userInputAsDefault);
		update_amplitude(Task.userInputAsDefault);
		update_duration(Task.userInputAsDefault);
		update_wavetype("SINE");

		Main.eng.addGenerator(this.get_gui_id(), "OSCILLATOR", getDefaultParameters());
	}
	
	private String[] getDefaultParameters(){
		return new String[] { "0", "0", "SINE"};
	}

	void update_frequency(String v) {
		this.frequency = new Expression(v);
	}

	void update_amplitude(String v) {
		this.amplitude = new Expression(v);
	}

	void update_wavetype(String wt) {
		this.wavetype = wt;
	}

	void update_duration(String v) {
		this.duration = new Expression(v);
	}

	String getFrequencyString() {
		return this.frequency + "";
	}

	String getAmplitudeString() {
		return this.amplitude + "";
	}

	String getDurationString() {
		return this.duration + "";
	}

	String getWavetype() {
		return this.wavetype + "";
	}

	@Override
	public void build(PApplet p, ControlP5 cp5) {
		// TODO Auto-generated method stub
		this.p = p;
		this.cp5 = cp5;
	}

	@Override
	public void update_status() {
	}

	@Override
	public Task clone_it() {
		OscillatorGenTask clone = new OscillatorGenTask(this.p, this.cp5, this.name);
		clone.frequency = this.frequency;
		clone.amplitude = this.amplitude;
		clone.duration = this.duration;
		clone.wavetype = this.wavetype;
		return clone;
	}

	private void updateSoundEngine() {
		String freq_val = (evaluate_value(this.frequency)).toString();
		String amp_val = (evaluate_value(this.amplitude)).toString();
		String dur_val = (evaluate_value(this.duration)).toString();

		this.status = Status.RUNNING;

		System.out.println("executing OscillatorGentask");

		String[] par = new String[] { freq_val, amp_val, getWavetype() };

		Main.eng.updateGenerator(this.get_gui_id(), par);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (!should_run())
			return;

		updateSoundEngine();

		// this.status = Status.DONE;
	}
	
	public void closeTask() {
		Main.eng.removeGenerator(this.get_gui_id());
		super.closeTask();
	}

	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Oscillator Generator";

		Group g = super.load_gui_elements(s);
		// CallbackListener cb_enter = generate_callback_enter();
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (font_size * 18);
		g.setBackgroundHeight(backgroundheight);

		this.createGuiTextField("frequency", localx, localy + (1 * localoffset), width, g, callback("frequency")).setText(this.frequency.toString());
		this.createGuiTextField("amplitude", localx, localy + (2 * localoffset), width, g, callback("amplitude")).setText(this.amplitude.toString());
		this.createGuiTextField("duration", localx, localy + (3 * localoffset), width, g, callback("duration")).setText(this.duration.toString());

		this.createGuiToggle(localx, localy + (4 * localoffset), width, g, callback("repeat"));
		this.createScrollableList("wavetype", list, localx, localy + (0 * localoffset), width, g,  callback("wavetype"));

		return g;
	}

	public CallbackListener callback(String target) {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {

				// if this group is not open, returns...
				if (!((Group) cp5.get(get_gui_id())).isOpen())
					return;

				String content = theEvent.getController().getValueLabel().getText();
				
				if (content.trim().equals("")) 
					((Textfield) cp5.get(get_gui_id() + "/" + target)).setText(Task.userInputAsDefault);
				
				else {
					if (target.equals("frequency"))
						update_frequency(content);
					if (target.equals("amplitude"))
						update_amplitude(content);
					if (target.equals("duration"))
						update_duration(content);
					if (target.equals("repeat"))
						check_repeat_toggle(theEvent.getController().getName(), theEvent);
					if (target.equals("wavetype"))
						update_wavetype(content);
				}
			}
		};
	}

	public CallbackListener generate_callback_enter() {
		return null;
	}

	@Override
	public void reset_gui_fields() {
		String g_name = this.get_gui_id();
		String nv;
		// if this group is not open, returns...
		if (!((Group) cp5.get(get_gui_id())).isOpen())
			return;

		nv = ((Textfield) cp5.get(g_name + "/frequency")).getText();
		update_frequency(nv);
		nv = ((Textfield) cp5.get(g_name + "/amplitude")).getText();
		update_amplitude(nv);
		nv = ((Textfield) cp5.get(g_name + "/duration")).getText();
		update_amplitude(nv);
	}
}
