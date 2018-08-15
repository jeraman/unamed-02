package frontend;

import java.util.Vector;

import controlP5.ControlP5;
import ddf.minim.Minim;
import frontend.core.Blackboard;
import frontend.core.MainCanvas;
import frontend.ui.AbstractElementUi;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PFont;
import soundengine.util.MidiIO;

public class ZenStates {
	
	public Main p;
	public static MainCanvas canvas;
	public static OscP5 oscP5; // my osc variables
	public static ControlP5 cp5; // my controlP5 variable for gui
	public static Minim minim;

	// system's default port for receiveing osc messages
	public static String SERVER_IP;
	public static int SERVER_PORT;
	public static int OSC_RECV_PORT;
	public static int STATE_CIRCLE_SIZE;
	public static int FONT_SIZE;
	public static PFont FONT;
	public static String USER_ID;
	public static boolean debug = false;
	
	public boolean keyReleased = false;
	
	public static boolean mouseRightButtonReleased = false;
	volatile public static boolean is_loading = false;

	public ZenStates(Main p) {
		this.p = p;
		this.setup();
	}

	public void setup() {
		is_loading = true;
		p.background(0);
		p.smooth();
		setupUtil();
		canvas = new MainCanvas(p, cp5);
		is_loading = false;
	}


	// calls all other utils
	void setupUtil() {
		load_config();
		setup_osc();
		setup_controlP5_and_Font();
		setupAudio();
		AbstractElementUi.setup(cp5, p);
	}

	void setupAudio() {
		minim = new Minim(p);
		MidiIO.setup(p);
	}

	// function for setting up osc
	void setup_osc() {
		// start oscP5, listening for incoming messages.
		oscP5 = new OscP5(p, OSC_RECV_PORT);
	}

	// function for setting up controlp5
	void setup_controlP5_and_Font() {
		cp5 = new ControlP5(p);
		
		// this link covers changing font size:
		// https://www.kasperkamperman.com/blog/processing-code/controlp5-library-example2/
		// also here for the origins of the bug
		// https://forum.processing.org/one/topic/controlp5-blurry-text.html

		// PFont pfont = createFont("Arial",15,true); // use true/false for
		// smooth/no-smooth
		// ControlFont font = new ControlFont(pfont,12);
		// cp5.setFont(font);
		// textFont(pfont);
		
		FONT = cp5.getFont().getFont();
		cp5.setFont(FONT, FONT_SIZE);
		
		p.textFont(FONT);
		p.textSize(FONT_SIZE);
	}
	
	
	public void draw() {
		p.background(0);

		// if is loading an open patch, do not draw anything
		if (is_loading) {
			p.fill(255);
			p.textAlign(p.CENTER);
			p.text("loading... please, wait.", p.width / 2, p.height / 2);
			return;
		}
			
		// draws the scenario
		canvas.draw();

		if (keyReleased)
			keyReleased = false;
		if (mouseRightButtonReleased)
			mouseRightButtonReleased = false;

		//serializer.autosave();
	}
	
	public void noteOn(int channel, int pitch, int velocity) {
		canvas.noteOn(channel, pitch, velocity);
	}

	public void noteOff(int channel, int pitch, int velocity) {
		canvas.noteOff(channel, pitch, velocity);
	}

	public void controllerChange(int channel, int number, int value) {
		MidiIO.inputControllerChange(channel, number, value);
	}

	void oscEvent(OscMessage msg) {
		if (debug) {
			System.out.println("### received an osc message.");
			System.out.println(" addrpattern: " + msg.addrPattern());
			System.out.println(" typetag: " + msg.typetag());
		}

		canvas.oscEvent(msg);
	}

	void load_config() {

		String lines[] = p.loadStrings(p.sketchPath() + "/data/config.txt");
		Vector<String> params = new Vector<String>();

		for (int i = 0; i < lines.length; i++)
			if (!lines[i].trim().startsWith("#"))
				params.add((String) lines[i]);

		if (debug) {
			System.out.println("SERVER IP: " + params.get(0));
			System.out.println("SERVER PORT: " + params.get(1));
			System.out.println("INCOMING OSC PORT: " + params.get(2));
			System.out.println("STATE CIRCLE SIZE: " + params.get(3));
			System.out.println("FONT SIZE: " + params.get(4));
			System.out.println("USER ID: " + params.get(5));
		}

		SERVER_IP = params.get(0);
		SERVER_PORT = Integer.parseInt(params.get(1));
		OSC_RECV_PORT = Integer.parseInt(params.get(2));
		STATE_CIRCLE_SIZE = Integer.parseInt(params.get(3));
		FONT_SIZE = Integer.parseInt(params.get(4));
		USER_ID = params.get(5);
	}

	///////////////////////////////////////
	// ui related methods

	public boolean cmg_or_ctrl_pressed = false;
	public boolean should_copy = false;

	public void keyPressed() {
		if (debug)
			System.out.println("keyCode: " + p.keyCode + " key: " + p.key);

		// if is loading an open patch, do not draw anything
		if (is_loading)
			return;

		// updating cmg_or_ctrl_pressed if contrll or command keys were pressed
		if (p.keyCode == 17 || p.keyCode == 27 || p.keyCode == 157)
			cmg_or_ctrl_pressed = true;

		if (p.key == 'b') {
			System.out.println("changing begin");
			this.canvas.nextBegin();
		}		
		
		// if control or command key are not pressed, ignore
		if (!cmg_or_ctrl_pressed)
			return;

		switch (p.keyCode) {
		case 61: // +
			// create_state();
			canvas.process_plus_key_pressed();
			break;
		case 45: // -
			// remove_state();
			canvas.process_minus_key_pressed();
			break;
		case 32: // spacebar
			if (canvas.is_running())
				canvas.button_stop();
			else
				canvas.button_play();
			break;
		case 83: // s
			canvas.button_save();
			break;
		case 76: // l
			canvas.button_load();
			break;
		}
	}
	
	public void keyReleased() {
		if (is_loading)
			return;
		
		keyReleased = true;

		// updating cmg_or_ctrl_pressed if control or command keys were pressed
		if (p.keyCode == 17 || p.keyCode == 27 || p.keyCode == 157)
			cmg_or_ctrl_pressed = false;
	}

	public void mousePressed() {
		if (is_loading)
			return;

		if (p.mouseButton == p.RIGHT)
			canvas.process_right_mouse_button();
		// canvas.process_shift_key();

		if (p.mouseButton == p.LEFT) {
			// if the key is not pressed, i'm not interested
			if (!p.keyPressed)
				return;

			switch (p.keyCode) {
			// in case the key it's shift
			// case 16:
			// canvas.process_shift_key();
			// break;
			// if alt key was pressed
			case 18:
				canvas.process_copy();
				break;
			}

		}
	}
	
	public void mouseDragged() {
		if (is_loading)
			return;
		
		if (p.mouseButton == p.RIGHT)
			canvas.start_dragging_connection();
	}
	
	public void mouseReleased() {
		if (is_loading)
			return;
		
		if (p.mouseButton == p.RIGHT) {
			mouseRightButtonReleased = true;
			canvas.closeConnectionAttempt();
		}
	}


	// checks if the user released the key minus
	public boolean user_pressed_minus() {
		boolean result = keyReleased && p.key == '-';
		// returns the result
		return result;
	}
	
	public static String whatUserIsPlaying() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.whatUserIsPlaying();
		else
			return "";
	}
	
	public static boolean thereIsKeyDown() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.thereIsKeyDown();
		else
			return false;
	}
	
	public static boolean thereIsKeyReleased() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.thereIsKeyReleased();
		else
			return false;
	}
	
	public static int numberOfKeyPressed() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.numberOfKeyPressed();
		else
			return 0;
	}
	
	public static String getLastPlayedNote() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.getLastPlayedNote();
		else
			return "";
	}

	public static int getBeat() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.getBeat();
		else
			return -1;
	}

	public static int getBar() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.getBar();
		else
			return -1;
	}

	public static int getNoteCount() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.getNoteCount();
		else
			return -1;
	}

	public static int getBPM() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.getBPM();
		else
			return -1;
	}
	
	public static float getTime() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.getTime();
		else
			return -1;
	}
	
	public static float getSeconds() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.getSeconds();
		else
			return -1;
	}
	
	public static int getMinutes() {
		if (canvas != null && !ZenStates.is_loading)
			return canvas.getMinutes();
		else
			return -1;
	}
	
	public static Blackboard board() {
		return canvas.board;
	}
}
