package frontend.tasks.augmenters;

import java.util.Arrays;
import java.util.List;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.State;
import frontend.tasks.Task;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUIWithUserInput;
import frontend.ui.ScrollableListUI;
import processing.core.PApplet;

public class ChordAugTask extends AbstractAugTask {
	
	protected static final List<String> list = Arrays.asList("maj", "maj6", "maj7", "min", "min6", "min7", "aug", "dim");
	
	private ComputableIntegerTextfieldUI root;
	private ScrollableListUI chordType;
	
	public ChordAugTask(PApplet p, ControlP5 cp5, String taskname) {
		super(p, cp5, taskname);

		this.root = new ComputableIntegerTextfieldUI(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault,-1);
		this.chordType = new ScrollableListUI(list, 0);
		
//		this.musicActioner = new ChordActioner(this.root.getDefaultValueAsInt(), 5, this.velocity.getDefaultValueAsInt(),(int) this.duration.getValue());

		addOnEngine();
	}
	
	@Override
	protected String[] getDefaultParameters() {
		return new String[] { "-1", "-1", "-1", "maj"};
	}
	
	@Override
	protected void setModeUserInput() {
		this.root.resetDefaults(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault, -1);
		super.setModeUserInput();
	}

	@Override
	protected void setModePlayOnce() {
		this.root.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 60);
		super.setModePlayOnce();
	}
	
	@Override
	protected void setModeRepeat() {
		this.root.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 60);
		super.setModeRepeat();
	}

	@Override
	protected void addOnEngine() {
		Main.eng.addAugmenter(this.get_gui_id(), "CHORD", getDefaultParameters());
	}
	
	private void processRootChange() {
		if (root.update()) {
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "root : " + root.getValueAsInt());
			//if (isModePlayOnce() || isModeRepeat())
			//	((ChordActioner)this.musicActioner).setRoot(root.getValueAsInt());
		}
	}
	
	private void processChordTypeChange() {
		if (chordType.update()) {
			if (isModeUserInput())
				Main.eng.updateAugmenter(this.get_gui_id(), "type : " + chordType.getValue());
			//if (isModePlayOnce() || isModeRepeat())
			//	((ChordActioner)this.musicActioner).setChord(chordType.getValue());
		}
	}
	

	@Override
	protected void processAllParameters() {
		processRootChange();
		processChordTypeChange();
		super.processAllParameters();
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Chord Augmenter";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 5.5);
		g.setBackgroundHeight(backgroundheight);
		int miniOffsetDueToScrollList = 5;
		root.createUI(id, "root", localx, localy + (0 * localoffset), width, g);
		velocity.createUI(id, "velocity", localx, localy + miniOffsetDueToScrollList + (2 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + miniOffsetDueToScrollList + (3 * localoffset), width, g);
		mode.createUI(id, "          mode", localx, localy + miniOffsetDueToScrollList + (4 * localoffset), width, g);
		chordType.createUI(id, "type", localx, localy + (1 * localoffset), width, g);

		return g;
	}

	@Override
	public Task clone_it() {
		ChordAugTask clone = new ChordAugTask(this.p, this.cp5, this.name);
		clone.root = this.root;
		clone.chordType = this.chordType;
		clone.velocity = this.velocity;
		clone.duration = this.duration;
		clone.mode = this.mode;
		return clone;
	}

}
