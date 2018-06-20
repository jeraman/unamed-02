package frontend.ui;

import frontend.tasks.Task;

public class ComputableFloatTextfieldUIWithUserInput extends ComputableFloatTextfieldUI {
	
	public static final String userInputAsDefault = "(USER INPUT)";
	
	public ComputableFloatTextfieldUIWithUserInput() {
		super(userInputAsDefault, -1);
	}
	
	
}
