package frontend.core;


import processing.core.PApplet;
import soundengine.SoundEngine;
import soundengine.util.Util;

import java.util.UUID;
import java.util.Vector;

import controlP5.*;
import frontend.Main;
import frontend.ZenStates;
import frontend.tasks.Task;
import frontend.ui.visuals.StateMachinePreview;

public class StateMachine extends Task {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private State begin;
	private State actual;
	private Vector<State> states;
	protected String title; //this should be the name. the super name should be an id instead.

	private float stateTimerMilestone = 0;
	private float stateTimer          = 0;
	public boolean debug;
	private boolean brandnew; //has the user added  any state or task added to this state machine?
	
	//private TempoControl timeCounter;
	
	transient private StateMachinePreview smp;

	//contructor
	public StateMachine (PApplet p, ControlP5 cp5, String name) {
		super (p, cp5, name, new SoundEngine(ZenStates.minim));
		title   = name;
		begin   = new State(p, cp5, State.generateRandomName(), this.eng);
		states  = new Vector<State>();
		debug = ZenStates.debug;
		
		brandnew = true;
		actual = begin;
		
//		this.timeCounter = new TempoControl();
//		this.timeCounter.createUi();
		
		if (debug)
			System.out.println("State_Machine " + this.name + " is inited!");
		
		if (Main.log != null) 
			Main.log.countCreatedSM();
	}
	
	//contructor
	public StateMachine (PApplet p, ControlP5 cp5, String name, boolean repeat) {
		this(p, cp5, name);
		this.repeat = repeat;
	}
	
//	protected TempoControl getTempoControl() {
//		return this.timeCounter;
//	}

	void check_if_any_substatemachine_needs_to_be_reloaded_from_file () {
		this.begin.check_if_any_substatemachine_needs_to_be_reloaded_from_file();
		
		for (State s : states) 
			s.check_if_any_substatemachine_needs_to_be_reloaded_from_file();
	}
	
	void reload_from_file () {
		//checks if there is a file with the same name
		boolean there_is_file = ((Main)p).serializer.check_if_file_exists_in_sketchpath(title);
		
		if (there_is_file) {
			StateMachine loaded = ((Main)p).serializer.loadSubStateMachine(title);
			mirror(loaded);			
		}
	}

	public void build (PApplet p, ControlP5 cp5) {
		this.p = p;
		this.cp5 = cp5;
		this.eng = new SoundEngine(ZenStates.minim);

		this.begin.build(p, cp5, eng);

		for (State s : states)
			s.build(p, cp5, eng);
		
//		this.timeCounter.createUi();
	}
	
	public void nextBegin() {
		if (states.size() < 1) 
			return;

		State temp = this.states.firstElement();
		this.states.remove(0);
		this.add_state(this.begin);
		this.begin = temp;
		
		Main.log.countChangedBegin();
	}
	
	StateMachine clone_state_machine_saved_in_file(String title) {
		return ((Main)p).serializer.loadSubStateMachine(title);
	}
	
	StateMachine clone_state_machine_not_saved_in_file(String title) {
		StateMachine duplicate = null;
		
		//clone the state machine
		duplicate = new StateMachine(p, cp5, title, this.repeat);
		
		//mpving the begin state
		duplicate.begin= this.begin.clone_it();
		
		//clone the states
		for (State s: states)
			duplicate.add_state(s.clone_it());
		
		duplicate.hide();
		
		return duplicate;
	}
	
	public StateMachine clone_it() {
		StateMachine duplicate = null;
		
		//if there is a file with this name, load it from file!
		if (((Main)p).serializer.existsSubStateMachineInFile(title))
			duplicate = clone_state_machine_saved_in_file(title);
		else 
			duplicate = clone_state_machine_not_saved_in_file(title);
		
		return duplicate;
	}
	
	//makes the current statemachine to mirror another statemachine sm
	void mirror (StateMachine sm) {
		this.title    = sm.title;
		this.begin    = sm.begin;
		this.states   = sm.states;
		this.brandnew = false;
		this.repeat   = sm.repeat;
		
		reinit_id_and_load_gui_internal_elements();	
	}
	
	
	//loads the ui of internal elements
	void reinit_id_and_load_gui_internal_elements () {
		this.begin.remove_all_gui_items();
		for (State s : states) 
			s.remove_all_gui_items();
		
		this.begin.reinit_id();
		for (State s : states) 
			s.reinit_id();
		
		this.begin.initStateGuiWithTasksAndConnections();
		for (State s : states) 
			s.initStateGuiWithTasksAndConnections();
		
		this.begin.hide_gui();
		for (State s : states) 
			s.hide_gui();
	}
	
	
	StateMachine getReferenceForThisStateMachine() {
		return this;
	}

	//run all tasks associated to this node
	public void run () {
		if (!should_run())
			return;

		this.status = Status.RUNNING;
		update_actual(begin);
		reset_state_timer();
		actual.run();
		
		if (debug)
			System.out.println("running the State_Machine " + this.title + ". actual is " + actual.get_name());
	}
	
	public void start() {
		if (debug)
			System.out.println("starting a statemachine " + this);
		this.status = Status.RUNNING;
		begin.start();
	}

	//stops all tasks associated to this node
	public void stop() {
		//stopping all states...
		for (State s : states) {
			s.reset_first_time();
			s.stop();
		}

		//stop begin and end
		begin.reset_first_time();
		begin.stop();

		update_actual(null);

		//resets the stateTimer for this state machine
		reset_state_timer();

		this.status = Status.INACTIVE;

		if (debug)
			System.out.println("stopping State_Machine" + this.name);
	}

	synchronized void clear() {
		this.stop();

		for (int i = states.size()-1; i >= 0; i--) {
			State s = states.get(i);
			s.clear();
			remove_state(s);
		}

		begin.clear();
		
//		this.timeCounter.removeUi();
	}

	//stops all tasks associated to this node
	public void interrupt() {
		//stopping all states...
		for (State s : states)
			s.interrupt();

		//stop begin and end
		begin.interrupt();
		update_actual(null);

		//resets the stateTimer for this state machine
		reset_state_timer();

		this.status = Status.DONE;
		if (debug)
			System.out.println("iterrupting State_Machine" + this.name);
	}
	
	public String whatUserIsPlaying() {
		return this.eng.whatUserIsPlaying();
	}
	
	public String getLastPlayedNote() {
		return this.eng.getLastPlayedNote();
	}
	
	public boolean thereIsKeyDown() {
		return this.eng.thereIsKeyDown();
	}
	
	public boolean thereIsKeyReleased() {
		return this.eng.thereIsKeyReleased();
	}
	
	public int numberOfKeyPressed() {
		return this.eng.numberOfKeyPressed();
	}

	void update_title(String newtitle) {		
		//String n = get_formated_blackboard_title();
		//((Main)p).board.remove(n+"_timer");
		this.title = newtitle;
	}

	void update_actual (State next) {
		actual.is_actual = false;

		//does nothing if the actuall will be null
		if (next==null) return;

		//updating the actual
		actual = next;
		actual.is_actual = true;
	}

	//updates the status of this state
	public void update_status() {

		//if it' done, no point to execute this
		if (this.status == Status.DONE)  return;

		//updating the status of the actual
		actual.update_status();

		//if there are no states associated to this State_Machine
		if (states.size()==0 & begin.get_number_of_connections()==0) {
			this.status = Status.DONE;
			if (debug)
				System.out.println("State_Machine " + this.name +  " is empty! Done!");
		}

	}

	//function called everytime there is a new input
	void tick() {

		//if not ready yet, returns...
		if (this.status==Status.INACTIVE || this.status==Status.DONE) {
			reset_state_timer(); //sets timer to zero
			return;
		}

		//stores the results for the state changing
		State next = null;

		//updates global variables in the blackboard
		update_global_variables();

		//updates the status of the hfsm
		update_status();

		//tries to update the next
		next = actual.tick();

		//if it really changed to another state
		if (next!=null && next!=actual) {
			//refreshing the stateTimer in the blackboard
			reset_state_timer();
			if (debug)
				System.out.println("changing to a different state. reset stateTimer.");

		} else {
			if (debug)
				System.out.println("changing to the same state. do not reset stateTimer.");
		}

		//in case next is not null, change state!
		if (next!=null)
			update_actual(next);

	}
	
	//in case there are statemachine inside this state, this machine should be saved to file
	void save() {
		//saving the current state machine
		((Main)p).serializer._saveAs(title, this);
		
		//saving substatemachines inside all states...
		for (State s : states)
			s.save();

		//save substatemachines inside begin and end
		begin.save();
		//end.save();
	}
	
	boolean is_brandnew() {
		return brandnew;
	}
	
	public void noteOn(int channel, int pitch, int velocity) {
		this.eng.noteOn(channel, pitch, velocity);
		this.actual.forwardNoteOnToSubStateMachines(channel, pitch, velocity);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		this.eng.noteOff(channel, pitch, velocity);
		this.forwardNoteOffToSubStateMachines(channel, pitch, velocity);
		//this.actual.forwardNoteOffToSubStateMachines(channel, pitch, velocity);
	}
	
	public void forwardNoteOffToSubStateMachines(int channel, int pitch, int velocity) {
		for (State s : states)
			s.forwardNoteOffToSubStateMachines(channel, pitch, velocity);
		begin.forwardNoteOffToSubStateMachines(channel, pitch, velocity);
	}

	//add a state s to this State_Machine
	void add_state(State s) {
		brandnew = false; //this sm is no longer brandnew
		states.addElement(s);
		if (debug)
			System.out.println("State " + s.get_name() + " added to State_Machine " + this.name);
	}

	//remove a state s from this State_Machine
	void remove_state(State s) {
		if (states.contains(s)) {
			this.remove_all_connections_to_a_state(s);

			s.clear();
			//s.remove_all_tasks();
			//s.remove_all_connections();
			//cp5.remove(s.get_id()+"/label");
			//cp5.remove(s.get_id()+"/acc");
			
			this.states.removeElement(s);
			
			if (s == actual) {
				ZenStates.canvas.button_stop();
				if (debug)
					System.out.println("You're removing the state that is currently executing. Halting the state machine.");
			}
			
		} else if (debug)
			System.out.println("Unable to remove state " + s.get_name() + " from State_Machine " + this.name);
	}

	//removes a state based on a certain x y position in the screen
	void remove_state(int x, int y) {
		//iterates over all states
		for (State s : states)
			//if it intersects with a certain x y position
			if (s.intersects_gui(x, y)) {
				//removes this state
				this.remove_state(s);
				//and breaks (one remotion per time))
				return;
			}
	}

	void remove_all_connections_to_a_state (State dest) {
		if (debug)
			System.out.println("removing all connections to " + dest.toString());

		//removing all connection to a state in the begin and in the end
		this.begin.remove_all_connections_to_a_state(dest);

		//iterates over all states
		for (State s : states)
			s.remove_all_connections_to_a_state(dest);
	}
	
	void update_all_connections_to_a_state (State dest, String newid) {
		if (debug)
			System.out.println("updating all connections to " + dest.toString());
		
		//updating all connection to a state in the begin and in the end
		this.begin.update_all_connections_to_a_state(dest, newid);

		//iterates over all states
		for (State s : states)
			s.update_all_connections_to_a_state(dest, newid);
	}

	//returns a state by its unique id. returns null if not available
	State get_state_by_id(String id) {
		State result = null;

		if (this.begin.get_id().equalsIgnoreCase(id)) result=this.begin;
		//if (this.end.get_id().equalsIgnoreCase(id))   result=this.end;

		//iterates over all states
		for (State s : states)
			if (s.get_id().equalsIgnoreCase(id)) result=s;

		if (debug) {
			if (result != null)
				System.out.println("found! " + result.toString());
			else
				System.out.println("problem!");
		}

		//returns the proper result
		return result;
	}

	//add a task t to the initialization of this State_Machine
	void add_initialization_task (Task t) {
		begin.add_task(t);
		if (debug)
			System.out.println("Task " + t.name + " added to the initialization of State_Machine " + this.name);
	}

	//remove a task t to the initialization of this State_Machine
	void remove_initialization_task (Task t) {
		begin.remove_task(t);
		if (debug)
			System.out.println("Task " + t.name + " removed from the initialization of State_Machine " + this.name);
	}

	//formats the title for the blackboard
//	String get_formated_blackboard_title () {
//		String n = this.title.replace(".", "_");
//		n = n.replace(" ", "_");
//		return n;
//	}

	//inits the global variables related to this blackboard
	void init_global_variables() {
//		String n = get_formated_blackboard_title();
//		Main.instance().board.put(n+"_timer", 0);
	}
	
	//updates the global variable related to this blackboard
	void update_global_variables() {
		update_state_timer();
//		String n = get_formated_blackboard_title();
//		Main.instance().board.put(n+"_timer", this.stateTimer);
	}

	void update_state_timer() {
		this.stateTimer = ((float)Util.millis()/1000f)-stateTimerMilestone;
	}

	void reset_state_timer() {
		this.stateTimerMilestone = (float)Util.millis()/1000f;
		this.stateTimer          = 0;
		update_global_variables();
	}

	//returns how many states we have in this state machine
	int get_states_size() {
		return this.states.size();
	}

	/*******************************************
	 ** GUI FUNCTIONS ***************************
	 ********************************************/
	//draws all states associated with this state_machine
	public void draw() {
		//if the Papplet wasn't loaded yet
		if (p==null) return;

		update_gui();

		//drawing the entry state
		begin.draw();
		begin.draw_begin();
		//drawing the states begining to this state machine
		for (State s : states)
			s.draw();

		//drawing the actual, if running
		if (this.status==Status.RUNNING)
			actual.draw_actual();
	}

	void draw_pie_menus () {
		//drawing the entry state
		begin.draw_pie();
		//drawing the states begining to this state machine
		for (State s : states)
			s.draw();
	}

	void update_gui () {
		//verifies if user wants to create a new connection for this state
		update_state_connections_on_gui();
	}

	void hide() {
		begin.hide_gui();
		for (State s : states)
			s.hide_gui();
	}

	void show() {
		begin.show_gui();
		for (State s : states)
			s.show_gui();
		//end.show_gui();
	}

	//returns a state that intersect test_x, test_y positions
	State intersects_gui(int test_x, int test_y) {
		State result = null;

		//testing the begin & end states
		if (this.begin.intersects_gui(test_x, test_y))  return this.begin;

		//iterates over the remaining states
		for (State s : states)
			//if intersects...
			if (s.intersects_gui(test_x, test_y)) {
				if (debug)
					System.out.println("i found someone to be intersected");
				//updates the result
				result = s;
				break;
			}

		return result;
	}

	//reinit any name the user was trying to change it
	void reset_all_names_gui() {
		//resets the begin and the end states
		this.begin.reset_name();

		//iterates over the remaining states
		for (State s : states)
			//reinit the name in case the user was trying to change it
			s.reset_name();
	}

	public CallbackListener generate_callback_enter() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {

				//if this group is not open, returns...
				if (!((Group)cp5.get(get_gui_id())).isOpen()) return;

				String s = theEvent.getController().getName();
				

				if (s.equals(get_gui_id() + "/name")) {
					String text = theEvent.getController().getValueLabel().getText();
					if (text.trim().equals("")) {
						text="(choose a name)";
						((Textfield)cp5.get(get_gui_id()+ "/name")).setText(text);
					}
					
					//if the name didn't change, return
					if(text.equals(title)) return;
					
					process_title(text, title);
				}
			}
			
			public void process_title(String newtitle, String oldtitle) {
				//does it finish with .zen extension?
				if (newtitle.endsWith(".zen")) { //if yes
					
					//checks if there is a file named newtitle
					boolean there_is_newtitle = ((Main)p).serializer.check_if_file_exists_in_sketchpath(newtitle);
					
					//if there is a file named newtitle
					if (there_is_newtitle) {
						
						//if the curring machine is brandnew
						if (brandnew) {
							
							//p.print("we jsut loaded a sm from file! name: " + loaded.title);
							ZenStates.is_loading = true;
							cp5.setAutoDraw(false);
							//load newtile from file
							StateMachine loaded = ((Main)p).serializer.loadSubStateMachine(newtitle);
							//next step is to copy all parameters of loaded to this state machine
							mirror(loaded);
							ZenStates.is_loading = false;
							cp5.setAutoDraw(true);
							Main.log.countLoadedExistingSM();
						
						//if the current machine isn't brandnew
						} else {
							
							//remove the .zen extension
							newtitle = newtitle.replace(".zen", "");
							//update textfield on the ui
							((Textfield)cp5.get(get_gui_id()+ "/name")).setText(newtitle);
							//update title
							update_title(newtitle);
						}
					
					//if there is no file named newtitle
					} else {	
						((Main)p).serializer.delete(oldtitle);
						update_title(newtitle);
						((Main)p).serializer._saveAs(newtitle, getReferenceForThisStateMachine());
						if (debug)
							System.out.println("no " + newtitle + " was found in sketchpath");
					}
					
				//if it does not finish with .zen, just update the name
				} else
					update_title(newtitle);
					
			}
		};
	}

	CallbackListener generate_callback_open_substate() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {

				String s = theEvent.getController().getName();
				smp.open();
				if (debug)
					System.out.println("open substate " + s);
				Main.log.countSmZoomIn();
			}
		};
	}

	void close () {
		smp.close();
	}

	public Group load_gui_elements(State s) {

		Group g					= super.load_gui_elements(s);
		  CallbackListener cb_enter = generate_callback_enter();
		  CallbackListener cb_pressed = generate_callback_open_substate();
		  String g_name			  	= this.get_gui_id();
		  int w 					= g.getWidth()-(localx*2);
		  
		  textlabel 	 			= "State Machine";
		  backgroundheight 			= (int)(localoffset * 3.8);
		    
		  g.setBackgroundHeight(backgroundheight);
		  g.setLabel(textlabel);

		cp5.addTextfield(g_name+"/name")
		.setPosition(localx, localy)
		.setSize(w, (int)(font_size*1.25))
		.setGroup(g)
		.setAutoClear(false)
		.setLabel("name")
		.setText(this.title+"")
		.onChange(cb_enter)
		.onReleaseOutside(cb_enter)
		.getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE);
		;

		smp = new StateMachinePreview( (Main)p, this, localx, localy+localoffset);
		g.addCanvas((controlP5.Canvas)smp);

		cp5.addButton(g_name+"/open_preview")
		.setPosition(localx, localy+(int)(2.8*localoffset))
		.setSize(w, (int)(font_size*1.25))
		.setValue(0)
		.setLabel("open preview")
		.onPress(cb_pressed)
		.setGroup(g)
		;

		//createGuiToggle(localx, localy+(int)(3.8*localoffset), w, g, callbackRepeatToggle());

		return g;
	}

	void connect_state_if_demanded_by_user (State s) {
		s.unfreeze_movement_and_untrigger_connection();

		State intersected = this.intersects_gui(p.mouseX, p.mouseY);
		//if there is someone to connect to
		if (intersected!=null) {
			//connects
			s.connectWithDefaultCondition(intersected);
			//s.connect_anything_else_to_self();
			brandnew = false; //this statemachine is no longer brandnew
		}
	}

	//verifies on the gui if the user wants to create a new connection
	void update_state_connections_on_gui () {

		//updates the begin state
		if (this.begin.verify_if_user_released_mouse_while_temporary_connecting())
			connect_state_if_demanded_by_user(this.begin);

		//iterates over the remaining states
		for (State s : states)
			//if the mouse was released and there is a temporary connection on gui
			if (s.verify_if_user_released_mouse_while_temporary_connecting()) {
				connect_state_if_demanded_by_user(s);
				break;
			}

	}

	void remove_all_gui_connections_to_a_state (State dest) {

		//removing all connection to a state in the begin and in the end
		this.begin.remove_all_gui_connections_to_a_state(dest);

		//iterates over all states
		for (State s : states)
			s.remove_all_gui_connections_to_a_state(dest);
	}
	
	void init_all_gui_connections_to_a_state (State dest) {

		//removing all connection to a state in the begin and in the end
		this.begin.init_all_gui_connections_to_a_state(dest);

		//iterates over all states
		for (State s : states)
			s.init_all_gui_connections_to_a_state(dest);
	}

	public void reset_gui_fields() {
		String g_name = this.get_gui_id();
		String nv;

		//if this group is not open, returns...
		if (!((Group)cp5.get(get_gui_id())).isOpen()) return;

		//nothing in here!
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
