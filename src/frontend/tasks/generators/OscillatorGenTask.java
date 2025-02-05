package frontend.tasks.generators;

import java.util.Arrays;
import java.util.List;

import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.ui.ComputableDurationTextfieldUIWithUserInput;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableFloatTextfieldUIWithUserInput;
import frontend.ui.ComputableIntegerTextfieldUIWithUserInput;
import frontend.ui.ScrollableListUI;
import processing.core.PApplet;
import soundengine.SoundEngine;

public class OscillatorGenTask extends AbstractGenTask {

	protected static final List<String> list = Arrays.asList("PHASOR", "QUATERPULSE", "SAW", "SINE", "SQUARE",
			"TRIANGLE");

	private ComputableFloatTextfieldUIWithUserInput frequency;
	private ComputableFloatTextfieldUIWithUserInput amplitude;
	private ComputableDurationTextfieldUIWithUserInput duration;
	private ScrollableListUI wavetype;
	
	public OscillatorGenTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
		
		this.frequency = new ComputableFloatTextfieldUIWithUserInput(50f, 10000f);
		this.amplitude = new ComputableFloatTextfieldUIWithUserInput(0f, 1f);
		this.duration = new ComputableDurationTextfieldUIWithUserInput();
		this.wavetype = new ScrollableListUI(list, 3);

		Main.log.countOscGenTask();
	}
	
	public void addToEngine() {
		this.eng.addGenerator(this.get_gui_id(), "OSCILLATOR", getDefaultParameters());
	}
	
	protected String[] getDefaultParameters(){
		//return new String[] { "-1", "-1", "SINE", "-1"};
		return new String[] { this.frequency.getValue()+"", 
				 this.amplitude.getValue()+"", 
				 this.wavetype.getValue()+"",
				 this.duration.getValueAsInt()+""
				 };
	}
	
	private void processFrequencyChange() {
		if (frequency.update())
			this.eng.updateGenerator(this.get_gui_id(), "frequency : " + frequency.getValue());
	}
	private void processAmplitudeChange() {
		if (amplitude.update())
			this.eng.updateGenerator(this.get_gui_id(), "amplitude : " + amplitude.getValue());
	}
	private void processDurationChange() {
		if (duration.update())
			this.eng.updateGenerator(this.get_gui_id(), "duration : " + duration.getValue());
	}
			
	private void processWavetypeChange() {
		if (wavetype.update())
			this.eng.updateGenerator(this.get_gui_id(), "waveform : " + wavetype.getValue());
	}

	protected void processAllParameters() {
		this.processFrequencyChange();
		this.processAmplitudeChange();
		this.processDurationChange();
		this.processWavetypeChange();
	}

	@Override
	public Task clone_it() {
		OscillatorGenTask clone = new OscillatorGenTask(this.p, this.cp5, this.name, this.eng);
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
		
		return g;
	}


//	@Override
//	public void reset_gui_fields() {
//		String g_name = this.get_gui_id();
//		String nv;
//		// if this group is not open, returns...
//		if (!((Group) cp5.get(get_gui_id())).isOpen())
//			return;
//
//		frequency.updateValueExpression();
//		amplitude.updateValueExpression();
//		duration.updateValueExpression();
//	}
}
