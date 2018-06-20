package frontend.tasks;

import java.util.Arrays;
import java.util.List;

import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableFloatTextfieldUIWithUserInput;
import frontend.ui.ScrollableListUI;
import processing.core.PApplet;

public class OscillatorGenTask extends Task {

	protected static final List<String> list = Arrays.asList("PHASOR", "QUATERPULSE", "SAW", "SINE", "SQUARE",
			"TRIANGLE");

	private ComputableFloatTextfieldUIWithUserInput frequency;
	private ComputableFloatTextfieldUIWithUserInput amplitude;
	private ComputableFloatTextfieldUIWithUserInput duration;
	private ScrollableListUI wavetype;
	
	public OscillatorGenTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		
		this.frequency = new ComputableFloatTextfieldUIWithUserInput();
		this.amplitude = new ComputableFloatTextfieldUIWithUserInput();
		this.duration = new ComputableFloatTextfieldUIWithUserInput();
		this.wavetype = new ScrollableListUI(list, 3);

		Main.eng.addGenerator(this.get_gui_id(), "OSCILLATOR", getDefaultParameters());
	}
	
	private String[] getDefaultParameters(){
		return new String[] { "-1", "-1", "SINE", "-1"};
	}
	
	private void processFrequencyChange() {
		if (frequency.update())
			Main.eng.updateGenerator(this.get_gui_id(), "frequency : " + frequency.getValue());
	}
	private void processAmplitudeChange() {
		if (amplitude.update())
			Main.eng.updateGenerator(this.get_gui_id(), "amplitude : " + amplitude.getValue());
	}
	private void processDurationChange() {
		if (duration.update())
			Main.eng.updateGenerator(this.get_gui_id(), "duration : " + duration.getValue());
	}
			
	private void processWavetypeChange() {
		if (wavetype.update())
			Main.eng.updateGenerator(this.get_gui_id(), "waveform : " + wavetype.getValue());
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

	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Oscillator Generator";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 4.5);
		g.setBackgroundHeight(backgroundheight);
		int miniOffsetDueToScrollList = 5;
		frequency.createUI(id, "frequency", localx, localy + miniOffsetDueToScrollList + (1 * localoffset), width, g);
		amplitude.createUI(id, "amplitude", localx, localy + miniOffsetDueToScrollList + (2 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + miniOffsetDueToScrollList + (3 * localoffset), width, g);
		wavetype.createUI(id, "wavetype", localx, localy + (0 * localoffset), width, g);
		
//		this.createGuiToggle(localx, localy + (4 * localoffset), width, g, callbackRepeatToggle());

		return g;
	}
	
	/////////////////////////////////////
	//methods to be carried to super or to be deleted	
	public void closeTask() {
		Main.eng.removeGenerator(this.get_gui_id());
		super.closeTask();
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

		frequency.updateValueExpression();
		amplitude.updateValueExpression();
		duration.updateValueExpression();
	}
}
