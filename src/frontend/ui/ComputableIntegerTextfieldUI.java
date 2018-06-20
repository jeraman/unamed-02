package frontend.ui;

import javax.script.ScriptException;

import frontend.tasks.Task;

public class ComputableIntegerTextfieldUI extends ComputableFloatTextfieldUI {
	
	public ComputableIntegerTextfieldUI(String defaultText, float defaultValue) {
		super(defaultText, defaultValue);
	}

	public int getValueAsInt() {
		return (int) super.getValue();
	}
}
