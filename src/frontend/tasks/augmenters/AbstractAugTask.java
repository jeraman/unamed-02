package frontend.tasks.augmenters;

import java.util.Arrays;
import java.util.List;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableFloatTextfieldUIWithUserInput;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUIWithUserInput;
import frontend.ui.ScrollableListUI;
import processing.core.PApplet;

/**
 * Abstract class implementing all functionalities to be used by AugmenterTasks (ie. Note, Interval, and Chord)
 * @author jeronimo
 *
 */
public abstract class AbstractAugTask extends Task {
	
	protected static final List<String> list = Arrays.asList("USER INPUT", "PLAY ONCE", "REPEAT");

	protected ComputableIntegerTextfieldUI velocity;
	protected ComputableFloatTextfieldUI duration;
	protected ScrollableListUI mode;
	private AugmenterMode currentMode;
	protected AbstractMusicActioner musicActioner;
	
	private boolean wasFirstTime = false;

	public AbstractAugTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);

		this.velocity = new ComputableIntegerTextfieldUI(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault,-1);
		this.duration = new ComputableFloatTextfieldUI(ComputableFloatTextfieldUIWithUserInput.userInputAsDefault,-1.0f);

		this.currentMode = AugmenterMode.USER_INPUT;
		this.mode = new ScrollableListUI(list, this.currentMode.ordinal());
	}

	protected abstract String[] getDefaultParameters();

	protected abstract void addOnEngine();
	
	public abstract Task clone_it();
	
	protected void removeFromEngine() {
		Main.eng.removeAugmenter(this.get_gui_id());
	}
	
	protected boolean isModeUserInput() {
		return (this.currentMode == AugmenterMode.USER_INPUT);
	}

	protected boolean isModePlayOnce() {
		return (this.currentMode == AugmenterMode.PLAY_ONCE);
	}

	protected boolean isModeRepeat() {
		return (this.currentMode == AugmenterMode.REPEAT);
	}
	
	protected void resetMusicActioner() {
		musicActioner.setVelocity(this.velocity.getDefaultValueAsInt());
		musicActioner.setDuration((int)this.duration.getDefaultValue());
	}

	protected void setModeUserInput() {
		this.currentMode = AugmenterMode.USER_INPUT;
		this.velocity.resetDefaults(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault, -1);
		this.duration.resetDefaults(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault, -1);
		resetMusicActioner();
		addOnEngine();
	}

	protected void setModePlayOnce() {
		this.currentMode = AugmenterMode.PLAY_ONCE;
		this.velocity.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 100);
		this.duration.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 1000);
		resetMusicActioner();
		removeFromEngine();
	}

	protected void setModeRepeat() {
		this.setModePlayOnce();
		this.currentMode = AugmenterMode.REPEAT;
	}

	protected void processVelocityChange() {
		if (velocity.update())
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "velocity : " + velocity.getValueAsInt());
		if (isModePlayOnce() || isModeRepeat())
			this.musicActioner.setVelocity(velocity.getValueAsInt());
	}

	protected void processDurationChange() {
		if (duration.update()) {
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "duration : " + duration.getValue());
			if (isModePlayOnce() || isModeRepeat())
				this.musicActioner.setDuration((int) duration.getValue());
		}
	}

	protected void processModeChange() {
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

	public void start() {
		this.addOnEngine();
	}
	
	public void stop() {
		this.removeFromEngine();
	}
	
	protected void processModes() {
		if (wasFirstTime && isModePlayOnce())
			musicActioner.noteOneAndScheduleKiller();
		else if (isModeRepeat()) 
			musicActioner.noteOneAndScheduleKiller();
	}

	@Override
	protected void processAllParameters() {
		processVelocityChange();
		processDurationChange();
		processModeChange();
	}

//	/////////////////////////////////
//	// UI config
//	public Group load_gui_elements(String id, Group g, int index) {
//		int width = g.getWidth() - (localx * 2);
//
//		this.backgroundheight = (int) (localoffset * 4.5);
//		g.setBackgroundHeight(backgroundheight);
//		velocity.createUI(id, "velocity", localx, localy + (index * localoffset), width, g);
//		duration.createUI(id, "duration", localx, localy + (index * localoffset), width, g);
//		mode.createUI(id, "          mode", localx, localy + (index * localoffset), width, g);
//
//		return g;
//	}

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
