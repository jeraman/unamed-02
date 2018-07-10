package frontend.ui;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.Numberbox;
import controlP5.Textlabel;
import controlP5.Toggle;
import frontend.Main;
import soundengine.time.TimeManager;


public class TempoWindowUi extends AbstractElementUi {

	private transient Group g;
	private transient Textlabel timerLabel;
	private transient Textlabel timerContent;
	private transient Textlabel barLabel;
	private transient Textlabel barContent;
	private transient Textlabel signatureLabel;
	private transient Numberbox signatureBar;
	private transient Numberbox signatureBeat;
	private transient Textlabel bpmLabel;
	private transient Numberbox bpmContent;
	private transient Textlabel metroLabel;
	private transient Toggle metroContent;
	
	private TimeManager parent;

	private int xpos = 20, xOffset = 30, xsize = 30;
	private int ypos = 15, yOffset = 20, ysize = 15;

	private int xSpecialOffset = 10, ySpecialOffset = 15;
	
	private int bpm;
	private int beats;
	private int noteValue;
	
	public TempoWindowUi(TimeManager tm) {
		this.bpm = tm.getBpm();
		this.beats = tm.getGlobalBeat();
		this.noteValue = tm.getGlobalNoteValue();
		this.parent = tm;
	}
	
	public void updateElapsedTime() {
		this.setTime(this.parent.getElapsedTime()+"");
	}
	
	public void updateMusicalTime() {
		this.setTimeInBars(this.parent.getMusicalTime());
	}
	
	public void setTime(String newTime) {
		this.timerContent.setText(newTime);
	}
	
	public void setTimeInBars(String newTime) {
		this.barContent.setText(newTime);
	}

	public void setPosition(int x, int y) {
		if (g != null)
			g.setPosition(x, y);
	}
	
	public void createUi() {
		int width = Main.instance().board().getWidth();
		int x = Main.instance().board().getX()-30;
		int y = Main.instance().board().getY() + Main.instance().board().getHeight();
		
		g = cp5.addGroup("tempo")
				.setPosition(250, 100).setWidth(width)
				.activateEvent(true)
				.setHeight(20)
				.setBackgroundColor(blackboardBackgroundColor)
				.setColorBackground(blackboardHeaderColor)
				.setBackgroundHeight(115)
				.setLabel("TEMPO");

		g.getCaptionLabel().align(cp5.CENTER, cp5.CENTER);
		g.getCaptionLabel().setColor(blackboardHeaderTextColor);

		timerLabel = cp5.addTextlabel("tempo/timer/label").setText("Time: ").setPosition(xpos, ypos).setGroup(g);
		timerContent = cp5.addTextlabel("tempo/timer/counter").setText("00 : 00 : 000")
				.setPosition(xpos + xOffset, ypos).setGroup(g);
		barLabel = cp5.addTextlabel("tempo/bars/label").setText("Bar: ")
				.setPosition(xpos, 2 * ypos + 2)
				.setGroup(g);
		barContent = cp5.addTextlabel("tempo/bars/counter").setText("00 : 00 : 00")
				.setPosition(xpos + xOffset, 2 * ypos + 2)
				.setGroup(g);
		signatureLabel = cp5.addTextlabel("tempo/signature/label").setText("Signature:                          / ")
				.setPosition(xpos, (2 * ypos) + yOffset).setGroup(g);
		signatureBar = cp5.addNumberbox("tempo/signature/bar")
				.setSize(xsize, ysize).setLabel("")
				.setDecimalPrecision(0)
				.setPosition(xpos + (2 * xOffset), (2 * ypos) + yOffset - 2)
				.setRange(0, 1000)
				.setValue(this.beats)
				.setRange(1, 16)
				.setColorBackground(blackboardHeaderColor)
				.setColorForeground(connectionBackgroundColor)
				.setColorValueLabel(blackboardHeaderTextColor)
				.onChange(changeBarCallback())
				.setGroup(g);
		signatureBeat = cp5.addNumberbox("tempo/signature/beat").setSize(xsize, ysize).setLabel("")
				.setDecimalPrecision(0)
				.setPosition(xpos + (3 * xOffset) + (2 * xSpecialOffset), (2 * ypos) + yOffset - 2).setValue(this.noteValue)
				.setRange(1, 16).onChange(changeBeatCallback())
				.setColorBackground(blackboardHeaderColor)
				.setColorForeground(connectionBackgroundColor)
				.setColorValueLabel(blackboardHeaderTextColor)
				.setGroup(g);
		bpmLabel = cp5.addTextlabel("tempo/bpm/label").setText("BPM: ").setPosition(xpos, (2 * ypos) + (2 * yOffset))
				.setGroup(g);
		bpmContent = cp5.addNumberbox("tempo/bpm/counter").setSize(3 * xsize, ysize).setLabel("").setDecimalPrecision(0)
				.setRange(0, 1000).setValue(0)
				.setPosition(xpos + xOffset + xSpecialOffset, (2 * ypos) + (2 * yOffset) - 2)
				.setValue(this.bpm)
				.setColorBackground(blackboardHeaderColor)
				.setColorForeground(connectionBackgroundColor)
				.setColorValueLabel(blackboardHeaderTextColor)
				.onChange(changeBPMCallback())
				.setGroup(g);
		metroLabel = cp5.addTextlabel("tempo/metro/label").setText("Metronome: ")
				.setPosition(xpos, ypos + (4 * yOffset) - 4).setGroup(g);
		metroContent = cp5.addToggle("toggleValue")
				.setPosition(xpos + (2 * xOffset), ypos + (4 * yOffset) - 6)
				.setSize(xsize, ysize).setGroup(g)
				.setColorBackground(connectionBackgroundColor)
				.setColorForeground(blackboardForegroundColor)
				.setColorActive(blackboardHeaderColor)
				.onChange(changeMetronomeCallback()).setLabel("");

		this.setPosition(x, y);
		
		makeEditable(signatureBar);
		makeEditable(signatureBeat);
		makeEditable(bpmContent);
	}

	private void makeEditable(Numberbox n) {
		// allows the user to click a numberbox and type in a number which is
		// confirmed with RETURN
		final NumberboxInput nin = new NumberboxInput(n); // custom input
															// handler for the
															// numberbox

		// control the active-status of the input handler when releasing the
		// mouse button inside
		// the numberbox. deactivate input handler when mouse leaves.
		n.onClick(new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				nin.setActive(true);
			}
		}).onLeave(new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				nin.setActive(false);
				nin.submit();
			}
		});
	}

	private CallbackListener changeBPMCallback() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				String content = theEvent.getController().getValueLabel().getText();
				System.out.println("new BPM: " + content);
				if (!content.equals("")) {
					bpm = Integer.parseInt(content);
					parent.setBpm(bpm);
				}
			}
		};
	}

	private CallbackListener changeBeatCallback() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				String content = theEvent.getController().getValueLabel().getText();
				System.out.println("new note value: " + content);
				if (!content.equals("")) {
					noteValue = Integer.parseInt(content);
					parent.setGlobalNoteValue(noteValue);
				}
			}
		};
	}

	private CallbackListener changeBarCallback() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				String content = theEvent.getController().getValueLabel().getText();
				System.out.println("new beat: " + content);
				if (!content.equals("")) {
					beats = Integer.parseInt(content);
					parent.setGlobalBeat(beats);
				}
			}
		};
	}

	private CallbackListener changeMetronomeCallback() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				int temp = (int) theEvent.getController().getValue();
				System.out.println("metronome is: " + temp);
				
				if (temp == 1)
					parent.enableSound();
				else
					parent.disableSound();
			}
		};
	}

	@Deprecated
	@Override
	public boolean hasChanged() {
		return false;
	}

	@Deprecated
	@Override
	public void setLastValue() {
	}

	@Deprecated
	@Override
	public void createUI(String id, String label, int localx, int localy, int w, Group g) {
		this.createUi();
	}
}
