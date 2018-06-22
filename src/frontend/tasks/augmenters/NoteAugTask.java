package frontend.tasks.augmenters;

import java.util.Arrays;
import java.util.List;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.Status;
import frontend.tasks.Task;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableFloatTextfieldUIWithUserInput;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUIWithUserInput;
import frontend.ui.ScrollableListUI;
import processing.core.PApplet;

public class NoteAugTask extends Task {

	protected static final List<String> list = Arrays.asList("USER INPUT", "PLAY ONCE", "REPEAT");

	private ComputableIntegerTextfieldUI pitch;
	private ComputableIntegerTextfieldUI velocity;
	private ComputableFloatTextfieldUI duration;
	private ScrollableListUI mode;
	private AugmenterMode currentMode;
	private NoteKiller noteKiller;
	
	private boolean wasFirstTime = false;

	public NoteAugTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);

		this.pitch = new ComputableIntegerTextfieldUI(60.0f);
		this.velocity = new ComputableIntegerTextfieldUI(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault,
				-1);
		this.duration = new ComputableFloatTextfieldUI(ComputableFloatTextfieldUIWithUserInput.userInputAsDefault,
				-1.0f);

		this.currentMode = AugmenterMode.USER_INPUT;
		this.mode = new ScrollableListUI(list, this.currentMode.ordinal());
		this.noteKiller = new NoteKiller(this.pitch.getDefaultValueAsInt(), this.velocity.getDefaultValueAsInt(),(int) this.duration.getValue());

		Main.eng.addAugmenter(this.get_gui_id(), "NOTE", getDefaultParameters());
	}

	@Override
	protected String[] getDefaultParameters() {
		return new String[] { "60", "-1", "-1" };
	}

	private boolean isModeUserInput() {
		return (this.currentMode == AugmenterMode.USER_INPUT);
	}

	private boolean isModePlayOnce() {
		return (this.currentMode == AugmenterMode.PLAY_ONCE);
	}

	private boolean isModeRepeat() {
		return (this.currentMode == AugmenterMode.REPEAT);
	}

	private void addOnEngine() {
		Main.eng.addAugmenter(this.get_gui_id(), "NOTE", getDefaultParameters());
	}

	private void removeFromEngine() {
		Main.eng.removeAugmenter(this.get_gui_id());
	}

	private void setModeUserInput() {
		this.currentMode = AugmenterMode.USER_INPUT;
		this.velocity.resetDefaults(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault, -1);
		this.duration.resetDefaults(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault, -1);
		addOnEngine();
	}

	private void setModePlayOnce() {
		this.currentMode = AugmenterMode.PLAY_ONCE;
		this.velocity.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 100);
		this.duration.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 1000);
		removeFromEngine();
	}

	private void setModeRepeat() {
		this.setModePlayOnce();
		this.currentMode = AugmenterMode.REPEAT;
	}

	private void processPitchChange() {
		if (pitch.update()) {
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "pitch : " + pitch.getValueAsInt());
			if (isModePlayOnce() || isModeRepeat())
				this.noteKiller.setPitch(pitch.getValueAsInt());
		}
	}

	private void processVelocityChange() {
		if (velocity.update())
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "velocity : " + velocity.getValueAsInt());
		if (isModePlayOnce() || isModeRepeat())
			this.noteKiller.setVelocity(velocity.getValueAsInt());
	}

	private void processDurationChange() {
		if (duration.update()) {
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "duration : " + duration.getValue());
			if (isModePlayOnce() || isModeRepeat())
				this.noteKiller.setDuration((int) duration.getValue());
		}
	}

	private void processModeChange() {
		if (mode.update()) {
			if (mode.getValue().trim().equalsIgnoreCase("USER INPUT"))
				setModeUserInput();
			if (mode.getValue().trim().equalsIgnoreCase("PLAY ONCE"))
				setModePlayOnce();
			if (mode.getValue().trim().equalsIgnoreCase("REPEAT"))
				setModeRepeat();
		}
	}

	public void run() {
		wasFirstTime = first_time;
		super.run();
		processModes();
	}

	protected void processModes() {
		if (wasFirstTime && isModePlayOnce())
			noteKiller.noteOneAndScheduleKiller();
		else if (isModeRepeat()) 
			noteKiller.noteOneAndScheduleKiller();
	}

	@Override
	protected void processAllParameters() {
		processPitchChange();
		processVelocityChange();
		processDurationChange();
		processModeChange();
	}

	@Override
	public Task clone_it() {
		NoteAugTask clone = new NoteAugTask(this.p, this.cp5, this.name);
		clone.pitch = this.pitch;
		clone.velocity = this.velocity;
		clone.duration = this.duration;
		return clone;
	}

	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Note Augmenter";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 4.5);
		g.setBackgroundHeight(backgroundheight);
		pitch.createUI(id, "pitch", localx, localy + (0 * localoffset), width, g);
		velocity.createUI(id, "velocity", localx, localy + (1 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + (2 * localoffset), width, g);
		mode.createUI(id, "          mode", localx, localy + (3 * localoffset), width, g);

		return g;
	}

	/////////////////////////////////////
	// methods to be carried to super or to be deleted
	public void closeTask() {
		Main.eng.removeAugmenter(this.get_gui_id());
		super.closeTask();
	}

	@Override
	public void reset_gui_fields() {
		// TODO Auto-generated method stub

	}

}
