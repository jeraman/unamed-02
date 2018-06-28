package frontend.ui;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textfield;

public class TextfieldUi extends AbstractElementUi {

	protected String defaultText;
	protected String value;
	private String lastValue;
	transient protected Textfield textfield;
	
	public TextfieldUi(String defaultText) {
		this.defaultText = defaultText;
		this.value = defaultText;
		this.lastValue = "";
	}
	
	public String getValue( ) {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDefaultText( ) {
		return this.defaultText;
	}
	
	public void setDefaultText(String newText) {
		this.defaultText = newText;
		
		if (isDefaultValue())
			this.setValue(this.defaultText);
	}
	
	public boolean isDefaultValue() {
		return value.toString().trim().equalsIgnoreCase(defaultText);
	}
	
	@Override
	public boolean hasChanged() {
		return !this.lastValue.trim().equalsIgnoreCase(this.value);
	}

	@Override
	public void setLastValue() {
		this.lastValue = value;
	}

	@Override
	public void createUI(String id, String label, int localx, int localy, int w, Group g) { 
		this.textfield = (cp5.addTextfield(id + "/" + label)
		.setPosition(localx, localy)
		.setSize(w, (int) (font_size * 1.25))
		.setHeight(defaultHeight)
		.setGroup(g)
		.setAutoClear(false)
		.setLabel(label)
		.align(ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
		.onClick(callbackEmptyWhenUsingUserInput())
		.onChange(callbackPressEnterOrOutside())
		.onReleaseOutside(callbackPressEnterOrOutside()));
	}
	
	private CallbackListener callbackEmptyWhenUsingUserInput() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {

				String content = theEvent.getController().getValueLabel().getText();

				if (content.trim().equals(defaultText))
					textfield.setText("");
			}
		};
	}
	
	public CallbackListener callbackPressEnterOrOutside() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				String content = theEvent.getController().getValueLabel().getText();
				
				//if there parameter should be controlled via user input, do nothing
				if (content.trim().equals(defaultText))
					return;
				
				//if user deleted the text, sets user input as default value
				if (content.trim().equals("")) {
					content = defaultText;
					textfield.setText(content);
				}
				
				setValue(content);
				
			}
		};
	}

}
