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

public class OscillatorBBTask extends AbstractBBTask {

	private ComputableFloatTextfieldUI frequency;
	private ComputableFloatTextfieldUI amplitude;

	public OscillatorBBTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		this.frequency = new ComputableFloatTextfieldUI(01);
		this.amplitude = new ComputableFloatTextfieldUI(1f);
		this.value = new TextfieldUi("0");
	}

	private void updateValue() {
		float freq_val = frequency.getValue();
		float amp_val = amplitude.getValue();
		this.value = new TextfieldUi(amp_val + "*math.sin(" + timer + "*2*math.PI*" + freq_val + ")");
	}
	
	protected boolean isFirstCycle() {
		float period = 1f/frequency.getValue();
		float remainder = timer % period;
		float threshold = (1f/(30f*period));
		boolean result =  ( remainder > threshold || timer <= threshold);
		System.out.println("period: " + period);
		System.out.println("timer: " + timer);
		System.out.println("threshold: " + threshold);
		System.out.println("remainder: " + remainder);
		System.out.println("done? " + result);
		return result || first_time;
	}	
	
	private void processFreqChange() {
		frequency.update();
		updateValue();
	}
	
	private void processAmpChange() {
		amplitude.update();
		updateValue();
	}
	
	@Override
	protected void processAllParameters() {
		super.processAllParameters();
		processFreqChange();
		processAmpChange();
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Oscillator Variable";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 4.5);
		g.setBackgroundHeight(backgroundheight);

		variableName.createUI(id, "name", localx, localy + (0 * localoffset), width, g);
		frequency.createUI(id, "frequency", localx, localy + (1 * localoffset), width, g);
		amplitude.createUI(id, "amplitude", localx, localy + (2 * localoffset), width, g);
		shouldRepeat.createUI(id, "once - repeat", localx, localy + (3 * localoffset), width, g);

		return g;
	}

	public OscillatorBBTask clone_it() {
		OscillatorBBTask clone = new OscillatorBBTask(this.p, this.cp5, this.name);

		clone.variableName = this.variableName;
		clone.frequency = this.frequency;
		clone.amplitude = this.amplitude;
		clone.shouldRepeat = this.shouldRepeat;
		clone.value = this.value;
		clone.timerMilestone = this.timerMilestone;
		clone.timer = this.timer;

		return clone;
	}
}

/*
 * public class OscillatorBBTask extends DefaultBBTask { private Object
 * frequency; private Object amplitude;
 * 
 * public OscillatorBBTask(PApplet p, ControlP5 cp5) { super(p, cp5, ("osc_" +
 * (int) p.random(0, 100)), new Expression("1"));
 * 
 * update_frequency("1"); update_amplitude("1"); // update_content(); }
 * 
 * // clone function public OscillatorBBTask clone_it() { OscillatorBBTask clone
 * = new OscillatorBBTask(this.p, this.cp5);
 * 
 * clone.variableName = this.variableName; clone.frequency = this.frequency;
 * clone.amplitude = this.amplitude; clone.value = this.value;
 * clone.timerMilestone = this.timerMilestone; clone.timer = this.timer;
 * clone.repeat = this.repeat;
 * 
 * return clone; }
 * 
 * void update_frequency(String v) { this.frequency = new Expression(v); //
 * update_content(); }
 * 
 * void update_amplitude(String v) { this.amplitude = new Expression(v); //
 * update_content(); }
 * 
 * public void run() {
 * 
 * if (!should_run()) return;
 * 
 * String freq_val = (old_evaluate_value(this.frequency)).toString(); String
 * amp_val = (old_evaluate_value(this.amplitude)).toString();
 * 
 * Expression ne = new Expression(amp_val + "*math.sin(" + timer + "*" +
 * freq_val + ")");
 * 
 * Blackboard board = Main.instance().board(); this.status = Status.RUNNING;
 * board.put(variableName, old_evaluate_value(ne)); this.status = Status.DONE; }
 * 
 * // UI config public Group load_gui_elements(State s) {
 * 
 * Group g = super.load_gui_elements(s); CallbackListener cb_enter =
 * generate_callback_enter(); String g_name = this.get_gui_id(); int w =
 * g.getWidth() - (localx * 2);
 * 
 * textlabel = "Oscillator variable"; backgroundheight = (int) (font_size *
 * 13.5);
 * 
 * g.setBackgroundHeight(backgroundheight); g.setLabel(textlabel);
 * 
 * cp5.addTextfield(g_name + "/name").setPosition(localx, localy).setSize(w,
 * (int) (font_size * 1.25)).setGroup(g)
 * .setAutoClear(false).setLabel("name").setText(this.variableName).onChange(
 * cb_enter)
 * .onReleaseOutside(cb_enter).getCaptionLabel().align(ControlP5.CENTER,
 * ControlP5.BOTTOM_OUTSIDE); ;
 * 
 * cp5.addTextfield(g_name + "/frequency").setPosition(localx, localy + (1 *
 * localoffset)) .setSize(w, (int) (font_size *
 * 1.25)).setGroup(g).setAutoClear(false).setLabel("frequency")
 * .setText(this.frequency.toString()) .align(ControlP5.CENTER,
 * ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER).onChange(cb_enter)
 * .onReleaseOutside(cb_enter).getCaptionLabel().align(ControlP5.CENTER,
 * ControlP5.BOTTOM_OUTSIDE);
 * 
 * cp5.addTextfield(g_name + "/amplitude").setPosition(localx, localy + (2 *
 * localoffset)) .setSize(w, (int) (font_size *
 * 1.25)).setGroup(g).setAutoClear(false).setLabel("amplitude")
 * .setText(this.amplitude.toString()) .align(ControlP5.CENTER,
 * ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER).onChange(cb_enter)
 * .onReleaseOutside(cb_enter).getCaptionLabel().align(ControlP5.CENTER,
 * ControlP5.BOTTOM_OUTSIDE);
 * 
 * createGuiToggle(localx, localy + (3 * localoffset), w, g,
 * callbackRepeatToggle());
 * 
 * return g; }
 * 
 * public CallbackListener generate_callback_enter() { return new
 * CallbackListener() { public void controlEvent(CallbackEvent theEvent) {
 * 
 * // if this group is not open, returns... if (!((Group)
 * cp5.get(get_gui_id())).isOpen()) return;
 * 
 * String s = theEvent.getController().getName();
 * 
 * if (s.equals(get_gui_id() + "/name")) { String text =
 * theEvent.getController().getValueLabel().getText(); // if the name is empty,
 * resets if (text.trim().equalsIgnoreCase("")) { ((Textfield)
 * cp5.get(get_gui_id() + "/name")).setText(name); return; }
 * update_variable_name(text); }
 * 
 * if (s.equals(get_gui_id() + "/frequency")) { String nv =
 * theEvent.getController().getValueLabel().getText(); if (nv.trim().equals(""))
 * { nv = "1"; ((Textfield) cp5.get(get_gui_id() + "/frequency")).setText(nv); }
 * update_frequency(nv); }
 * 
 * if (s.equals(get_gui_id() + "/amplitude")) { String nv =
 * theEvent.getController().getValueLabel().getText(); if (nv.trim().equals(""))
 * { nv = "1"; ((Textfield) cp5.get(get_gui_id() + "/amplitude")).setText(nv); }
 * update_amplitude(nv); }
 * 
 * } }; }
 * 
 * public void reset_gui_fields() { String g_name = this.get_gui_id(); String
 * nv;
 * 
 * // if this group is not open, returns... if (!((Group)
 * cp5.get(get_gui_id())).isOpen()) return;
 * 
 * nv = ((Textfield) cp5.get(g_name + "/name")).getText();
 * update_variable_name(nv); nv = ((Textfield) cp5.get(g_name +
 * "/frequency")).getText(); update_frequency(nv); nv = ((Textfield)
 * cp5.get(g_name + "/amplitude")).getText(); update_amplitude(nv); } }
 */