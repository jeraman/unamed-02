package effects;

import ddf.minim.ugens.ADSR;

public class AdrsEffect extends ADSR implements Effect {
	float maxAmp; 
	float attTime; 
	float decTime;
	float susLvl;
	float relTime;
	float befAmp; 
	float aftAmp;

	public AdrsEffect(float maxAmp, float attTime, float decTime, float susLvl, float relTime, float befAmp, float aftAmp) {
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
		return new AdrsEffect(maxAmp, attTime, decTime, susLvl, relTime, befAmp, aftAmp);
	}
}
