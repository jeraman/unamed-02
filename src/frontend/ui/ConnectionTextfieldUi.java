package frontend.ui;

import javax.script.ScriptException;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textfield;
import frontend.Expression;
import frontend.Main;

public class ConnectionTextfieldUi extends TextfieldUi {

	private static final String classDefaultText = "true";
	public static final int width = 20;
	private int currentBackgroundColor; 
	

	public ConnectionTextfieldUi(String defaultText) {
		super(defaultText);
	}

	protected void setDefaultColorOnTextfield() {
		if (textfield != null) {
			currentBackgroundColor = defaultBackgroundConnectionColor;
			textfield.setColorBackground(currentBackgroundColor);
			textfield.setColorForeground(currentBackgroundColor);
		}
	}
	
	protected void setErrorColorOnTextfield() {
		if (textfield != null) {
			currentBackgroundColor = errorColor;
			textfield.setColorBackground(currentBackgroundColor);
			textfield.setColorForeground(currentBackgroundColor);
		}
	}

	public Expression getExpression() {
		return new Expression(this.value);
	}

	public void remove() {
		this.textfield.remove();
	}

	public void hide() {
		this.textfield.hide();
	}

	public void show() {
		this.textfield.show();
	}

	public void setPosition(int x, int y) {
		this.textfield.setPosition(x + width, y);
	}

	public boolean isMouseOver() {
		return this.textfield.isMouseOver();
	}

	public int getLabelWidth() {
		String currentText;
		
		if (textfield != null)
			currentText = textfield.getText().trim();
		else
			currentText = value;
			
		return (int) (width + (Main.instance().textWidth(currentText)));
	}
	
	protected void resizeTextfieldWidth() {
		textfield.setWidth(getLabelWidth());
	}

	@Override
	public void createUI(String id, String label, int localx, int localy, int w, Group g) {
		this.createUI(id);
	}

	public void createUI(String id) {
		this.textfield = cp5.addTextfield(id + "/condition").setText(value).setColorValue(whiteColor)
				.setColorBackground(defaultBackgroundConnectionColor)
				.setColorForeground(defaultBackgroundConnectionColor)
				.setColorValue(whiteColor)
				.setWidth(getLabelWidth())
				.setHeight(15)
				.setFocus(false)
				.onEnter(generate_callback_textfield_enter()) 
				.onLeave(generate_callback_textfield_leave()) 
				.onClick(callbackEmptyWhenUsingUserInput())
				.onChange(callbackPressEnterOrOutside())
				.onReleaseOutside(callbackPressEnterOrOutside())
				.setAutoClear(false)
				.setLabel("");
	}

	CallbackListener generate_callback_textfield_enter() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				textfield.setColorBackground(defaultForegroundConnectionColor);
				textfield.setColorForeground(defaultForegroundConnectionColor);
			}
		};
	}

	CallbackListener generate_callback_textfield_leave() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				textfield.setColorBackground(currentBackgroundColor);
				textfield.setColorForeground(currentBackgroundColor);
			}
		};
	}
	
	protected CallbackListener callbackPressEnterOrOutside() {
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
		
				System.out.println("getLabelWidth " + getLabelWidth());
				resizeTextfieldWidth();
				setValue(content);	
				evaluateAsBoolean();
			}
		};
	}

}
