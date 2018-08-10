package frontend.tasks.meta;



import controlP5.*;
import frontend.Main;
import frontend.ZenStates;
import frontend.core.Expression;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.tasks.generators.OscillatorGenTask;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ToggleUi;
import processing.core.PApplet;
import soundengine.SoundEngine;


public class DMXTask extends AbstractMetaTask {
	private ComputableIntegerTextfieldUI channel;
	private ComputableIntegerTextfieldUI intensity;
	private ComputableIntegerTextfieldUI rate;
	private ComputableIntegerTextfieldUI duration;
	private ToggleUi shouldRepeat;

	public DMXTask(PApplet p, ControlP5 cp5, String id, SoundEngine eng) {
		super(p, cp5, id, eng);

		this.channel = new ComputableIntegerTextfieldUI(0);
		this.intensity = new ComputableIntegerTextfieldUI(255);
		this.duration = new ComputableIntegerTextfieldUI(255);
		this.rate = new ComputableIntegerTextfieldUI(255);
		this.shouldRepeat = new ToggleUi();

	}
	
	protected boolean debug() {
		return ZenStates.debug;
	}

	private void processChannelChange() {
		if (channel.update() && debug())
			System.out.println(this.get_gui_id() + " changes channel " + channel.getValueAsInt());
	}
	private void processIntensityChange() {
		if (intensity.update() && debug())
			System.out.println(this.get_gui_id() + " changes intensity " + intensity.getValueAsInt());
	}
	private void processRateChange() {
		if (rate.update() && debug())
			System.out.println(this.get_gui_id() + " changes rate " + rate.getValueAsInt());
	}
	private void processDurationChange() {
		if (duration.update() && debug())
			System.out.println(this.get_gui_id() + " changes duration " + duration.getValueAsInt());
	}

	protected void processAllParameters() {
		this.processChannelChange();
		this.processIntensityChange();
		this.processRateChange();
		this.processDurationChange();
	}
	
	public void run() {
		boolean wasFirstTime = first_time;
		super.run();
		if (shouldRepeat.getValue() || wasFirstTime)
			sendDmxMessage();
	}

	private void sendDmxMessage() {
		System.out.println("stub sendDmxMessage method");
	}

	@Override
	public Task clone_it() {
		DMXTask clone = new DMXTask(this.p, this.cp5, this.name, this.eng);
		clone.channel = this.channel;
		clone.intensity = this.intensity;
		clone.rate = this.rate;
		clone.duration = this.duration;
		return clone;
	}

	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "DMX Light";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 5.2);
		g.setBackgroundHeight(backgroundheight);
		
		channel.createUI(id, "channel", localx, localy + (0 * localoffset), width, g);
		intensity.createUI(id, "intensity", localx, localy + (1 * localoffset), width, g);
		rate.createUI(id, "rate", localx, localy + (2 * localoffset), width, g);
		duration.createUI(id, "duration", localx, localy + (3 * localoffset), width, g);
		shouldRepeat.createUI(id, "once - repeat", localx, localy + (4 * localoffset), width, g);
		
		return g;
	}
}