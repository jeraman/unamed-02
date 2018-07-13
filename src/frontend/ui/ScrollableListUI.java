package frontend.ui;

import java.util.List;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.ScrollableList;
import controlP5.Textlabel;
import frontend.Main;

public class ScrollableListUI extends AbstractElementUi {
	
	private String selection;
	private String lastSelection;
	private List<String> options; 
	private transient ScrollableList scrollableList;
	private transient Textlabel label;
	
	public ScrollableListUI (List<String> list, int defaultSelection) {
		this.options = list;
		this.lastSelection = "";

		this.processDefaultSelection(defaultSelection);
	}
	
	private void processDefaultSelection(int defaultSelection) {
		if (defaultSelection < options.size())
			this.selection = options.get(defaultSelection);
		else
			this.selection = options.get(0);
	}
	
	void setSelection(String newSelection) {
		this.selection = newSelection;
		//processWavetypeChange();
	}
	
	public boolean hasChanged() {
		return !this.lastSelection.trim().equalsIgnoreCase(this.selection);
	}
	
	public void setLastValue() {
		this.lastSelection = this.selection;
	}
	
	public String getValue() {
		return this.selection;
	}
	
	public CallbackListener callback() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				int index = (int)theEvent.getController().getValue();
				setSelection(options.get(index));
			}
		};
	}
	
	public void createUI(String id, String name, int localx, int localy, int w, Group g) {
		this.label = (cp5.addTextlabel(id+ "/" + name + "/label")
				.setGroup(g)
				.setText(name.toUpperCase())
				.setSize(w, 100)
				.align(ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
				.setPosition(localx, localy+25));

		this.scrollableList = cp5.addScrollableList(id + "/" + name)
				.setPosition(localx, localy)
				.setLabel(this.getValue())
				.setSize(w, 100)
				.setGroup(g)
				.setDefaultValue(2)
				.close()
				.setValue(1)
				.setBarHeight(20)
				.setItemHeight(20)
				.align(ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
				.setType(ControlP5.DROPDOWN)
				.addItems(options)
				.onClick(toFront())
				.onChange(callback());
	}
	
	CallbackListener toFront() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				theEvent.getController().bringToFront();
			}
		};
	}
	

}
