package frontend.ui;

public class ComputableBooleanTextfieldUi extends TextfieldUi {

	
	private boolean value;
	public static final String classDefaultText = "true";
	
	public ComputableBooleanTextfieldUi(String defaultText) {
		super(defaultText);
		this.value = true;
	}

}
