package frontend.tasks.augmenters;

import java.util.Arrays;
import java.util.List;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.ui.ComputableFloatTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ComputableIntegerTextfieldUIWithUserInput;
import frontend.ui.ComputableMIDITextfieldUI;
import frontend.ui.ComputableMIDITextfieldUIWithUserInput;
import frontend.ui.ScrollableListUI;
import processing.core.PApplet;
import soundengine.SoundEngine;

public class IntervalAugTask extends AbstractAugTask {

	protected static final List<String> list = Arrays.asList("minor second", 
															 "major second", 
															 "minor third", 
															 "major third", 
															 "perfect fourth", 
															 "perfect fifth", 
															 "minor sixth",
															 "major sixth",
															 "minor seventh",
															 "major seventh",
															 "perfect octave");
	
	private ComputableMIDITextfieldUIWithUserInput root;
	private ComputableIntegerTextfieldUI interval;
//	private ScrollableListUI interval;
	
	public IntervalAugTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);

		this.root = new ComputableMIDITextfieldUIWithUserInput();
		this.interval = new ComputableIntegerTextfieldUI(5);
//		this.interval = new ScrollableListUI(list, 5);
		this.musicActioner = new IntervalActioner(this.root.getDefaultValueAsInt(), 5, this.velocity.getDefaultValueAsInt(),(int) this.duration.getValue(), eng);
		
		Main.log.countIntervalAugTask();
	}
	
	@Override
	protected String[] getDefaultParameters() {
		return new String[] { "-1","-1", "-1", "5"};
	}
	
	@Override
	protected void setModeUserInput() {
		this.root.resetDefaults(-1);
		//this.root.resetDefaults(ComputableIntegerTextfieldUIWithUserInput.userInputAsDefault, -1);
		super.setModeUserInput();
	}

	@Override
	protected void setModePlayOnce() {
		this.root.resetDefaults(60);
		//this.root.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 60);
		super.setModePlayOnce();
	}
	
	@Override
	protected void setModeRepeat() {
		this.root.resetDefaults(60);
		//this.root.resetDefaults(ComputableFloatTextfieldUI.classDefaultText, 60);
		super.setModeRepeat();
	}

	@Override
	protected void addOnEngine() {
		this.eng.addAugmenter(this.get_gui_id(), "INTERVAL", getDefaultParameters());
	}
	
	private void processRootChange() {
		if (root.update()) {
			if (isModeUserInput())
				this.eng.updateAugmenter(this.get_gui_id(), "root : " + root.getValueAsInt());
			if (isModePlayOnce() || isModeRepeat())
				((IntervalActioner)this.musicActioner).setRoot(root.getValueAsInt());
		}
	}
	
	private void processIntervalChange() {
		if (interval.update()) {
			if (isModeUserInput())
				this.eng.updateAugmenter(this.get_gui_id(), "type : " + interval.getValueAsInt());
				//this.eng.updateAugmenter(this.get_gui_id(), "type : " + interval.getIndex());
			if (isModePlayOnce() || isModeRepeat())
				((IntervalActioner)this.musicActioner).setInterval(interval.getValueAsInt());
				//((IntervalActioner)this.musicActioner).setInterval(interval.getIndex());
		}
	}
	
	@Override
	protected void processAllParameters() {
		processRootChange();
		processIntervalChange();
		super.processAllParameters();
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Interval Augmenter";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 5.5);
		g.setBackgroundHeight(backgroundheight);
		int miniOffsetDueToScrollList = 5;
		
		mode.createUI(id, "          mode", localx, localy + (0 * localoffset), width, g);
		root.createUI(id, "root", localx, localy + miniOffsetDueToScrollList + (1 * localoffset), width, g);
		interval.createUI(id, "interval", localx, localy + miniOffsetDueToScrollList + (2 * localoffset), width, g);
		velocity.createUI(id, "velocity", localx, localy + miniOffsetDueToScrollList + (3 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + miniOffsetDueToScrollList + (4 * localoffset), width, g);

		return g;
	}

	@Override
	public Task clone_it() {
		IntervalAugTask clone = new IntervalAugTask(this.p, this.cp5, this.name, this.eng);
		clone.root = this.root;
		clone.interval = this.interval;
		clone.velocity = this.velocity;
		clone.duration = this.duration;
		clone.mode = this.mode;
		return clone;
	}

}
