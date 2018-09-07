package frontend.ui;

import frontend.tasks.Task;

public class ComputableFloatTextfieldUIWithUserInput extends ComputableFloatTextfieldUI {
	
	public static final String userInputAsDefault = "(USER INPUT)";
	
	public ComputableFloatTextfieldUIWithUserInput() {
		super(userInputAsDefault, -1);
	}
	
	public ComputableFloatTextfieldUIWithUserInput(String defaultText, float defaultValue) {
		super(defaultText, defaultValue);
	}
	
	public ComputableFloatTextfieldUIWithUserInput(float min, float max) {
		super(-1, min, max);
	}
}
