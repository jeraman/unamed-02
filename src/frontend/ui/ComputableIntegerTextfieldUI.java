package frontend.ui;

import javax.script.ScriptException;

import frontend.tasks.Task;

public class ComputableIntegerTextfieldUI extends ComputableFloatTextfieldUI {
	
	public ComputableIntegerTextfieldUI (float defaultValue) {
		super(classDefaultText, defaultValue);
	}
	
	public ComputableIntegerTextfieldUI (float defaultValue, int min, int max) {
		super(defaultValue, min, max);
	}
	
	public ComputableIntegerTextfieldUI(String defaultText, float defaultValue) {
		super(defaultText, defaultValue);
	}

	public int getValueAsInt() {
		return (int) super.getValue();
	}
	
	public int getDefaultValueAsInt() {
		return (int) super.getDefaultValue();
	}
}
