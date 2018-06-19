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



public class ComputableFloatTextfieldUI extends AbstractElementUi {
	
	private Object valueExpression;
	private float computedValue;
	private String lastComputedValue;
	transient private Textfield textfield;

	public ComputableFloatTextfieldUI() {
		this("-1");
	}

	public void setValueExpression(String newValue) {
		this.valueExpression = new Expression(newValue);
		this.lastComputedValue = "";
	}
	
	public ComputableFloatTextfieldUI(String defaultValue) {
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
	
	public boolean update() {
		this.computeValue();
		return super.update();
	}
	
	public void computeValue() {
		if (this.isValueExpressionEquals(Task.userInputAsDefault)) {
			computedValue = -1;
			textfield.setColorBackground(defaultColor);
		} else
			try {
				computedValue = localEvaluate(this.valueExpression);
				textfield.setColorBackground(defaultColor); 
			} catch (ScriptException | NumberFormatException e) {
				System.out.println("ScrriptExpression-related error thrown, unhandled update.");
				computedValue = -1;
				textfield.setColorBackground(errorColor); 
			}
	}
	
	public float localEvaluate (Object exp) throws ScriptException{
		return evaluateAsFloat(exp);
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
