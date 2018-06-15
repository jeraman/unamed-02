package frontend.ui;

import java.io.Serializable;

import controlP5.ControlP5;
import controlP5.ControlP5Constants;
import controlP5.Group;
import frontend.Main;
import processing.core.PApplet;

public abstract class UiElement implements Serializable {
	
	private static final long serialVersionUID = 1L;

	transient protected static ControlP5 cp5;
	
	protected static final String userInputAsDefault = "(USER INPUT)";
	protected static final int defaultColor = ControlP5Constants.THEME_CP52014.getBackground();
	protected static final int errorColor = ControlP5Constants.THEME_RED.getBackground();
	protected static int font_size;
	
	public static void setup(ControlP5 cp5, PApplet p) {
		UiElement.cp5 = cp5;
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
	
	//public abstract Object getValue();
	public abstract boolean hasChanged();
	public abstract void setLastValue();
	public abstract void createUI(String id, String label, int localx, int localy, int w, Group g);

}
