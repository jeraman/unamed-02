package frontend.ui;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.DropdownList;
import controlP5.Group;
import frontend.State;

public class ConnectionPriorityDropdownUi extends AbstractElementUi {

	private int value;
	private int lastValue;
	private DropdownList dropdownlist;
	private State parent;
	
	public ConnectionPriorityDropdownUi(int priority, State parent) {
		this.value = priority;
		this.parent = parent;
		this.lastValue = -1;
	}
	
	public boolean hasChanged() {
		return this.lastValue != this.value;
	}
	
	public void setLastValue() {
		this.lastValue = this.value;
	}
	
	public int getPriotity() {
		return this.value;
	}
	
	public void setPriority(int newValue) {
		this.setLastValue();
		this.value = newValue;
	}
	
	public void remove() {
		this.dropdownlist.remove();
	}
	
	public void hide() {
		this.dropdownlist.hide();
	}
	
	public void show() {
		this.dropdownlist.show();
	}
	
	public void setPosition(int newx, int newy) {
		this.dropdownlist.setPosition(newx, newy);
	}
	
	public boolean isMouseOver() {
		return this.dropdownlist.isMouseOver();
	}

	@Override
	public void createUI(String id, String label, int localx, int localy, int w, Group g) {
		this.createUI(id);
	}
	
	public void createUI(String id) {
		this.dropdownlist = cp5.addDropdownList(id + "/priority")
				.setWidth(ConnectionTextfieldUi.width)
				.setItemHeight(20)
				.setBarHeight(15)
				.setBackgroundColor(defaultBackgroundConnectionColor)
				.setColorBackground(defaultBackgroundConnectionColor)
				.setOpen(false)
				.setValue(this.value)
				.setLabel(this.value + "")
				.onChange(generate_callback_dropdown());
		
		init_dropdown_list(parent.get_number_of_connections() - 1);
	}
	
	private void init_dropdown_list(int n) {
		for (int i = 1; i <= n; i++) {
			this.dropdownlist.addItem(i + "", i);
		}
	}
	
	CallbackListener generate_callback_dropdown() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				int newPriority = 1 + (int) theEvent.getController().getValue();
				parent.update_priority(value, newPriority);
			}
		};
	}
}
