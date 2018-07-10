package frontend.ui;

import java.io.Serializable;

import javax.script.ScriptException;

import controlP5.ControlP5;
import controlP5.ControlP5Constants;
import controlP5.Group;
import frontend.Main;
import frontend.core.Blackboard;
import frontend.core.Expression;
import processing.core.PApplet;

public abstract class AbstractElementUi implements Serializable {
	
	private static final long serialVersionUID = 1L;

	transient protected static ControlP5 cp5;
	
	protected static final String userInputAsDefault = "(USER INPUT)";
	public static final int defaultTaskColor = ControlP5Constants.THEME_CP52014.getBackground();
	public static final int connectionBackgroundColor = Main.instance().color(0, 0, 0, 50);
	public static final int connectionForegroundColor = Main.instance().color(0, 116, 217, 200);
	public static final int blackboardBackgroundColor = Main.instance().color(255, 50);
	public static final int blackboardForegroundColor = Main.instance().color(255, 100);
	public static final int blackboardHeaderColor = Main.instance().color(255, 200);
	public static final int blackboardHeaderTextColor = Main.instance().color(50);
	public static final int whiteColor = Main.instance().color(255, 255);
	protected static final int errorColor = ControlP5Constants.THEME_RED.getBackground();
	protected static final int defaultHeight = 15;
	protected static int font_size;
	
	public static void setup(ControlP5 cp5, PApplet p) {
		AbstractElementUi.cp5 = cp5;
		font_size = (int)(((Main)p).get_font_size());
	}
	
	public boolean update() {
		if (this.hasChanged()) {
			this.setLastValue();
			return true;
		}
		else 
			return false;
	}
	
	public boolean evaluateAsBoolean(Object o) throws ScriptException {
		return Boolean.parseBoolean(this.evaluateAsString(o));
	}

	public float evaluateAsFloat(Object o) throws ScriptException {
		return Float.parseFloat(this.evaluateAsString(o));
	}

	public int evaluateAsInteger(Object o) throws ScriptException {
		return Integer.parseInt(this.evaluateAsString(o));
	}

	public String evaluateAsString(Object o) throws ScriptException {
		return this.evaluate(o).toString();
	}

	// function that tries to evaluates the value (if necessary) and returns the real value
	public Object evaluate(Object o) throws ScriptException {
		Object ret = o;
		Blackboard board = Main.instance().board();

		// If added an expression, process it and save result in blackboard.
		if (o instanceof Expression)
			ret = ((Expression) o).eval(board);

		return ret;
	}
	
	//public abstract Object getValue();
	public abstract boolean hasChanged();
	public abstract void setLastValue();
	public abstract void createUI(String id, String label, int localx, int localy, int w, Group g);

}
