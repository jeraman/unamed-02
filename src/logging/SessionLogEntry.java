package logging;

import java.sql.Timestamp;

import com.google.gson.*;

import frontend.ZenStates;
import soundengine.util.Util;

/**
 * Logs usage sessions in a JSON file, where each entry is an individual session.
 * @author jeraman.info
 *
 */
class SessionLogEntry {

	// session metrics
	private String userID;
	private Timestamp begin;
	private Timestamp end;
	private String closingInfo;
	private StackTraceElement[] detailedClosingInfo;

	protected SessionLogEntry(String userID) {
		this.userID = userID;
		this.begin = new Timestamp(System.currentTimeMillis());
	}

	protected void close() {
		this.end = new Timestamp(System.currentTimeMillis());
		this.programmingTime = ((this.end.getTime() - this.begin.getTime())/1000) - this.playingTime;
		this.createdTransition -= (this.createdState + this.createdSMTasks);
		this.removedTransition -= this.removedState;
		this.closingInfo = "Ordinary closing";
		this.detailedClosingInfo = null;// "No errors occurred";
	}

	protected void close(Exception e) {
		this.close();
		this.closingInfo = e.toString();
		this.detailedClosingInfo = e.getStackTrace();
	}

	// time metrics
	private float playingTime = 0;
	private float programmingTime = 0;	

	transient private int beginPlayingTime;
	transient private boolean isCountingPayingTime = false;
	
	public void updatePlayingStatus() {
		if (ZenStates.thereIsKeyDown() && !isCountingPayingTime)
			this.startCountingPlayingTime();
		if (ZenStates.thereIsKeyReleased() && isCountingPayingTime)
			this.stopCountingPlayingTime();
	}
	
	private void startCountingPlayingTime() {
		this.beginPlayingTime = Util.millis();
		this.isCountingPayingTime = true;
	}
	
	private void stopCountingPlayingTime() {
		this.addPlayingTime((Util.millis() - this.beginPlayingTime)/1000f);
		this.isCountingPayingTime = false;
	}
	
	public void addPlayingTime(float newValue) {
		this.playingTime += newValue;
	}

//	public void addProgrammingTime(int newValue) {
//		this.programmingTime += newValue;
//	}

	// basics (explorability metrics)
	private int createdState = 0;
	private int createdTask = 0;
	private int createdTransition = 0;
	private int removedState = 0;
	private int removedTask = 0;
	private int removedTransition = 0;
	private int changedBegin = 0;

	public void countCreatedState() {
		this.createdState++;
	}

	private void countCreatedTask() {
		this.createdTask++;
	}

	public void countCreatedTransition() {
		this.createdTransition++;
	}

	public void countRemovedState() {
		this.removedState++;
	}

	public void countRemovedTask() {
		this.removedTask++;
	}

	public void countRemovedTransition() {
		this.removedTransition++;
	}

	public void countChangedBegin() {
		this.changedBegin++;
	}

	// blackboard (explorability metrics)
	private int bbVarInTaskParameters = 0;
	private int bbVarInTransitions = 0;
	private int createdbbVars = 0;
//	private int pulledUserPushedBBVar = 0;

	public void countBbVarInTaskParameters() {
		this.bbVarInTaskParameters++;
	}

	public void countBbVarInTransitions() {
		this.bbVarInTransitions++;
	}

	private void countCreatedbbVars() {
		this.createdbbVars++;
	}

//	public void countPulledUserPushedBBVar() {
//		this.pulledUserPushedBBVar++;
//	}

	// tasks (explorability metrics)
	private int oscGenTask = 0;
	private int fMGenTask = 0;
	private int samplerGenTask = 0;
	private int delayFxTask = 0;
	private int flangerFxTask = 0;
	private int adsrFxTask = 0;
	private int bitchrushFxTask = 0;
	private int filterFxTask = 0;
	private int noteAugTask = 0;
	private int intervalAugTask = 0;
	private int chordAugTask = 0;
	private int randomBBTask = 0;
	private int oscBBTask = 0;
	private int rampBBTask = 0;
	private int defaultBBTask = 0;

	public void countOscGenTask() {
		this.oscGenTask++;
		this.countCreatedTask();
	}

	public void countFMGenTask() {
		this.fMGenTask++;
		this.countCreatedTask();
	}

	public void countSamplerGenTask() {
		this.samplerGenTask++;
		this.countCreatedTask();
	}

	public void countDelayFxTask() {
		this.delayFxTask++;
		this.countCreatedTask();
	}

	public void countFlangerFxTask() {
		this.flangerFxTask++;
		this.countCreatedTask();
	}

	public void countAdsrFxTask() {
		this.adsrFxTask++;
		this.countCreatedTask();
	}

	public void countBitchrushFxTask() {
		this.bitchrushFxTask++;
		this.countCreatedTask();
	}

	public void countFilterFxTask() {
		this.filterFxTask++;
		this.countCreatedTask();
	}

	public void countNoteAugTask() {
		this.noteAugTask++;
		this.countCreatedTask();
	}

	public void countIntervalAugTask() {
		this.intervalAugTask++;
		this.countCreatedTask();
	}

	public void countChordAugTask() {
		this.chordAugTask++;
		this.countCreatedTask();
	}

	public void countRandomBBTask() {
		this.randomBBTask++;
		this.countCreatedTask();
		this.countCreatedbbVars();
	}

	public void countOscBBTask() {
		this.oscBBTask++;
		this.countCreatedTask();
		this.countCreatedbbVars();
	}

	public void countRampBBTask() {
		this.rampBBTask++;
		this.countCreatedTask();
		this.countCreatedbbVars();
	}

	public void countDefaultBBTask() {
		this.defaultBBTask++;
		this.countCreatedTask();
		this.countCreatedbbVars();
	}

	// meta (explorability metrics)
	private int createdSMTasks = 0;
	private int smZoomIn = 0;
	private int smZoomOut = 0;
	private int loadedExistingSM = 0;
	private int jsScript = 0;
	private int oscMessages = 0;

	public void countCreatedSM() {
		this.createdSMTasks++;
	}

	public void countSmZoomIn() {
		this.smZoomIn++;
	}

	public void countSmZoomOut() {
		this.smZoomOut++;
	}

	public void countLoadedExistingSM() {
		this.loadedExistingSM++;
	}

	public void countJsScript() {
		this.jsScript++;
	}

	public void countOscMessages() {
		this.oscMessages++;
	}

	// transitions (explorability metrics)
	private int oneToManyTransitions = 0;
	private int changedPriority = 0;

	public void countOneToManyTransitions() {
		this.oneToManyTransitions++;
	}

	public void countChangedPriority() {
		this.changedPriority++;
	}

	// tempo (explorability metrics)
	private int changedSignature = 0;
	private int changedBPM = 0;
	private int changedMetro = 0;

	public void countChangedSignature() {
		this.changedSignature++;
	}

	public void countChangedBPM() {
		this.changedBPM++;
	}

	public void countChangedMetro() {
		this.changedMetro++;
	}

	// button prossed (explorability metrics)
	private int stopBtn = 0;
	private int playBtn = 0;
	private int loadBtn = 0;
	private int saveBtn = 0;
	
	public void countStopBtn() {
		this.stopBtn++;
	}

	public void countPlayBtn() {
		this.playBtn++;
	}

	public void countLoadBtn() {
		this.loadBtn++;
	}
	
	public void countSaveBtn() {
		this.saveBtn++;
	}

}
