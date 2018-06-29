package frontend.tasks.blackboard;

import javax.script.ScriptException;

import controlP5.*;
import frontend.Blackboard;
import frontend.Expression;
import frontend.Main;
import frontend.State;
import frontend.Status;
import frontend.tasks.Task;
import frontend.tasks.generators.OscillatorGenTask;
import frontend.ui.TextfieldUi;
import frontend.ui.ToggleUi;
import processing.core.PApplet;


public class SetBBTask extends Task {

	private TextfieldUi variableName;
	private TextfieldUi value;
	private ToggleUi shouldRepeat;
	float timer;
	float timerMilestone;
	
	public SetBBTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);
		
		this.variableName = new TextfieldUi(taskname);
		this.value = new TextfieldUi("0");
		this.shouldRepeat = new ToggleUi();
		
		this.timerMilestone = 0;
		this.timer = 0;
	}
	
	private void processNameChange() {
		variableName.update();
		//if (variableName.update())
		//	updateVariableName();
	}
	
	private void processValueChange() {
		value.update();
		//if (value.update())
		//	updateValueName();
	}

	@Override
	protected void processAllParameters() {
		processNameChange();
		processValueChange();
	}
	
	public void run() {
		super.run();
		
		if (shouldRepeat.getValue())
			updateVariable();
	}
	
	public boolean should_run() {
		if (first_time)
			reset_timer();
		boolean should_run = super.should_run();
		update_timer();
		return should_run;
	}
	
	void update_timer() {
		this.timer = ((float) p.millis() / 1000f) - timerMilestone;
	}

	void reset_timer() {
		this.timerMilestone = (float) p.millis() / 1000f;
		this.timer = 0;
	}

	public void updateVariable() {
		try {
			System.out.println("updating variable: " + value.evaluate());
			Blackboard board = Main.instance().board();
			board.put(variableName.getValue(), value.evaluate());
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Task clone_it() {
		SetBBTask clone = new SetBBTask(this.p, this.cp5, this.name);
		clone.variableName = this.variableName;
		clone.value = this.value;
		clone.timer = this.timer;
		clone.timerMilestone = this.timerMilestone;
		return clone;
	}

	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Blackboard Variable";
		
		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);
		
		this.backgroundheight = (int) (localoffset * 3.5);
		g.setBackgroundHeight(backgroundheight);

		variableName.createUI(id, "frequency", localx, localy + (0 * localoffset), width, g);
		value.createUI(id, "amplitude", localx, localy + (1 * localoffset), width, g);
		shouldRepeat.createUI(id, "once - repeat", localx, localy + (2 * localoffset), width, g);
		
		return g;
	}
	
	
	@Override
	protected String[] getDefaultParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset_gui_fields() {
		// TODO Auto-generated method stub
		
	}
}

/*
public class SetBBTask extends Task {

	Object value;
	String variableName;
	float timer;
	float timerMilestone;

	public SetBBTask(PApplet p, ControlP5 cp5, String taskname, Object value) {
		super(p, cp5, taskname);
		this.variableName = taskname;
		this.value = value;
		this.timerMilestone = 0;
		this.timer = 0;
	}

	public SetBBTask(PApplet p, ControlP5 cp5, String taskname, Object value, boolean repeat) {
		this(p, cp5, taskname, value);
		this.repeat = repeat;
	}

	public void build(PApplet p, ControlP5 cp5) {
		this.p = p;
		this.cp5 = cp5;
	}
	
	public SetBBTask clone_it() {
		return new SetBBTask(this.p, this.cp5, this.name, this.value, this.repeat);
	}

	// updates the stateTimer variable related to this state machine
	void update_timer() {
		this.timer = ((float) p.millis() / 1000f) - timerMilestone;
	}

	// resets the timer variable related to this setBBTask
	void reset_timer() {
		this.timerMilestone = (float) p.millis() / 1000f;
		this.timer = 0;
	}

	// special "should run" modifed for blackboard variables involving timers
	public boolean should_run() {
		if (first_time)
			reset_timer();
		boolean should_run = super.should_run();
		update_timer();
		return should_run;
	}

	public void run() {
		if (!should_run())
			return;

		Blackboard board = Main.instance().board();
		this.status = Status.RUNNING;
		board.put(variableName, old_evaluate_value(value));
		this.status = Status.DONE;
	}

	public void stop() {
		super.stop();
		this.status = Status.INACTIVE;
	}

	void update_value(Object new_value) {
		value = new_value;
	}

	void update_variable_name(String newname) {
		this.variableName = newname;
	}
	
	@Override
	protected void processAllParameters() {
		// TODO Auto-generated method stub
		
	}

	public void update_status() {
	}

	public Group load_gui_elements(State s) {

		Group g = super.load_gui_elements(s);
		CallbackListener cb_enter = generate_callback_enter();
		String g_name = this.get_gui_id();
		int w = g.getWidth() - (localx * 2);

		textlabel = "Blackboard variable";
		backgroundheight = (int) (font_size * 10.5);

		g.setBackgroundHeight(backgroundheight);
		g.setLabel(textlabel);

		cp5.addTextfield(g_name + "/name").setPosition(localx, localy).setSize(w, (int) (font_size * 1.25)).setGroup(g)
				.setAutoClear(false).setLabel("name").setText(this.variableName).onChange(cb_enter)
				.onReleaseOutside(cb_enter).getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE);

		cp5.addTextfield(g_name + "/value").setPosition(localx, localy + localoffset)
				.setSize(w, (int) (font_size * 1.25)).setGroup(g).setAutoClear(false).setLabel("value")
				.setText(this.value.toString()).onChange(cb_enter).onReleaseOutside(cb_enter).getCaptionLabel()
				.align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE);

		createGuiToggle(localx, localy + (2 * localoffset), w, g, callbackRepeatToggle());

		return g;
	}

	public CallbackListener generate_callback_enter() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {

				// if this group is not open, returns...
				if (!((Group) cp5.get(get_gui_id())).isOpen())
					return;

				String s = theEvent.getController().getName();
				// println(s + " was entered");

				if (s.equals(get_gui_id() + "/name")) {
					String text = theEvent.getController().getValueLabel().getText();
					// if the name is empty, resets
					if (text.trim().equalsIgnoreCase("")) {
						((Textfield) cp5.get(get_gui_id() + "/name")).setText(name);
						return;
					}
					update_variable_name(text);
					System.out.println(s + " " + text);
				}
				if (s.equals(get_gui_id() + "/value")) {
					String newvalue = theEvent.getController().getValueLabel().getText();
					// if the name is empty, resets
					if (newvalue.trim().equalsIgnoreCase("")) {
						((Textfield) cp5.get(get_gui_id() + "/value")).setText(value.toString());
						return;
					}
					update_value(new Expression(newvalue));
					System.out.println(s + " " + newvalue);
				}

			}
		};
	}

	CallbackListener generate_callback_leave() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {

				String s = theEvent.getController().getName();

				String newtext = theEvent.getController().getValueLabel().getText();
				String oldtext = "";

				if (s.equals(get_gui_id() + "/name"))
					oldtext = variableName;
				else if (s.equals(get_gui_id() + "/value"))
					oldtext = value.toString();
				else
					return;

				// if the user tried to change but did not press enter
				if (!newtext.replace(" ", "").equals(oldtext)) {
					// resets the test for the original
					Textfield t = (Textfield) cp5.get(s);
					t.setText(oldtext);
				}
			}
		};
	}

	public void reset_gui_fields() {
		String g_name = this.get_gui_id();
		String nv;

		// if this group is not open, returns...
		if (!((Group) cp5.get(get_gui_id())).isOpen())
			return;

		nv = ((Textfield) cp5.get(g_name + "/name")).getText();
		update_variable_name(nv);
		nv = ((Textfield) cp5.get(g_name + "/value")).getText();
		update_value(nv);

	}
	
	protected String[] getDefaultParameters(){
		//TODO: abstract auto gen method
		return null;
	}

}
*/