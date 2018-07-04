package frontend;
/************************************************
 ** Class representing a conection between two states in the HFSM
 ************************************************
 ** jeraman.info, Sep. 30 2016 ******************
 ************************************************
 ************************************************/

import java.io.Serializable;
import processing.core.PApplet;

import javax.script.*;
import controlP5.*;
import frontend.ui.ConnectionPriorityDropdownUi;
import frontend.ui.ConnectionTextfieldUi;

public class Connection implements Serializable {
	private static final long serialVersionUID = 1L;
	private State next_state;
	private State parent;

	private ConnectionTextfieldUi condition;
	private ConnectionPriorityDropdownUi priority;

	// GUI ELEMENTS
	private transient PApplet p;
	private transient ControlP5 cp5;

	// constructor for an empty transition
	public Connection(PApplet p, ControlP5 cp5, State par, State ns, int priority) {
		this(p, cp5, par, ns, new Expression("true"), priority);
	}

	// constructor for a more complex transition
	public Connection(PApplet p, ControlP5 cp5, State parent, State ns, Expression expression, int priority) {
		this.p = p;
		this.cp5 = cp5;
		this.next_state = ns;
		this.condition = new ConnectionTextfieldUi(expression.toString());
		this.priority = new ConnectionPriorityDropdownUi(priority, parent);
		
		this.parent = parent;
		//@TODO PROBLEM TAKING TIME TO LOAD IS IN THE FOLLOWING METHOD!!!
		init_gui_items();
	}

	void build(PApplet p, ControlP5 cp5) {
		this.p = p;
		this.cp5 = cp5;
		init_gui_items();
	}

	boolean is_condition_satisfied() {
		return condition.evaluateAsBoolean();
	}

	// updates the priority
	void update_priority(int p) {
		this.priority.setPriority(p);
	}

	// updates the parent
	void update_parent(String newid) {
		this.parent.set_id(newid);
	}

	// updates the next state
	void update_id_next_state(String newid) {
		this.next_state.set_id(newid);
	}

	// returns the next state
	State get_next_state() {
		return next_state;
	}

	// gets a condition
	Expression get_expression() {
		return this.condition.getExpression();
	}

	int get_priority() {
		return this.priority.getPriotity();
	}

	String get_name() {
		return parent.get_id() + "_TO_" + next_state.get_id();
	}

	/*******************************************
	 ** GUI FUNCTIONS ***************************
	 ********************************************/
	// @TODO PROBLEM TAKING TIME TO LOAD IS IN THE FOLLOWING METHOD!!!
	void init_gui_items() {

		// if this is a transition to self, do not add any gui items
		if (parent == next_state)
			return;

		String gui_name = get_name();

		this.priority.createUI(gui_name);
		this.condition.createUI(gui_name);
	}

	void remove_gui_items() {
		// if this is a transition to self, do not add any gui items
		if (parent == next_state)
			return;

		priority.remove();
		condition.remove();
	}

	void reload_gui_items() {
		remove_gui_items();
		init_gui_items();
	}

	void hide() {
		// if this is a transition to self, do not add any gui items
		if (parent == next_state)
			return;

		priority.hide();
		condition.hide();
	}

	void show() {
		// if this is a transition to self, do not add any gui items
		if (parent == next_state)
			return;
		
		priority.show();
		condition.show();
		
	}

	int get_label_width() {
		return this.condition.getLabelWidth();
	}

	void set_gui_position(int newx, int newy) {
		int x_offset = (int) get_label_width() / 2;
		newx = newx - x_offset;
		newy = newy - 30;
		priority.setPosition(newx, newy);
		condition.setPosition(newx + 5, newy);
	}

	boolean is_mouse_over() {
		return condition.isMouseOver() || priority.isMouseOver();
	}

	boolean should_be_removed() {
		if (Main.instance().user_pressed_minus() && is_mouse_over())
			return true;
		else
			return false;
	}
}
