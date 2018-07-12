package frontend.ui;

import javax.script.ScriptException;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textfield;
import frontend.Main;
import frontend.core.Blackboard;
import frontend.core.Expression;
import frontend.tasks.Task;



public class ComputableFloatTextfieldUI extends AbstractElementUi {
	
	private Object valueExpression;
	private float computedValue;
	private String lastComputedValue;
	
	private float defaultValue;
	private String defaultText;
	transient private Textfield textfield;
	
	public static final String classDefaultText = "CLICK TO CHANGE";

	public ComputableFloatTextfieldUI(float defaultValue) {
		this(classDefaultText, defaultValue);
	}
	
	public ComputableFloatTextfieldUI(String defaultText, float defaultValue) {
		this.defaultValue = defaultValue;
		this.defaultText = defaultText;
		this.computedValue = this.defaultValue;
		this.setValueExpression(defaultText);
	}
	
	public float getDefaultValue( ) {
		return this.defaultValue;
	}
	
	private void setDefaultValue(float newDefaultValue) {
		this.defaultValue = newDefaultValue;
	}
	
	private void setDefaultText(String newText) {
		this.defaultText = newText;
	}
	
	public void resetDefaults (String newDefaultText, float newDefaultValue) {
		boolean wasDefaultValue = isDefaultValue();
		
		this.setDefaultValue(newDefaultValue);
		this.setDefaultText(newDefaultText);
		
		if (wasDefaultValue) {
			this.setValueExpression(this.defaultText);
			this.textfield.setText(this.defaultText);
		}
	}
	
	public void setValueExpression(String newValue) {
		this.valueExpression = new Expression(newValue);
		this.lastComputedValue = "";
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
	
	protected boolean isValueExpressionEquals(String target) {
		return this.valueExpression.toString().trim().equalsIgnoreCase(target);
	}
	
	private boolean isNecessaryToUpdateValueExpression(String newValue) {
		return this.valueExpression == null || !this.isValueExpressionEquals(newValue);
	}
	
	public void setLastValue() {
		this.lastComputedValue = computedValue+"";
	}
	
	public boolean hasChanged() {
		return !this.lastComputedValue.trim().equalsIgnoreCase(this.computedValue+"");
	}
	
	public void updateValueExpression() {
		this.updateValueExpression(getTextFieldText());
	}

	public void updateValueExpression(String newValue) {
		if (!isNecessaryToUpdateValueExpression(newValue))
			return;
		this.valueExpression = new Expression(newValue);
		computeValue();
	}
	
	public float getValue() {
		return this.computedValue;
	}
	
	public boolean isDefaultValue() {
		return valueExpression.toString().trim().equalsIgnoreCase(defaultText);
	}
	
	public boolean update() {
		this.computeValue();
		return super.update();
	}
	
	private void setDefaultColorOnTextfield() {
		if (textfield != null)
			textfield.setColorBackground(defaultTaskColor);
	}
	
	private void setErrorColorOnTextfield() {
		if (textfield != null)
			textfield.setColorBackground(errorColor); 
	}
	
	public void computeValue() {
		if (this.isValueExpressionEquals(defaultText)) {
			computedValue = defaultValue;
		} else
			try {
				computedValue = this.evaluate();
				setDefaultColorOnTextfield();
			} catch (ScriptException | NumberFormatException e) {
				System.out.println("ScrriptExpression-related error thrown, unhandled update.");
				computedValue = defaultValue;
				setErrorColorOnTextfield();
			}
	}
	
	private float evaluate () throws ScriptException {
		return evaluateAsFloat(this.valueExpression);
	}
	
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
				theEvent.getController().bringToFront();
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
				
				updateValueExpression(content);
			}
		};
	}
}
