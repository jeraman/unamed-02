package frontend.ui;

import javax.script.ScriptException;

import frontend.tasks.Task;

public class ComputableIntegerTextFieldUI extends ComputableFloatTextfieldUI {
	
	public int getValueAsInt() {
		return (int) super.getValue();
	}
}
