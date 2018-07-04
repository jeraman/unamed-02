package frontend.tasks.generators;

import java.util.Arrays;
import java.util.List;

import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableFloatTextfieldUIWithUserInput;
import frontend.ui.ComputableIntegerTextfieldUIWithUserInput;
import frontend.ui.ScrollableListUI;
import processing.core.PApplet;

public class FMGenTask extends AbstractGenTask {
	
	private ComputableFloatTextfieldUIWithUserInput frequency;
	private ComputableFloatTextfieldUIWithUserInput amplitude;
	private ScrollableListUI carrierWavetype;
	private ComputableFloatTextfieldUI modFreq;
	private ComputableFloatTextfieldUI modAmp;
	private ScrollableListUI modWavetype;
	private ComputableIntegerTextfieldUIWithUserInput duration;
	
	public FMGenTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		this.frequency = new ComputableFloatTextfieldUIWithUserInput();
		this.amplitude = new ComputableFloatTextfieldUIWithUserInput();
		this.carrierWavetype = new ScrollableListUI(OscillatorGenTask.list, 3);
		this.modFreq = new ComputableFloatTextfieldUI(30f);
		this.modAmp = new ComputableFloatTextfieldUI(75f);
		this.modWavetype = new ScrollableListUI(OscillatorGenTask.list, 2);
		this.duration = new ComputableIntegerTextfieldUIWithUserInput();
		
//		this.addToEngine();
	}
	
	public void addToEngine() {
		Main.eng.addGenerator(this.get_gui_id(), "FM", getDefaultParameters());
	}
	
	protected String[] getDefaultParameters(){
		//return new String[] { "-1", "-1", "SINE", "30", "75.", "SAW", "-1"};
		return new String[] { this.frequency.getValue()+"", 
							 this.amplitude.getValue()+"", 
							 this.carrierWavetype.getValue()+"",
							 this.modFreq.getValue()+"",
							 this.modFreq.getValue()+"", 
							 this.modWavetype.getValue()+"",
							 this.duration.getValueAsInt()+""
							 };
	}
	
	private void processFrequencyChange() {
		if (frequency.update())
			Main.eng.updateGenerator(this.get_gui_id(), "carrierFreq : " + frequency.getValue());
	}
	private void processAmplitudeChange() {
		if (amplitude.update())
			Main.eng.updateGenerator(this.get_gui_id(), "carrierAmp : " + amplitude.getValue());
	}
			
	private void processWavetypeChange() {
		if (carrierWavetype.update())
			Main.eng.updateGenerator(this.get_gui_id(), "carrierWave : " + carrierWavetype.getValue());
	}
	
	private void processModFrequencyChange() {
		if (modFreq.update())
			Main.eng.updateGenerator(this.get_gui_id(), "modFreq : " + modFreq.getValue());
	}
	private void processModAmplitudeChange() {
		if (modAmp.update())
			Main.eng.updateGenerator(this.get_gui_id(), "modAmp : " + modAmp.getValue());
	}
			
	private void processModWavetypeChange() {
		if (modWavetype.update())
			Main.eng.updateGenerator(this.get_gui_id(), "modWave : " + modWavetype.getValue());
	}

	private void processDurationChange() {
		if (duration.update())
			Main.eng.updateGenerator(this.get_gui_id(), "duration : " + duration.getValue());
	}

	protected void processAllParameters() {
		this.processFrequencyChange();
		this.processAmplitudeChange();
		this.processWavetypeChange();
		this.processModFrequencyChange();
		this.processModAmplitudeChange();
		this.processModWavetypeChange();
		this.processDurationChange();
	}

	@Override
	public Task clone_it() {
		FMGenTask clone = new FMGenTask(this.p, this.cp5, this.name);
		clone.frequency = this.frequency;
		clone.amplitude = this.amplitude;
		clone.carrierWavetype = this.carrierWavetype;
		clone.modFreq = this.modFreq;
		clone.modAmp = this.modAmp;
		clone.modWavetype = this.modWavetype;
		clone.duration = this.duration;
		return clone;
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "FM Generator";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 7.5);
		g.setBackgroundHeight(backgroundheight);
		int miniOffsetDueToScrollList = 5;
		
		frequency.createUI(id, "frequency", localx, localy + miniOffsetDueToScrollList + (1 * localoffset), width, g);
		amplitude.createUI(id, "amplitude", localx, localy + miniOffsetDueToScrollList +(2 * localoffset), width, g);
		modFreq.createUI(id, "mod. frequency", localx, localy + miniOffsetDueToScrollList*2 +(4 * localoffset), width, g);
		modAmp.createUI(id, "mod. amplitude", localx, localy + miniOffsetDueToScrollList*2 +(5 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + miniOffsetDueToScrollList*2 + (6 * localoffset), width, g);

		carrierWavetype.createUI(id, "wavetype", localx, localy + (0 * localoffset), width, g);
		modWavetype.createUI(id, "mod. wavetype", localx, localy + miniOffsetDueToScrollList + (3 * localoffset), width, g);
		
		return g;
	}

	
//	@Override
//	public void reset_gui_fields() {
//		// TODO Auto-generated method stub
//		String g_name = this.get_gui_id();
//		String nv;
//		// if this group is not open, returns...
//		if (!((Group) cp5.get(get_gui_id())).isOpen())
//			return;
//
//		frequency.updateValueExpression();
//		amplitude.updateValueExpression();
//		this.modFreq.updateValueExpression();
//		this.modAmp.updateValueExpression();
//		duration.updateValueExpression();
//	}

}
