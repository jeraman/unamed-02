package frontend.tasks.effects;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.ui.ComputableMillisDurationTextfieldUI;
import frontend.ui.ComputableFloatTextfieldUI;
import processing.core.PApplet;
import soundengine.SoundEngine;

public class AdsrFxTask extends AbstractFxTask {
	
	private ComputableFloatTextfieldUI maxAmp; 
	private ComputableMillisDurationTextfieldUI attTime; 
	private ComputableFloatTextfieldUI decTime;
	private ComputableFloatTextfieldUI susLvl;
	private ComputableFloatTextfieldUI relTime;
	private ComputableFloatTextfieldUI befAmp; 
	private ComputableFloatTextfieldUI aftAmp;
	
	
	public AdsrFxTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
		this.maxAmp = new ComputableFloatTextfieldUI(1f, 0, 1);
		this.attTime = new ComputableMillisDurationTextfieldUI(100);
		this.decTime = new ComputableMillisDurationTextfieldUI(500);
//		this.attTime = new ComputableMillisDurationTextfieldUI(0.1f);
//		this.decTime = new ComputableMillisDurationTextfieldUI(0.5f);
		this.susLvl = new ComputableFloatTextfieldUI(0.5f, 0, 1);
//		this.relTime = new ComputableMillisDurationTextfieldUI(1f);
		this.relTime = new ComputableMillisDurationTextfieldUI(1000);
		this.befAmp = new ComputableFloatTextfieldUI(0f, 0, 1);
		this.aftAmp = new ComputableFloatTextfieldUI(0f, 0, 1);

		Main.log.countAdsrFxTask();
	}
	
	public void addToEngine() {
		this.eng.addEffect(this.get_gui_id(), "ADSR", getDefaultParameters());
	}
	
	protected String[] getDefaultParameters(){
		//return new String[] { "1.f", "0.1f", "0.5f", "0.5f", "1.f", "0.f", "0.f"};
		return new String[] { 
				 this.maxAmp.getValue()+"f", 
				 this.attTime.getValue()/1000f+"f", 
				 this.decTime.getValue()/1000f+"f",
				 this.susLvl.getValue()+"f",
				 this.relTime.getValue()/1000f+"f", 
				 this.befAmp.getValue()+"f",
				 this.aftAmp.getValue()+"f"
				 };
	}
	
	private void processMaxAmpChange() {
		if (maxAmp.update())
			this.eng.updateEffect(this.get_gui_id(), "maxAmp : " + maxAmp.getValue());
	}
	
	private void processAttTimeChange() {
		if (attTime.update())
			this.eng.updateEffect(this.get_gui_id(), "attTime : " + attTime.getValue()/1000f);
	}
	private void processDecTimeChange() {
		if (decTime.update())
			this.eng.updateEffect(this.get_gui_id(), "decTime : " + decTime.getValue()/1000f);
	}
	private void processSusLvlChange() {
		if (susLvl.update())
			this.eng.updateEffect(this.get_gui_id(), "susLvl : " + susLvl.getValue());
	}
	private void processRelTimeChange() {
		if (relTime.update())
			this.eng.updateEffect(this.get_gui_id(), "relTime : " + relTime.getValue()/1000f);
	}
	private void processBefAmpChange() {
		if (befAmp.update())
			this.eng.updateEffect(this.get_gui_id(), "befAmp : " + befAmp.getValue());
	}
	private void processAftAmpChange() {
		if (aftAmp.update())
			this.eng.updateEffect(this.get_gui_id(), "aftAmp : " + aftAmp.getValue());
	}
	
	@Override
	protected void processAllParameters() {
		this.processMaxAmpChange();
		this.processAttTimeChange();
		this.processDecTimeChange();
		this.processSusLvlChange();
		this.processRelTimeChange();
		this.processBefAmpChange();
		this.processAftAmpChange();
	}
	
	@Override
	public Task clone_it() {
		AdsrFxTask clone = new AdsrFxTask(this.p, this.cp5, this.name, this.eng);
		clone.maxAmp = this.maxAmp;
		clone.attTime = this.attTime;
		clone.decTime = this.decTime;
		clone.susLvl = this.susLvl;
		clone.relTime = this.relTime;
		clone.befAmp = this.befAmp;
		clone.aftAmp = this.aftAmp;
		return clone;
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "Adsr Effect";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 7.3);
		g.setBackgroundHeight(backgroundheight);

		attTime.createUI(id, "Attack", localx, localy + (0 * localoffset), width, g);
		decTime.createUI(id, "Decay", localx, localy + (1 * localoffset), width, g);
		susLvl.createUI(id, "Sustain", localx, localy + (2 * localoffset), width, g);
		relTime.createUI(id, "Release", localx, localy + (3 * localoffset), width, g);
		maxAmp.createUI(id, "Max. Amplitude", localx, localy + (4 * localoffset), width, g);
		befAmp.createUI(id, "Amplitude before", localx, localy + (5 * localoffset), width, g);
		aftAmp.createUI(id, "Amplitude after", localx, localy + (6 * localoffset), width, g);

		return g;
	}

}
