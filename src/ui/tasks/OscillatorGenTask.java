package ui.tasks;

import java.util.Arrays;
import java.util.List;

import javax.script.ScriptException;

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

	private String lastWavetype;
	private String lastFrequency;
	private String lastAmplitude;
	private String lastDuration;
	
	public OscillatorGenTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		
		this.frequency = new Expression(Task.userInputAsDefault);
		this.amplitude = new Expression(Task.userInputAsDefault);
		this.duration = new Expression(Task.userInputAsDefault);
		this.wavetype = "SINE";
		
		lastWavetype = "";
		lastFrequency = "";
		lastAmplitude = "";
		lastDuration = "";

		Main.eng.addGenerator(this.get_gui_id(), "OSCILLATOR", getDefaultParameters());
	}
	
	private String[] getDefaultParameters(){
		return new String[] { "-1", "-1", "SINE", "-1"};
	}

	private boolean frequencyShouldBeUpdated(String v) {
		return this.frequency == null || !this.frequency.toString().equals(v);
	}
	private boolean amplitudeShouldBeUpdated(String v) {
		return this.amplitude == null || !this.amplitude.toString().equals(v);
	}
	private boolean durationShouldBeUpdated(String v) {
		return this.duration == null || !this.duration.toString().equals(v);
	}
	
	void update_frequency(String v) {
		if (!frequencyShouldBeUpdated(v))
			return;
		System.out.println("updating frequency!" + v);
		this.frequency = new Expression(v);
		processFrequencyChange();
		
	}

	void update_amplitude(String v) {
		if (!amplitudeShouldBeUpdated(v))
			return;
		System.out.println("updating amplitude!" + v);
		this.amplitude = new Expression(v);
		processAmplitudeChange();

	}

	void update_duration(String v) {
		if (!durationShouldBeUpdated(v))
			return;
		System.out.println("updating duration!" + v);
		this.duration = new Expression(v);
		processDurationChange();
		
	}
	
	void update_wavetype(String wt) {
		System.out.println("updating wavetype!" + wt);
		this.wavetype = wt;
		processWavetypeChange();
	}
	
	private boolean willFrequencyChange(String newValue) {
		return !this.lastFrequency.trim().equalsIgnoreCase(newValue);
	}
	
	private boolean willAmplitudeChange(String newValue) {
		return !this.lastAmplitude.trim().equalsIgnoreCase(newValue);
	}

	private boolean willDurationChange(String newValue) {
		return !this.lastDuration.trim().equalsIgnoreCase(newValue);
	}
	
	private boolean willWavetypeChange(String newValue) {
		return !this.lastWavetype.trim().equalsIgnoreCase(newValue);
	}
	
	void processFrequencyChange() {
		String valueToUpdate = this.lastFrequency;
		
		if (this.frequency.toString().trim().equalsIgnoreCase(Task.userInputAsDefault))
			valueToUpdate = "-1";
		else
			try {
				valueToUpdate = evaluateAsFloat(this.frequency)+"";
				((Textfield) cp5.get(get_gui_id() + "/frequency")).setColorBackground(Task.defaultColor); //color of the task
			} catch (ScriptException | NumberFormatException e) {
				System.out.println("ScriptExpression-related error thrown, unhandled update.");
				((Textfield) cp5.get(get_gui_id() + "/frequency")).setColorBackground(p.color(255,0, 0, 100)); //color of the task
				valueToUpdate = "-1";
			}
						
		if (willFrequencyChange(valueToUpdate)) {
			Main.eng.updateGenerator(this.get_gui_id(), "frequency : " + valueToUpdate);
			this.lastFrequency = valueToUpdate;
		}
	}
	
	void processAmplitudeChange() {
		String valueToUpdate = "";

		if (this.amplitude.toString().trim().equalsIgnoreCase(Task.userInputAsDefault))
			valueToUpdate = "-1";
		else
			try {
				valueToUpdate = evaluateAsFloat(this.amplitude)+"";
				((Textfield) cp5.get(get_gui_id() + "/amplitude")).setColorBackground(Task.defaultColor); //color of the task
			} catch (ScriptException | NumberFormatException e) {
				System.out.println("ScriptExpression-related error thrown, unhandled update.");
				((Textfield) cp5.get(get_gui_id() + "/amplitude")).setColorBackground(p.color(255,0, 0, 100)); //color of the task
				valueToUpdate = "-1";
			}

		if (willAmplitudeChange(valueToUpdate)) {
			Main.eng.updateGenerator(this.get_gui_id(), "amplitude : " + valueToUpdate);
			this.lastAmplitude = valueToUpdate;
		}
	}
	
	void processDurationChange() {
		String valueToUpdate = "";
		if (this.duration.toString().trim().equalsIgnoreCase(Task.userInputAsDefault))
			valueToUpdate = "-1";
		else
			try {
				valueToUpdate = evaluateAsFloat(this.duration)+"";
				((Textfield) cp5.get(get_gui_id() + "/duration")).setColorBackground(Task.defaultColor); //color of the task
			} catch (ScriptException | NumberFormatException e) {
				System.out.println("ScriptExpression-related error thrown, unhandled update.");
				((Textfield) cp5.get(get_gui_id() + "/duration")).setColorBackground(p.color(255,0, 0, 100)); //color of the task
				valueToUpdate = "-1";
			}

		if (willDurationChange(valueToUpdate)) {
			Main.eng.updateGenerator(this.get_gui_id(), "duration : " + valueToUpdate);
			this.lastDuration = valueToUpdate;
		}
	}
	
	void processWavetypeChange() {
		if (willWavetypeChange(this.wavetype)) {
			Main.eng.updateGenerator(this.get_gui_id(), "waveform: " + this.wavetype);
			this.lastWavetype = this.wavetype;
		}
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

	private void processAllParameters() {
		this.processFrequencyChange();
		this.processAmplitudeChange();
		this.processDurationChange();
		this.processWavetypeChange();
	}

	@Override
	public void run() {
		if (!should_run())
			return;

		processAllParameters();

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
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (font_size * 15);
		g.setBackgroundHeight(backgroundheight);

		this.createGuiTextField("frequency", localx, localy + (1 * localoffset), width, g, callbackTextField("frequency")).setText(this.frequency.toString());
		this.createGuiTextField("amplitude", localx, localy + (2 * localoffset), width, g, callbackTextField("amplitude")).setText(this.amplitude.toString());
		this.createGuiTextField("duration", localx, localy + (3 * localoffset), width, g, callbackTextField("duration")).setText(this.duration.toString());
		//this.createGuiToggle(localx, localy + (4 * localoffset), width, g, callbackRepeatToggle());
		this.createScrollableList("wavetype", list, localx, localy + (0 * localoffset), width, g,  callbackScrollList());

		return g;
	}
	
	protected CallbackListener callbackScrollList() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				int index = (int)theEvent.getController().getValue();
				update_wavetype(list.get(index));
			}
		};
	}
	
	public CallbackListener callbackTextField(String target) {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {

				// if this group is not open, do nothing...
				if (!((Group) cp5.get(get_gui_id())).isOpen())
					return;

				String content = theEvent.getController().getValueLabel().getText();
				
				//if there parameter should be controlled via user input, do nothing
				if (content.trim().equals(Task.userInputAsDefault))
					return;
				
				//if user deleted the text, sets user input as default value
				if (content.trim().equals("")) {
					content = Task.userInputAsDefault;
					((Textfield) cp5.get(get_gui_id() + "/" + target)).setText(content);
				}
				
				//updates accordingly
				if (target.equals("frequency"))
					update_frequency(content);
				if (target.equals("amplitude"))
					update_amplitude(content);
				if (target.equals("duration"))
					update_duration(content);
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
