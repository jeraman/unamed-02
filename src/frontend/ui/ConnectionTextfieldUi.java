package frontend.ui;

import javax.script.ScriptException;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textfield;
import frontend.Main;
import frontend.core.Expression;
import frontend.core.MainCanvas;

public class ConnectionTextfieldUi extends TextfieldUi {

	private static final String classDefaultText = "true";
	public static final int width = 20;
	private int currentBackgroundColor;

	public ConnectionTextfieldUi() {
		this(classDefaultText);
	}
	public ConnectionTextfieldUi(String defaultText) {
		super(defaultText);
	}

	protected void setDefaultColorOnTextfield() {
		if (textfield != null) {
			currentBackgroundColor = connectionBackgroundColor;
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
		if (textfield != null)
			this.textfield.remove();
	}

	public void hide() {
		if (textfield != null) 
			this.textfield.hide();
	}

	public void show() {
		if (textfield != null) 
			this.textfield.show();
	}

	public void setPosition(int x, int y) {
		if (textfield != null)
			this.textfield.setPosition(x + width, y);
	}

	public boolean isMouseOver() {
		if (textfield != null)
			return this.textfield.isMouseOver();
		else
			return false;
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
		this.textfield = cp5.addTextfield(id + "/condition")
				.setText(value)
				.setColorValue(whiteColor)
				.setColorBackground(connectionBackgroundColor)
				.setColorForeground(connectionBackgroundColor)
				.setColorValue(whiteColor)
				.setWidth(getLabelWidth())
				.setHeight(15).setFocus(false)
				.onEnter(generate_callback_textfield_enter())
				.onLeave(generate_callback_textfield_leave())
				.onClick(callbackEmptyWhenUsingUserInput())
				.onChange(callbackPressEnterOrOutside())
				.setAutoClear(false)
				.setLabel("");
		this.textfield.onReleaseOutside(callbackPressEnterOrOutside());
	}

	CallbackListener generate_callback_textfield_enter() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				textfield.setColorBackground(connectionForegroundColor);
				textfield.setColorForeground(connectionForegroundColor);
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
				
				String content = textfield.getText();

				// if there parameter should be controlled via user input, do
				// nothing
				if (content.trim().equals(defaultText))
					return;

				// if user deleted the text, sets user input as default value
				if (content.trim().equals("")) {
					content = defaultText;
					textfield.setText(content);
				}

//				System.out.println("getLabelWidth " + getLabelWidth());
				resizeTextfieldWidth();
				setValue(content);
				evaluateAsBoolean();
			}
		};
	}

}
