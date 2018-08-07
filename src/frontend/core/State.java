package frontend.core;

import java.util.*;
import java.io.Serializable;
import processing.core.PApplet;
import soundengine.SoundEngine;
import controlP5.*;
import frontend.Main;
import frontend.tasks.Task;
import frontend.tasks.augmenters.ChordAugTask;
import frontend.tasks.augmenters.IntervalAugTask;
import frontend.tasks.augmenters.NoteAugTask;
import frontend.tasks.blackboard.OscillatorBBTask;
import frontend.tasks.blackboard.RampBBTask;
import frontend.tasks.blackboard.RandomBBTask;
import frontend.tasks.blackboard.DefaultBBTask;
import frontend.tasks.effects.AdsrFxTask;
import frontend.tasks.effects.BitChrushFxTask;
import frontend.tasks.effects.DelayFxTask;
import frontend.tasks.effects.FilterFxTask;
import frontend.tasks.effects.FlangerFxTask;
import frontend.tasks.generators.FMGenTask;
import frontend.tasks.generators.OscillatorGenTask;
import frontend.tasks.generators.SampleGenTask;
import frontend.tasks.meta.DMXTask;
import frontend.tasks.meta.OSCTask;
import frontend.tasks.meta.ScriptingTask;
import frontend.ui.visuals.MultiLevelPieMenu;
import java.util.UUID;

/**
 * Class representing a state in the HFSM
 * 
 * @author jeronimo
 * @date Sep. 30 2016
 *
 */
public class State implements Serializable {

	private static final long serialVersionUID = 1L;
	private Vector<Connection> connections;
	private Vector<Task> tasks;
	private String name;
	private Status status;
	private MovementStatus movement_status;
	public boolean is_actual;
	public boolean debug;
	static boolean is_dragging_someone = false;

	// variables used for the gui
	public int x;
	public int y;
	private final int size;// = 50;
	private final float arrow_scale_offset = 1.25f;
	private String id;

	// gui elements
	transient private MultiLevelPieMenu pie;
	transient private Accordion accordion;
	transient private Textfield label;
	transient private PApplet p;
	transient private ControlP5 cp5;

	// sound
	transient private SoundEngine eng;

	private static final String defaultCondition = "true";

	// constructor
	public State(PApplet p, ControlP5 cp5, String name, SoundEngine eng) {
		this.p = p;
		this.cp5 = cp5;
		this.size = ((Main) p).get_state_circle_size();
		this.name = name.toUpperCase();
		this.status = Status.INACTIVE;
		this.tasks = new Vector<Task>();
		this.connections = new Vector<Connection>();
		this.x = (int) p.random(10, p.width - size);
		this.y = (int) p.random(10, p.height - size);
		this.movement_status = MovementStatus.FREE;
		this.debug = Main.instance().debug();
		this.id = UUID.randomUUID().toString();
		this.eng = eng;

		initStateGuiWithTasksAndConnections();
		hide_gui();

		this.connect_anything_else_to_self();

		this.is_actual = false;

	}

	// constructor
	public State(PApplet p, ControlP5 cp5, String name, SoundEngine eng, int x, int y) {
		this(p, cp5, name, eng);
		this.x = x;
		this.y = y;
		pie.set_position(x, y);
		// animation.set_position(x, y);
	}

	// @TODO IMMPLEMENT BUILD THAT LOADS THE UI ELEMENTS OF THE STATE
	void build(PApplet p, ControlP5 cp5, SoundEngine eng) {
		this.p = p;
		this.cp5 = cp5;
		this.eng = eng;

		// builds the tasks and add them to gui
		for (Connection c : connections)
			c.build(p, cp5);

		// builds the tasks and add them to gui
		for (Task t : tasks)
			t.build(p, cp5, eng);

		add_all_tasks_to_gui();

		// loads the gui
		initStateGuiWithoutTasksAndConnections();
		hide_gui();
	}

	String get_name() {
		return this.name;
	}

	String get_id() {
		return this.id;
	}

	void set_id(String newid) {
		this.id = id;
	}

	void reinit_id() {
		// updates the id
		this.id = UUID.randomUUID().toString();
		reset_group_id_gui_of_all_tasks();
	}

	State clone_it() {
		// cloning the simple attributes of this state
		State clone = new State(p, cp5, name, eng);

		// moving to the same position as this one
		clone.set_position_gui(this.x, this.y);

		// cloning all tasks
		for (Task t : tasks)
			clone.add_task(t.clone_it());

		// cloning all connections from this state
		for (Connection c : connections)
			// if it's not a transition to self
			if (c.get_next_state() != this)
				// clone it
				clone.connect(c.get_expression(), c.get_next_state());

		// cloning all connections to this state
		// @TODO

		return clone;
	}

	void check_if_any_substatemachine_needs_to_be_reloaded_from_file() {
		for (Task t : tasks)
			if (t instanceof StateMachine)
				((StateMachine) t).reload_from_file();
	}

	void start() {
		for (Task t : tasks)
			t.start();

		this.status = Status.RUNNING;

		if (debug)
			System.out.println("starting all the " + tasks.size() + " tasks from state " + this.name);
	}

	void run() {
		for (Task t : tasks)
			t.run();
		if (debug)
			System.out.println("running all the " + tasks.size() + " tasks from state " + this.name);
	}

	void stop() {
		for (Task t : tasks)
			t.stop();
		this.status = Status.INACTIVE;

		if (debug)
			System.out.println("stopping all tasks from state " + this.name);
	}

	void clear() {
		this.remove_all_tasks();
		this.remove_all_tasks_from_gui();
		this.remove_all_connections();
		cp5.remove(this.id + "/label");
		cp5.remove(this.id + "/acc");
	}

	// stops all tasks associated to this node
	void interrupt() {
		for (Task t : tasks)
			t.interrupt();

		this.status = Status.DONE;

		if (debug)
			System.out.println("interrupting all tasks from state " + this.name);
	}

	// in case there are statemachine inside this state, this machine should be
	// saved to file
	void save() {
		for (Task t : tasks)
			if (t instanceof StateMachine)
				((StateMachine) t).save();
	}

	// if it's entering a state, you need to refresh it
	void reset_first_time() {
		for (Task t : tasks)
			t.reset_first_time();
	}

	// gets the current status of this state
	Status get_status() {
		return this.status;
	}

	// function called everytime there is a new input
	State tick() {
		State my_return = this;

		// ticks all subState_Machine that are inside this state
		if (this.status == Status.RUNNING)
			for (Task t : tasks)
				if (t instanceof StateMachine)
					((StateMachine) t).tick();

		// if (this.status==Status.DONE)
		my_return = this.change_state();

		return my_return;
	}

	// tries to change the current state. returns the next state if it's time to
	// change
	State change_state() {

		// if done, looks for the next state
		State next_state = null;

		// iterates over array
		for (Connection c : connections) {
			// looks if c's condition corresponds to the current input. if so
			// changes the state
			if (c.is_condition_satisfied()) {
				next_state = c.get_next_state();

				// if it's going to another state
				if (next_state != this) {
					// interrupts current activities that are still going on
					this.interrupt();
					// refresh the next state
					// next_state.refresh();
					next_state.start();
					// reset_first_time
					next_state.reset_first_time();
					// runs the next state
					next_state.run();

					break;
				}

				// if this is a transition to this very same state
				else {
					if (debug)
						System.out.println("this is a transition to self!!! state: " + this.name);
					// refreshes currently stopped tasks
					this.refresh_and_run_completed_tasks();
					break;
				}
			}
		}

		if (next_state == null)
			if (debug)
				System.out.println(
						"State " + this.name + " doesn't have a connection for this input! this can be a bug!");

		return next_state;
	}

	// add a task t to this state
	void add_task(Task t) {
		tasks.addElement(t);
		if (debug)
			System.out.println("Task " + t.name + " added to state " + this.name);

		// updates the gui
		add_task_in_accordion_gui(t);
	}

	// remove a task t from this state
	void remove_task(Task t) {
		if (tasks.contains(t)) {
			// removes the physical task
			this.tasks.removeElement(t);
			// removes the task in gui
			this.remove_task_in_accordion_gui(t);
		} else if (debug)
			System.out.println("Unable to remove task " + t.name + " from state " + this.name);

		// updates the gui
		// remove_task_in_accordion_gui(t);
	}

	// removes all tasks associated to this state
	void remove_all_tasks() {
		// iterating over the array backwards
		for (int i = tasks.size() - 1; i >= 0; i--) {
			this.remove_task(tasks.get(i));
		}
	}

	void remove_all_connections_to_a_state(State dest) {
		// iterating over connections backwards
		for (int i = connections.size() - 1; i >= 0; i--) {
			Connection c = connections.get(i);
			if (c.get_next_state() == dest)
				this.disconnect(c);
		}

		this.reload_connections_gui();
	}

	void update_all_connections_to_a_state(State dest, String newid) {
		// iterating over connections backwards
		for (int i = connections.size() - 1; i >= 0; i--) {
			Connection c = connections.get(i);
			if (c.get_next_state() == dest)
				c.update_id_next_state(newid);
			// all connections are automatically realoaded inside the last
			// method. no need to do it again.
		}
	}

	void update_all_connections_from_this_state() {
		// iterating over connections backwards
		for (int i = connections.size() - 1; i >= 0; i--) {
			Connection c = connections.get(i);
			c.update_parent(this.get_id());
			// all connections are automatically realoaded inside the last
			// method. no need to do it again.
		}
	}

	void reload_connections_gui() {
		for (Connection c : connections)
			c.reload_gui_items();
	}

	void connectWithDefaultCondition(State next_state) {
		this.connect(new Expression(defaultCondition), next_state);
	}

	void connect(Expression expression, State next_state) {
		// if there is already a connection next_state, do nothing
		if (there_is_already_a_connection_to_state(next_state))
			return;

		int p = connections.size() + 1;

		// in case the condition hasnt been used, create a new connection
		Connection c = new Connection(this.p, this.cp5, this, next_state, expression, p);

		// @TODO add item from all dropdown lists
		connections.addElement(c);
		// reload_connections_gui();

		// if there is already a transition to self, and it's not the one we
		// just created
		if (there_is_already_a_connection_to_state(this) && next_state != this)
			// update its priority to the last
			update_priority(p - 1, p);

		if (debug)
			System.out.println("Connection created. If " + this.name + " receives " + expression.toString()
					+ ", it goes to state " + next_state.name);
	}

	void update_all_priorities() {
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			c.update_priority(i + 1);
		}
	}

	public void update_priority(int oldPriority, int newPriority) {
		// change the position of the connection in the Vector
		// udpate all connections
		// reload gui elements

		Connection item = connections.get(oldPriority - 1);
		connections.remove(oldPriority - 1);
		connections.add(newPriority - 1, item);

		if (debug)
			System.out.println("updating priority. was" + oldPriority + " now is " + newPriority);

		update_all_priorities();
		reload_connections_gui();
	}

	// creates a "anything else" connection to next_state
	void connect(State next_state) {
		this.connect(new Expression("true"), next_state);
	}

	// creates a "anything else" connection to self
	void connect_anything_else_to_self() {
		this.connect(this);
	}

	boolean there_is_already_a_connection_to_state(State next) {
		boolean result = false;

		for (Connection c : connections)
			if (next == c.get_next_state())
				result = true;

		return result;
	}

	// remove a connection from this state
	void disconnect(Connection c) {
		if (connections.contains(c)) {
			// all connection gui items
			c.remove_gui_items();
			// remove the connection
			this.connections.removeElement(c);

			// if there is only one connection left and it is a trnsition to
			// self
			// if (this.connections.size()==1 &&
			// this.connections.get(0).get_next_state()==this)
			// removes everything
			// this.disconnect(this.connections.get(0));

			// updates priorities of connections
			this.update_all_priorities();
		} else if (debug)
			System.out.println("Unable to remove connection " + c.toString() + " from state " + this.name);
	}

	int get_number_of_tasks() {
		return this.tasks.size();
	}

	// method that generates a random name for the demo task
	String generate_random_name() {
		return ("example" + ((int) p.random(0, 100)));
	}

	/////////////////////////////
	// tasks

	private void init_sample_task() {
		if (debug)
			System.out.println("create sample task!");
		String taskname = generate_random_name();
		SampleGenTask t = new SampleGenTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	private void init_live_input_task() {
		// TODO Auto-generated method stub
		System.out.println("create live input task!");

	}

	private void init_fm_synth_task() {
		if (debug)
			System.out.println("create fm synth task!");
		String taskname = generate_random_name();
		FMGenTask t = new FMGenTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	private void init_oscillator_task() {
		if (debug)
			System.out.println("create oscillator task!");
		String taskname = generate_random_name();
		OscillatorGenTask t = new OscillatorGenTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	private void init_filter_task() {
		if (debug)
			System.out.println("create filter task!");
		String taskname = generate_random_name();
		FilterFxTask t = new FilterFxTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	private void init_bitchrush_task() {
		if (debug)
			System.out.println("create bitchrush task!");
		String taskname = generate_random_name();
		BitChrushFxTask t = new BitChrushFxTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);

	}

	private void init_adsr_task() {
		if (debug)
			System.out.println("create adsr task!");
		String taskname = generate_random_name();
		AdsrFxTask t = new AdsrFxTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	private void init_flanger_task() {
		if (debug)
			System.out.println("create flanger task!");
		String taskname = generate_random_name();
		FlangerFxTask t = new FlangerFxTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	private void init_delay_task() {
		if (debug)
			System.out.println("create delay task!");
		String taskname = generate_random_name();
		DelayFxTask t = new DelayFxTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	private void init_chord_task() {
		if (debug)
			System.out.println("create chord task!");
		String taskname = generate_random_name();
		ChordAugTask t = new ChordAugTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	private void init_interval_task() {
		if (debug)
			System.out.println("create interval task!");
		String taskname = generate_random_name();
		IntervalAugTask t = new IntervalAugTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	private void init_note_task() {
		if (debug)
			System.out.println("create note task!");
		String taskname = generate_random_name();
		NoteAugTask t = new NoteAugTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);

	}

	void init_control_dmx_task() {
		if (debug)
			System.out.println("create dmx task!");
		String taskname = generate_random_name();
		DMXTask t = new DMXTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	void init_osc_task() {
		if (debug)
			System.out.println("create osc task!");
		String taskname = generate_random_name();
		OSCTask t = new OSCTask(p, cp5, taskname, this.eng);
		this.add_task(t);
	}

	void init_state_machine_task() {
		if (debug)
			System.out.println("create sub sm task!");
		String taskname = generate_random_name();
		StateMachine t = new StateMachine(p, cp5, taskname);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	void init_scripting_task() {
		if (debug)
			System.out.println("create js script task!");
		String taskname = generate_random_name();
		ScriptingTask t = new ScriptingTask(p, cp5, "example.js", this.eng);
		this.add_task(t);
	}

	void init_set_blackboard_task() {
		if (debug)
			System.out.println("create bb task!");
		String taskname = generate_random_name();
		DefaultBBTask t = new DefaultBBTask(p, cp5, taskname, this.eng);
		this.add_task(t);
	}

	void init_bb_rand_task() {
		if (debug)
			System.out.println("create random bb task!");
		String taskname = generate_random_name();
		RandomBBTask t = new RandomBBTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	// method that initializes a random osc balckboard var
	void init_bb_osc_task() {
		if (debug)
			System.out.println("create oscilator bb task!");
		String taskname = generate_random_name();
		OscillatorBBTask t = new OscillatorBBTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
		// println(selected + " " + pie.options[selected]);
	}

	// method that initializes a ramp balckboard var
	void init_bb_ramp_task() {
		if (debug)
			System.out.println("create ramping bb task!");
		String taskname = generate_random_name();
		RampBBTask t = new RampBBTask(p, cp5, taskname, this.eng);
		if (this.status == Status.RUNNING)
			t.start();
		this.add_task(t);
	}

	public int get_number_of_connections() {
		return connections.size();
	}

	@Deprecated
	// if it's entering a state, you need to refresh it
	void refresh() {
		for (Task t : tasks)
			t.refresh();
	}

	// updates the status of this state
	@Deprecated
	void update_status() {

		// if there are no tasks, the state is done
		// if (tasks.size() == 0)
		// this.status = Status.DONE;

		// gets the status of the tasks associated to this state and updates
		// accordingly
		// for (Task t : tasks) {
		// Status temporary_status = t.get_status();
		// // updates accordingly
		// if (temporary_status == Status.INACTIVE) {
		// this.status = Status.INACTIVE;
		// break;
		// }
		//
		// if (temporary_status == Status.RUNNING) {
		// this.status = Status.RUNNING;
		// // if this is a State_Machine
		// // if (t instanceof State_Machine)
		//
		// break;
		// }
		//
		// if (temporary_status == Status.DONE)
		// this.status = Status.DONE;
		// }
	}

	@Deprecated
	// only refreshes and reruns completed tasks
	void refresh_and_run_completed_tasks() {
		for (Task t : tasks)
			if (t.get_status() == Status.DONE || t.get_status() == Status.INACTIVE) {
				t.refresh();
				t.run();
			}
	}

	public void forwardNoteOnToSubStateMachines(int channel, int pitch, int velocity) {
		for (Task t : tasks)
			if (t instanceof StateMachine)
				((StateMachine) t).noteOn(channel, pitch, velocity);
	}

	public void forwardNoteOffToSubStateMachines(int channel, int pitch, int velocity) {
		for (Task t : tasks)
			if (t instanceof StateMachine)
				((StateMachine) t).noteOff(channel, pitch, velocity);
	}

	/*******************************************
	 ** GUI FUNCTIONS ***************************
	 ********************************************/
	void draw() {
		draw_temp_connection();
		update_gui();
		draw_connections();
		draw_pie();
		draw_state();
		// animation.draw();
		// draw_gui();
	}

	/*
	 * void draw_gui() { //draw this cp5 elements label.draw(p.g);
	 * 
	 * 
	 * accordion.draw(p.g); //draw cp5 elements of the tasks for (Task t :
	 * tasks) t.draw_gui(); for (Connection c : connections) c.draw_gui();
	 * 
	 * }
	 */

	// updates the current position of this state in screen
	void set_position_gui(int newx, int newy) {
		this.x = newx;
		this.y = newy;
		this.pie.set_position(x, y);
		// this.animation.set_position(x, y);
		// disables the focus
		// label.setFocus(false);
	}

	// checks if a certain position (often the mouse) intersects this state in
	// the screen
	boolean intersects_gui(int test_x, int test_y) {
		int dx = p.abs(test_x - x);
		int dy = p.abs(test_y - y);
		int R = size - 25;

		return (dx * dx) + (dy * dy) <= R * R;
	}

	// functions that updates the gui
	void update_gui() {
		// verifies if the user picked an option in the pie menu
		verify_if_user_picked_a_pie_option();
		// removes the task, if necessary
		remove_task_in_gui_if_necessary();
		// updates the gui cordinates, if necessary
		update_cordinates_gui();
		// update connecitons gui
		// update_connections_gui();
		remove_connection_if_necessary();
	}

	// aux variable to handle the state moving on the screen
	// boolean moving = false;

	boolean should_start_dragging() {
		return this.intersects_gui(p.mouseX, p.mouseY) && !is_dragging_someone
				&& this.movement_status == MovementStatus.FREE;
	}

	// updates the coords of the state in the screen in case mouse drags it
	void update_cordinates_gui() {
		// if mouse if moving
		if (p.mousePressed && p.mouseButton == p.LEFT) {
			// if intersects for the first time
			if (should_start_dragging()) {
				// set move equals true
				// moving= true;
				// movement_status = MovementStatus.MOVING;
				// is_dragging_someone = true;
				this.start_gui_dragging();
			}

			// if is moving, updates the value
			if (movement_status == MovementStatus.MOVING)
				set_position_gui(p.mouseX, p.mouseY);
			// if mouse is released
		} else {
			// if this state was moving before
			if (movement_status == MovementStatus.MOVING) {
				// stops moving
				// movement_status = MovementStatus.FREE;
				// is_dragging_someone = false;
				this.stop_gui_dragging();
			}
		}
	}

	void start_gui_dragging() {
		movement_status = MovementStatus.MOVING;
		is_dragging_someone = true;
	}

	void stop_gui_dragging() {
		movement_status = MovementStatus.FREE;
		is_dragging_someone = false;
	}

	void remove_all_connections() {
		for (int i = connections.size() - 1; i >= 0; i--) {
			Connection c = connections.get(i);
			this.disconnect(c);
			this.reload_connections_gui();
		}
	}

	void remove_connection_if_necessary() {
		for (int i = connections.size() - 1; i >= 0; i--) {
			Connection c = connections.get(i);
			if (c.should_be_removed()) {
				if (debug)
					System.out.println("remove " + c.toString());
				this.disconnect(c);
				this.reload_connections_gui();
			}
		}

	}

	void update_name(String newName) {
		// cp5.remove(this.name);
		// this.remove_all_tasks_from_gui();
		// this.remove_gui_connections_involving_this_state();
		this.name = newName.toUpperCase();
		label.setText(name);
		// this.init_state_name_gui();
		// this.add_all_tasks_to_gui();
		// this.init_gui_connections_involving_this_state();
		updateLabelWidth();
	}

	private void updateLabelWidth() {
		int textwidth = (int) (p.textWidth(this.name)*1.05);
		label.setWidth(textwidth);
		//label.setWidth((int)(p.textWidth(this.name)*1.5));
	}

	// resets the name of this state
	void reset_name() {
		// for (Task t : tasks)
		// t.reset_gui_fields();
		this.update_name(this.label.getText());
	}

	// inits gui elements related to controlP5
	void initStateGuiWithTasksAndConnections() {
		initStateGuiWithoutTasksAndConnections();

		add_all_tasks_to_gui();
		for (Connection c : connections)
			c.init_gui_items();
	}

	void initStateGuiWithoutTasksAndConnections() {
		this.pie = new MultiLevelPieMenu(p);
		this.pie.set_position(x, y);
		this.pie.set_inner_circle_diam((float) size);

		p.textFont(Main.instance().get_font());
		p.textSize(Main.instance().get_font_size());
		init_state_name_gui();
	}

	void hide_gui() {
		// if the PApplet wasn't loaded yet
		if (label == null || accordion == null)
			return;

		label.hide();
		accordion.hide();

		for (Connection c : connections)
			c.hide();
	}

	void show_gui() {
		// if the PApplet wasn't loaded yet
		if (label == null || accordion == null)
			return;
		label.show();
		accordion.show();

		for (Connection c : connections)
			c.show();
	}

	boolean is_textfield_selected = false;

	CallbackListener generate_callback_enter() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				// if this textfield is not selected, returns...
				// if (label.getText().equalsIgnoreCase(newName))
				// if (!label.isFocus()) return;

				// if the name is empty, resets
				if (label.getText().trim().equalsIgnoreCase(""))
					label.setText(name);
				// if the name didn't change, no need to continue
				if (label.getText().equalsIgnoreCase(name))
					return;
			
				String newName = theEvent.getController().getValueLabel().getText();
				String oldName = name;
				
				update_name(newName);
				
				/*
				 * MainCanvas canvas = HFSMPrototype.instance().canvas;
				 * 
				 * //checks if there is already a state with the very same
				 * future name [BAD CODE!] State
				 * is_there_a_state_with_the_new_id =
				 * canvas.root.get_state_by_name(newName); State result =
				 * canvas.root.get_state_by_name(oldName);
				 * 
				 * //if there is, prints an error and change does not occur! if
				 * (is_there_a_state_with_the_new_name != null) { System.out.
				 * println("There is alrealdy a state with this same name. Please, pick another name!"
				 * ); //if the names are different, reset if
				 * (!oldName.equals(newName)) result.update_name(oldName);
				 * return; }
				 * 
				 * if (result != null)
				 * 
				 * result.update_name(newName); else
				 * System.out.println("a state with name " + oldName +
				 * " could not be found! ");
				 */
			}
		};
	}

	CallbackListener generate_callback_leave() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				// if the user leaves the textfield without pressing enter
				if (!label.getText().equalsIgnoreCase(name))
					// resets the label
					// init_state_name_gui();
					reset_name();
			}
		};
	}

	/*
	 * CallbackListener generate_callback_double_press() { return new
	 * CallbackListener() { public void controlEvent(CallbackEvent theEvent) {
	 * 
	 * System.out.println("double clicked on " + name); } }; }
	 */

	// inits the label with the name of the state
	void init_state_name_gui() {

		// ControlP5 cp5 = HFSMPrototype.instance().cp5();

		CallbackListener cb_enter = generate_callback_enter();
		// CallbackListener cb_leave = generate_callback_leave();

		int c1 = p.color(255, 255, 255, 255);
		int c2 = p.color(0, 0, 0, 1);

		// p.textAlign(p.CENTER, p.CENTER);

		label = cp5.addTextfield(this.id + "/label").setText(this.name).setVisible(true).setLabelVisible(true)
				.setColorValue(c1).setColorBackground(c2).setColorForeground(c2)
				.align(ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER)
				// .setWidth((int)(p.textWidth(this.name)*1.25))
				.setWidth((int) (p.textWidth(this.name)))
				.setFocus(false).setAutoClear(false).setLabel("").onChange(cb_enter).onReleaseOutside(cb_enter)
		// .onDoublePress(generate_callback_double_press())
		// .onDrag(cb)
		;
	}

	// init the accordion that will store the tasks
	void init_accordion_gui() {
		// ControlP5 cp5 = HFSMPrototype.instance().cp5();

		// accordion = cp5.addAccordion("acc_"+this.name)
		accordion = cp5.addAccordion(this.id + "/acc")
				// .setWidth(150)
				.setWidth(10 * ((Main) p).FONT_SIZE).setVisible(true)
		// .hide()
		;
	}

	// adds a task from the accordion
	void add_task_in_accordion_gui(Task t) {

		// creates a new group
		Group g = t.load_gui_elements(this);

		// sets it visible
		g.setVisible(true);

		// adds this group to the accordion
		accordion.addItem(g);
	}

	// removes a task from the accordion
	void remove_task_in_accordion_gui(Task t) {
		// ControlP5 cp5 = HFSMPrototype.instance().cp5();
		// looks for the group
		// Group g = cp5.get(Group.class, this.name + " " + t.get_name());
		// removes this task from the accordion
		if (t == null || cp5 == null)
			return;

		t.closeTask();
		// t.removeElementUi();
		// cp5.getGroup(t.get_gui_id()).remove();
		// cp5.getGroup(this.name + " " + t.get_name()).remove();
	}

	// draws the status of this state
	void draw_status() {
		p.noStroke();

		if (is_actual)
			// if (status==Status.RUNNING)
			// if (status==Status.RUNNING | (status==Status.DONE & is_actual))
			p.fill(0, green + 75, 0);
		else if (status == Status.DONE)
			p.fill(100, 0, 0);
		else if (status == Status.INACTIVE)
			p.fill(100);

		p.ellipse(x, y, size + 25, size + 25);

		// increments the status
		increment_status();
	}

	// aux variables for the gui
	float counter = 0, green = 0;

	void increment_status() {
		// incrementing the counter
		int limit = 32;
		if (counter < limit / 2)
			green = green + limit / 16;
		else
			green = green - limit / 16;

		counter = (counter + 1) % limit;
	}

	void draw_state() {
		// if keypressed, draws a connection
		// if (keyPressed)
		// draw_connections(mouseX, mouseY);

		// draws the status circle
		draw_status();

		// draws the main central ellipse
		p.noStroke();
		p.fill(0);
		p.ellipse(x, y, size, size);

		// prints info such as tasks and name
		move_gui();
	}

	void move_gui() {
		// moving the label
		label.align(ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER);
		float textwidth = p.textWidth(name);
		textwidth = textwidth / 2;
		label.setPosition(x-textwidth+(textwidth/10), y-7);
		//label.setPosition(x - textwidth, y - 7);

		// moving the tasks
		// accordion.setPosition(x-(accordion.getWidth()/2),
		// y+(size/2)+(size/4));
		accordion.setPosition(x - (accordion.getWidth() / 2), y + (size / 2) + (size / 8));
	}

	// draws additional info if this is a begin
	void draw_begin() {

		p.noFill();
		// stroke(green+25);
		p.stroke(50);
		// the wieght of the line
		p.strokeWeight(5);
		p.ellipse(x, y, size * 2, size * 2);
		// fill(green+25);
		p.fill(50);
		p.noStroke();
		p.textAlign(p.CENTER, p.CENTER);
		p.text("BEGIN", x, y - (size * 1.2f));
	}

	// draws additional info if this is an end
	void draw_end() {
		// line color
		p.noFill();
		// stroke(green+25);
		p.stroke(50);
		// the wieght of the line
		p.strokeWeight(5);
		p.ellipse(x, y, size * 2, size * 2);
		// fill(green+25);
		p.fill(50);
		p.noStroke();
		p.textAlign(p.CENTER, p.CENTER);
		p.text("END", x, y - (size * 1.2f));
	}

	// draws additional info if this is an end
	void draw_actual() {
		// line color
		p.noFill();
		p.stroke(green + 25);
		// stroke(50);
		// the wieght of the line
		p.strokeWeight(5);
		p.ellipse(x, y, size * 2.5f, size * 2.5f);
		p.fill(green + 25);
		// fill(50);
		p.noStroke();
		p.textAlign(p.CENTER, p.CENTER);
		p.text("ACTUAL", x, y - (size * 1.5f));
	}

	void draw_connections() {
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			c.update_priority(i + 1);
			// if (c.get_next_state().get_name() == this.get_name())
			// draw_connection_to_self(c);
			// else
			if (c.get_next_state().get_name() != this.get_name())
				draw_connection(c);
		}
	}

	public boolean hasTemporaryConnectionOnGui() {
		return (this.movement_status == MovementStatus.FREEZED);
	}

	boolean verify_if_user_released_mouse_while_temporary_connecting() {
		return (hasTemporaryConnectionOnGui() && Main.instance().mouseRightButtonReleased);
	}

	void draw_temp_connection() {
		if (hasTemporaryConnectionOnGui()) {
			draw_generic_connection(p.mouseX, p.mouseY, connections.size() + 1, "true");
		}
	}

	void draw_generic_connection(int destx, int desty, int priority, String label) {
		draw_generic_connection(destx, desty, priority, label, true);
	}

	void draw_generic_connection(int destx, int desty, int priority, String label, boolean print_label) {
		// line color
		p.stroke(50);
		// the wieght of the line
		p.strokeWeight(size / 10);
		// draws the line
		p.line(x, y, destx, desty);
		// saves the current matrix
		p.pushMatrix();
		// moving to where the arrow is going
		p.translate(x, y);
		// saves the current matrix
		p.pushMatrix();
		// computes the midpoint where the arrow is going to be
		float newx = ((destx - x) / 2) * arrow_scale_offset;
		float newy = ((desty - y) / 2) * arrow_scale_offset;
		// translate to the final position of the arrow
		p.translate(newx, newy);

		// saves the current matrix
		p.pushMatrix();

		// computes the angle to rotate the arrow
		float a = p.atan2(x - destx, desty - y);
		// rotates
		p.rotate(a);
		// draws the arr0w
		// p.line(0, 0, -10, -10);
		// p.line(0, 0, 10, -10);
		p.line(0, 0, -10, -10);
		p.line(0, 0, 10, -10);
		// returns the matris to the regular position
		p.popMatrix();
		// sets text color
		p.fill(180);
		p.textAlign(p.CENTER, p.CENTER);
		if (print_label)
			p.text("[ " + priority + " ] : " + label, 0, -30);
		// c.set_gui_position(0, -30);
		// returns the matris to the regular position
		p.popMatrix();
		p.popMatrix();
	}

	void draw_connection(Connection c) {
		State ns = c.get_next_state();
		float destx = ns.x;
		float desty = ns.y;
		draw_generic_connection(ns.x, ns.y, c.get_priority(), c.get_expression().toString(), false);
		float newx = ((destx - x) / 2) * arrow_scale_offset;
		float newy = ((desty - y) / 2) * arrow_scale_offset;
		newx = (newx + x);
		newy = (newy + y);
		c.set_gui_position((int) newx, (int) newy);
	}

	void draw_pie() {
		pie.draw();
	}

	// show the attached pie
	void show_pie() {
		pie.show();
	}

	// close the attached pie
	void hide_pie() {
		pie.hide();
	}

	void show_or_hide_pie() {
		if (pie.is_showing())
			pie.hide();
		else
			pie.show();
	}

	// gets what option of the pie has been selected. returns -1 if none is
	// selected
	int get_pie_option() {
		return pie.get_selection();
	}

	// returns if the pie menu is currently open or not
	boolean is_pie_menu_open() {
		return (pie.is_showing() && (!pie.is_fading_away()));
	}

	// verifies if the user selected a option inside the pie menu
	void verify_if_user_picked_a_pie_option() {
		// if the menu is not open or the mouse is not clicked, returns
		if (!is_pie_menu_open() || !p.mousePressed)
			return;

		int selected = get_pie_option();

		// if the mouse is pressed & the button is over a option & is not
		// dragging
		if (p.mousePressed && selected > -1 && !is_dragging_someone) {
			if (debug)
				System.out.println("state receive " + selected + " as a result");

			switch (selected) {

			case 13: // gen > oscillator
				init_oscillator_task();
				hide_pie();
				break;
			case 14: // gen > fm synth
				init_fm_synth_task();
				hide_pie();
				break;
			case 15: // gen > live input
				init_live_input_task();
				hide_pie();
				break;
			case 10: // gen > –-sample
				init_sample_task();
				hide_pie();
				break;

			case 22: // aug > note
				init_note_task();
				hide_pie();
				break;
			case 23: // aug > interval
				init_interval_task();
				hide_pie();
				break;
			case 20: // aug > chord
				init_chord_task();
				hide_pie();
				break;

			case 34: // fx > delay
				init_delay_task();
				hide_pie();
				break;
			case 35: // fx > flanger
				init_flanger_task();
				hide_pie();
				break;
			case 36: // fx > adsr
				init_adsr_task();
				hide_pie();
				break;
			case 37: // fx > bitchrush
				init_bitchrush_task();
				hide_pie();
				break;
			case 30: // fx > filter
				init_filter_task();
				hide_pie();
				break;

			case 43:
				init_bb_rand_task();
				hide_pie();
				break;
			case 44:
				init_bb_osc_task();
				hide_pie();
				break;
			case 45:
				init_bb_ramp_task();
				hide_pie();
				break;
			case 40:
				init_set_blackboard_task();
				hide_pie();
				break;

			case 53:
				init_state_machine_task();
				hide_pie();
				break;
			case 54:
				init_scripting_task();
				hide_pie();
				break;
			case 55:
				init_control_dmx_task();
				hide_pie();
				break;
			case 50:
				init_osc_task();
				hide_pie();
				break;
			}
		}
	}

	// verifies if the mouse is over a certain task, returning this task
	Task verifies_if_mouse_is_over_a_task() {
		Task to_be_removed = null;
		// ControlP5 cp5 = HFSMPrototype.instance().cp5();

		// iterates of all tasks related to this state
		for (Task t : tasks) {
			// gets the group related to this task
			Group g = cp5.get(Group.class, t.get_gui_id());

			if (g == null) {
				if (debug)
					System.out.println("g==null");
				return null;
			}

			// verifies if the menu item is selected and the user pressed '-'
			if (g.isMouseOver() && Main.instance().user_pressed_minus()) {
				// stores the item to be removed
				to_be_removed = t;
				break;
			}
		}

		return to_be_removed;
	}

	void remove_task_in_gui_if_necessary() {
		Task to_be_removed = verifies_if_mouse_is_over_a_task();
		// if there is someone to be removed
		if (to_be_removed != null)
			// removes this item
			remove_task(to_be_removed);
	}

	// removes all tasks from the gui (used whenever the state name needs to
	// change)
	void remove_all_tasks_from_gui() {
		// iterates of all tasks related to this state
		for (Task t : tasks)
			remove_task_in_accordion_gui(t);
	}

	// resets the group i all tasks to the gui (used whenever the state name
	// needs to change)
	void reset_group_id_gui_of_all_tasks() {
		// iterates of all tasks related to this state
		for (Task t : tasks)
			t.reset_group_id();

	}

	// adds all tasks to the gui (used whenever the state name needs to change)
	void add_all_tasks_to_gui() {
		init_accordion_gui();

		// iterates of all tasks related to this state
		for (Task t : tasks)
			add_task_in_accordion_gui(t);
	}

	void freeze_movement_and_trigger_connection() {
		if (debug)
			System.out.println("freezing " + this.name);
		this.movement_status = MovementStatus.FREEZED;
		label.setFocus(false);
	}

	void unfreeze_movement_and_untrigger_connection() {
		if (this.movement_status == MovementStatus.FREEZED) {
			if (debug)
				System.out.println("unfreezing " + this.name);
			this.movement_status = MovementStatus.FREE;
		}
	}

	void remove_gui_connections_involving_this_state() {
		// removing the connections originated in this state
		for (Connection c : connections)
			c.remove_gui_items();

		Main.instance().canvas().root.remove_all_gui_connections_to_a_state(this);
	}

	void remove_all_gui_connections_to_a_state(State dest) {
		// iterating over connections backwards
		for (int i = connections.size() - 1; i >= 0; i--) {
			Connection c = connections.get(i);
			if (c.get_next_state() == dest)
				c.remove_gui_items();
		}
	}

	void remove_all_gui_items() {
		// iterating over connections backwards
		for (int i = connections.size() - 1; i >= 0; i--) {
			Connection c = connections.get(i);
			c.remove_gui_items();
		}

		this.remove_all_tasks_from_gui();

		cp5.remove(this.id + "/label");
		cp5.remove(this.id + "/acc");

	}

	void init_gui_connections_involving_this_state() {
		// initing the connections originated in this state
		for (Connection c : connections)
			c.init_gui_items();

		Main.instance().canvas().root.init_all_gui_connections_to_a_state(this);
	}

	void init_all_gui_connections_to_a_state(State dest) {
		// iterating over connections backwards
		for (int i = connections.size() - 1; i >= 0; i--) {
			Connection c = connections.get(i);
			if (c.get_next_state() == dest)
				c.init_gui_items();
		}
	}
}
