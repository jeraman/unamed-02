package frontend.ui;

import javax.script.ScriptException;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textfield;
import frontend.Blackboard;
import frontend.Expression;
import frontend.Main;
import frontend.tasks.Task;



public class ComputableTextfieldUI extends UiElement {
	
	private Object valueExpression;
	private String lastComputedValue;
	transient private Textfield textfield;

	public ComputableTextfieldUI() {
		this("-1");
	}

	public void setValueExpression(String newValue) {
		this.valueExpression = new Expression(newValue);
		this.lastComputedValue = "";
	}
	
	public ComputableTextfieldUI(String defaultValue) {
		this.setValueExpression(defaultValue);
	}
	
	public String getTextFieldText() {
		return this.textfield.getText();
	}
	
	public Textfield getTextField() {
		return this.textfield;
	}
	
	public void setTextField(Textfield t) {
		this.textfield = t;
		this.textfield.setText(this.valueExpression.toString());
	}
	
	private boolean isValueExpressionEquals(String target) {
		return this.valueExpression.toString().trim().equalsIgnoreCase(target);
	}
	
	private boolean isNecessaryToUpdateValueExpression(String newValue) {
		return this.valueExpression == null || !this.isValueExpressionEquals(newValue);
	}
	
	public void setLastValue(String l) {
		this.lastComputedValue = l;
	}
	
	public boolean hasChanged(String newValue) {
		return !this.lastComputedValue.trim().equalsIgnoreCase(newValue);
	}
	
	public void updateValueExpression() {
		this.updateValueExpression(getTextFieldText());
	}

	public void updateValueExpression(String newValue) {
		if (!isNecessaryToUpdateValueExpression(newValue))
			return;
		this.valueExpression = new Expression(newValue);
		getValue();
	}
	
	public String getValue() {
		String valueToUpdate = this.lastComputedValue;
		
		if (this.isValueExpressionEquals(Task.userInputAsDefault)) {
			valueToUpdate = "-1";
			textfield.setColorBackground(defaultColor);
		} else
			try {
				valueToUpdate = evaluateAsFloat(this.valueExpression)+"";
				textfield.setColorBackground(defaultColor); 
			} catch (ScriptException | NumberFormatException e) {
				System.out.println("ScrriptExpression-related error thrown, unhandled update.");
				valueToUpdate = "-1";
				textfield.setColorBackground(errorColor); 
			}
		
		return valueToUpdate;
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
		return this.evaluate_value(o).toString();
	}

	// function that tries to evaluates the value (if necessary) and returns the real value
	public Object evaluate_value(Object o) throws ScriptException {
		Object ret = o;
		Blackboard board = Main.instance().board();

		// If added an expression, process it and save result in blackboard.
		if (o instanceof Expression)
			ret = ((Expression) o).eval(board);

		return ret;
	}
	
	
	public void createUI(String id, String label, int localx, int localy, int w, Group g) { 
		this.textfield = (cp5.addTextfield(id + "/" + label)
		.setPosition(localx, localy)
		.setSize(w, (int) (font_size * 1.25))
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

				if (content.trim().equals(Task.userInputAsDefault))
					textfield.setText("");
			}
		};
	}
	
	public CallbackListener callbackPressEnterOrOutside() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				String content = theEvent.getController().getValueLabel().getText();
				
				//if there parameter should be controlled via user input, do nothing
				if (content.trim().equals(userInputAsDefault))
					return;
				
				//if user deleted the text, sets user input as default value
				if (content.trim().equals("")) {
					content = userInputAsDefault;
					textfield.setText(content);
				}
				
				updateValueExpression(content);
			}
		};
	}
}
