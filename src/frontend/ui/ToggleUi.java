package frontend.ui;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ControlP5Constants;
import controlP5.Group;
import controlP5.ScrollableList;
import controlP5.Textlabel;
import controlP5.Toggle;
import frontend.tasks.Task;

public class ToggleUi extends AbstractElementUi {

	private boolean value;
	private String lastValue;
	private static boolean defaultValue = true;
	private transient Toggle toggle;

	public ToggleUi() {
		this.value = defaultValue;
		// this.enable();
		this.lastValue = "";
	}

	@Override
	public boolean hasChanged() {
		return !this.lastValue.trim().equalsIgnoreCase(this.value + "");
	}

	@Override
	public void setLastValue() {
		this.lastValue = this.value + "";
	}

	public boolean getValue() {
		return this.value;
	}

	public void disable() {
		value = false;
		//this.toggle.setColor(ControlP5Constants.THEME_GREY);
	}

	public void enable() {
		value = true;
		//this.toggle.setColor(ControlP5Constants.THEME_CP52014);
	}

	@Override
	public void createUI(String id, String label, int localx, int localy, int w, Group g) {
		this.group = g;
		this.toggle = (cp5.addToggle(id + "/" + label)
				.setPosition(localx, localy)
				.setSize(w, (int) (font_size * 1.25))
				.setGroup(g)
				.setMode(ControlP5.SWITCH)
				.setLabel(label)
				.setValue(!this.value)
				.onChange(callback())
				.onReleaseOutside(callback()));
		//this.enable();
		this.toggle.getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE);
	}

	private CallbackListener callback() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				//if (!group.isOpen()) 
				//	return;
				System.out.println("callback");
				float temp = toggle.getValue();
				if (temp == 0.0)
					enable();
				else
					disable();
			}
		};
	}

}
