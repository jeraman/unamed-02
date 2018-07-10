package frontend.ui;

import controlP5.Group;
import controlP5.Numberbox;
import frontend.Main;
import processing.event.KeyEvent;

/**
 * Editable Numberbox for ControlP5. Provided as an example in ControlP5 (cf. ControlP5editableNumberbox).
 * @author Sojamo
 *
 */
public class NumberboxInput extends AbstractElementUi {
	  
	  String text = "";
	  Numberbox n;
	  boolean active;

	  NumberboxInput(Numberbox theNumberbox) {
	    n = theNumberbox;
	    Main.instance().registerMethod("keyEvent", this );
	  }

	  public void keyEvent(KeyEvent k) {
	    // only process key event if input is active 
	    if (k.getAction()==KeyEvent.PRESS && active) {
	      if (k.getKey()=='\n') { // confirm input with enter
	        submit();
	        return;
	      } else if (k.getKeyCode()==Main.instance().BACKSPACE) { 
	        text = text.isEmpty() ? "":text.substring(0, text.length()-1);
	        //text = ""; // clear all text with backspace
	      } else if (k.getKey()<255) {
	        // check if the input is a valid (decimal) number
	        final String regex = "\\d+([.]\\d{0,2})?";
	        String s = text + k.getKey();
	        if ( java.util.regex.Pattern.matches(regex, s ) ) {
	          text += k.getKey();
	        }
	      }
	      n.getValueLabel().setText(this.text);
	    }
	  }

	  public void setActive(boolean b) {
	    active = b;
	    if (active) {
	      n.getValueLabel().setText("");
	      text = "";
	    }
	  }

	  public void submit() {
	    if (!text.isEmpty()) {
	      n.setValue( Integer.parseInt( text ) );
	      text = "";
	    } else {
	      n.getValueLabel().setText(""+(int)n.getValue());
	    }
	  }

	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void setLastValue() {
	}

	@Override
	public void createUI(String id, String label, int localx, int localy, int w, Group g) {
		
	}
	}
