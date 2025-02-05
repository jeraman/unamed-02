package frontend.tasks.meta;

import oscP5.*;
import netP5.*;
import controlP5.*;
import frontend.Main;
import frontend.ZenStates;
import frontend.core.Expression;
import frontend.core.State;
import frontend.core.Status;
import frontend.tasks.Task;
import frontend.tasks.generators.FMGenTask;
import frontend.ui.ComputableIntegerTextfieldUI;
import frontend.ui.ComputableSeparableTextfieldUI;
import frontend.ui.TextfieldUi;
import frontend.ui.ToggleUi;
import processing.core.PApplet;
import soundengine.SoundEngine;

public class OSCTask extends AbstractMetaTask {

	private TextfieldUi ip;
	private TextfieldUi message;
	private ComputableIntegerTextfieldUI port;
	private ComputableSeparableTextfieldUI parameters;
	private ToggleUi shouldRepeat;

//	private Object[] content;
	
	transient private NetAddress broadcast;
	transient private OscP5 oscP5;

	public OSCTask(PApplet p, ControlP5 cp5, String id, SoundEngine eng) {
		super(p, cp5, id, eng);

		this.ip = new TextfieldUi("localhost");
		this.message = new TextfieldUi("/test/value");
		this.port = new ComputableIntegerTextfieldUI("12000", 12000);
		this.parameters = new ComputableSeparableTextfieldUI();
		this.shouldRepeat = new ToggleUi();
		
		this.broadcast = new NetAddress(this.ip.getValue(), this.port.getValueAsInt());

		this.build(p, cp5, eng);
		
		Main.log.countOscMessages();
	}
	
	public void build(PApplet p, ControlP5 cp5, SoundEngine eng) {
		super.build(p, cp5, eng);
		this.broadcast = new NetAddress(ip.getValue(), port.getValueAsInt());
		this.oscP5 = ZenStates.oscP5;
	}
	
	private void udpateBroadcast() {
		this.broadcast = new NetAddress(ip.getValue(), port.getValueAsInt());
	}
	
	private void processIpChange() {
		if (ip.update())
			udpateBroadcast();
	}
	
	private void processPortChange() {
		if (port.update())
			udpateBroadcast();
	}
	
	@Override
	protected void processAllParameters() {
		processIpChange();
		processPortChange();
		message.update();
		parameters.update();
//		shouldRepeat.update();
	}

	public void run() {
		boolean wasFirstTime = first_time;
		
		super.run();
		
		if (shouldRepeat.getValue() || wasFirstTime)
			sendMessage();
	}
	
	private void sendMessage() {
		OscMessage msg = createMessage();
		oscP5.send(msg, broadcast);
		//if(debug())
		System.out.println("sending OSC message to: " + broadcast.toString() + ". content: " + msg.toString());
	}
	
	private OscMessage createMessage() {
		OscMessage om = new OscMessage(this.message.getValue());
		Object[] args = parameters.computeValues();
		om.add(args);
		return om;
	}
	
	public Group load_gui_elements(State s) {

		this.textlabel = "OSC Message";
		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 5.2);
		g.setBackgroundHeight(backgroundheight);

		ip.createUI(id, "ip", localx, localy + (0 * localoffset), width, g);		
		port.createUI(id, "port", localx, localy + (1 * localoffset), width, g);
		message.createUI(id, "message", localx, localy + (2 * localoffset), width, g);
		parameters.createUI(id, "parameters", localx, localy + (3 * localoffset), width, g);
		shouldRepeat.createUI(id, "once - repeat", localx, localy + (4 * localoffset), width, g);

		return g;
	}
	
	
	@Deprecated
	public OSCTask clone_it() {
		OSCTask clone = new OSCTask(this.p, this.cp5, this.name, this.eng);
		clone.ip = this.ip;
		clone.port = this.port;
		clone.message = this.message;
		clone.parameters = this.parameters;
		clone.shouldRepeat = this.shouldRepeat;
		return clone;
	}
	
}