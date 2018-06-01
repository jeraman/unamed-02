package soundengine.effects;

import ddf.minim.ugens.ADSR;

public class AdsrEffect extends ADSR implements Effect {
	public float maxAmp; 
	public float attTime; 
	public float decTime;
	public float susLvl;
	public float relTime;
	public float befAmp; 
	public float aftAmp;

	public AdsrEffect() {
		this(0, 0, 0, 0, 0, 0, 0);
	}
	
	public AdsrEffect(float maxAmp, float attTime, float decTime, float susLvl, float relTime, float befAmp, float aftAmp) {
		super(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
		this.maxAmp = maxAmp;
		this.attTime = attTime;
		this.decTime = decTime;
		this.susLvl = susLvl;
		this.relTime = relTime;
		this.befAmp = befAmp;
		this.aftAmp = aftAmp;
		
		this.noteOn();
	}
	
	@Override
	public Effect clone() {
		return new AdsrEffect(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}
}
