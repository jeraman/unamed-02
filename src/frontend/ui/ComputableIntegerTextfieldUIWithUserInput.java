package frontend.ui;

public class ComputableIntegerTextfieldUIWithUserInput extends ComputableFloatTextfieldUIWithUserInput {

	public ComputableIntegerTextfieldUIWithUserInput(String defaultText) {
		super(defaultText, -1);
	}
	
	public int getValueAsInt() {
		return (int) super.getValue();
	}
	
	public int getDefaultValueAsInt() {
		return (int) super.getDefaultValue();
	}
}
