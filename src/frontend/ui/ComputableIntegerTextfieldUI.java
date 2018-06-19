package frontend.ui;

import javax.script.ScriptException;

import frontend.tasks.Task;

public class ComputableIntegerTextfieldUI extends ComputableFloatTextfieldUI {
	
	public int getValueAsInt() {
		return (int) super.getValue();
	}
}
