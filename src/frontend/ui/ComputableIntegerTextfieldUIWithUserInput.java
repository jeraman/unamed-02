package frontend.ui;

public class ComputableIntegerTextfieldUIWithUserInput extends ComputableFloatTextfieldUIWithUserInput {

	public int getValueAsInt() {
		return (int) super.getValue();
	}
}
