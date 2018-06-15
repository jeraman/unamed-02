package frontend.ui;

import java.util.List;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.ScrollableList;
import frontend.Main;

public class ScrollableListUI extends UiElement {
	
	private String selection;
	private String lastSelection;
	List<String> options; 
	private ScrollableList scrollableList;
	
	public ScrollableListUI (List<String> list) {
		this.options = list;
		this.lastSelection = "";
		this.selection = list.get(0);
	}
	
	void update(String newSelection) {
		this.selection = newSelection;
		//processWavetypeChange();
	}
	
	public boolean hasChanged(String newValue) {
		return !this.lastSelection.trim().equalsIgnoreCase(newValue);
	}
	
	public void setLastValue(String l) {
		this.lastSelection = l;
	}
	
	public String getValue() {
		return this.selection;
	}
	
	public CallbackListener callback() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				int index = (int)theEvent.getController().getValue();
				update(options.get(index));
			}
		};
	}
	
	public void createUI(String id, String name, int localx, int localy, int w, Group g) {
		this.scrollableList = cp5.addScrollableList(id + "/" + name)
		.setPosition(localx, localy)
		.setLabel(name)
		.setSize(w, 100)
		.setGroup(g)
		.setDefaultValue(2)
		.close()
		.setValue(1)
		.setBarHeight(20)
		.setItemHeight(20)
		.align(ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
		.onChange(callback())
		.setType(ControlP5.DROPDOWN)
		.addItems(options);
	}

}