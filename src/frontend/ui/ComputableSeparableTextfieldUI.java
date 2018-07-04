package frontend.ui;

import javax.script.ScriptException;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import frontend.core.Expression;

public class ComputableSeparableTextfieldUI extends TextfieldUi {

	private Object[] content;
	
	public ComputableSeparableTextfieldUI() {
		super("0");
		this.content = new Object[] { 0 };
	}
	
	public Object[] computeValues() {
		Object[] args = null;

		try {
			args = new Object[content.length];
			
			for (int i = 0; i < args.length; i++) {
					args[i] = this.evaluate(content[i]);
					if (args[i] instanceof Double)
						args[i] = ((Double) args[i]).floatValue();
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		return args;
	}
	
//	private String build_string_from_content() {
//		String param_text = "";
//		for (int i = 0; i < content.length; i++) {
//			if (i != 0)
//				param_text += ", ";
//			param_text += content[i].toString();
//		}
//		return param_text;
//	}

	private void update_content_from_string(String parameters) {
		String[] split = parameters.trim().split(",");
		Object[] result = new Object[split.length];

		for (int i = 0; i < split.length; i++)
			result[i] = new Expression(split[i]);

		content = result;
	}
	
	public CallbackListener callbackPressEnterOrOutside() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				String content = theEvent.getController().getValueLabel().getText();
				
				//if there parameter should be controlled via user input, do nothing
				if (content.trim().equals(defaultText))
					return;
				
				//if user deleted the text, sets user input as default value
				if (content.trim().equals("")) {
					content = defaultText;
					textfield.setText(content);
				}
				update_content_from_string(content);
			}
		};
	}
	

}
