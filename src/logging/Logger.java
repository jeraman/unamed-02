package logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import frontend.Main;
import processing.core.PApplet;

public class Logger {

	private Vector<SessionLogEntry> allLogs;

	transient private String userID;
	transient private SessionLogEntry currentLog;
	transient private ScreenShooter screenshooter;
	transient private File file;
	transient private static Gson gson = new Gson();
	transient private static final Charset encoding = StandardCharsets.UTF_8;

	public Logger(PApplet p, String userID) {
		this.userID = userID;
		this.screenshooter = new ScreenShooter(p, userID);
		this.currentLog = new SessionLogEntry(userID);
		this.loadOldLogs();
	}
	
	public void update() {
		this.currentLog.updatePlayingStatus();
		this.screenshooter.updateCountdown();
	}
	
	

	private void loadOldLogs() {
		String filename = "./data/logs/" + userID + ".json";
		String json = readJson(filename);

		if (json == "")
			allLogs = new Vector<SessionLogEntry>();
		else
			allLogs = gson.fromJson(json, getOldLogType());
	}

	private Type getOldLogType() {
		return new TypeToken<Vector<SessionLogEntry>>() {
		}.getType();
	}

	private String readJson(String filename) {
		String result = "";
		this.file = new File(filename);
		if (this.file.exists())
			result = readTextFromFile(filename);
		return result;
	}

	private String readTextFromFile(String path) {
		byte[] encoded = null;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(encoded, encoding);
	}

	public void close() {
		currentLog.close();
		saveCurrentLogWithOldLogs();
	}

	public void close(Exception e) {
		currentLog.close(e);
		saveCurrentLogWithOldLogs();
	}

	private void saveCurrentLogWithOldLogs() {
		allLogs.add(currentLog);
		writeJsonToFile();
	}

	private void writeJsonToFile() {
		try {
			PrintWriter printer;
			printer = new PrintWriter(file);
			printer.println(toJson());
			printer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	private String toJson() {
		String json = gson.toJson(allLogs);
		System.out.println(json);
		return json;
	}

	
	public void addPlayingTime(int newValue) {
		currentLog.addPlayingTime(newValue);
	}
	
//	public void addProgrammingTime(int newValue) {
//		currentLog.addProgrammingTime(newValue);
//	}
	
	public void countCreatedState() {
		currentLog.countCreatedState();
	}
	
	public void countCreatedTransition() {
		currentLog.countCreatedTransition();
	}
	
	public void countRemovedState() {
		currentLog.countRemovedState();
	}
	
	public void countRemovedTask() {
		currentLog.countRemovedTask();
	}
	
	public void countRemovedTransition() {
		currentLog.countRemovedTransition();
	}
	
	public void countChangedBegin() {
		currentLog.countChangedBegin();
	}
	
	public void countBbVarInTaskParameters() {
		currentLog.countBbVarInTaskParameters();
	}

	public void countBbVarInTransitions() {
		currentLog.countBbVarInTransitions();
	}

//	public void countPulledUserPushedBBVar() {
//		currentLog.countPulledUserPushedBBVar();
//	}

	public void countOscGenTask() {
		currentLog.countOscGenTask();
	}

	public void countFMGenTask() {
		currentLog.countFMGenTask();
	}

	public void countSamplerGenTask() {
		currentLog.countSamplerGenTask();
	}

	public void countDelayFxTask() {
		currentLog.countDelayFxTask();
	}

	public void countFlangerFxTask() {
		currentLog.countFlangerFxTask();
	}

	public void countAdsrFxTask() {
		currentLog.countAdsrFxTask();
	}

	public void countBitchrushFxTask() {
		currentLog.countBitchrushFxTask();
	}

	public void countFilterFxTask() {
		currentLog.countFilterFxTask();
	}

	public void countNoteAugTask() {
		currentLog.countNoteAugTask();
	}

	public void countIntervalAugTask() {
		currentLog.countIntervalAugTask();
	}

	public void countChordAugTask() {
		currentLog.countChordAugTask();
	}

	public void countRandomBBTask() {
		currentLog.countRandomBBTask();
	}

	public void countOscBBTask() {
		currentLog.countOscBBTask();
	}

	public void countRampBBTask() {
		currentLog.countRampBBTask();
	}

	public void countDefaultBBTask() {
		currentLog.countDefaultBBTask();
	}
	
	public void countCreatedSM() {
		currentLog.countCreatedSM();
	}

	public void countSmZoomIn() {
		currentLog.countSmZoomIn();
	}

	public void countSmZoomOut() {
		currentLog.countSmZoomOut();
	}

	public void countLoadedExistingSM() {
		currentLog.countLoadedExistingSM();
	}

	public void countJsScript() {
		currentLog.countJsScript();
	}

	public void countOscMessages() {
		currentLog.countOscMessages();
	}
	
	public void countOneToManyTransitions() {
		currentLog.countOneToManyTransitions();
	}

	public void countChangedPriority() {
		currentLog.countChangedPriority();
	}
	
	public void countChangedSignature() {
		currentLog.countChangedSignature();
	}

	public void countChangedBPM() {
		currentLog.countChangedBPM();
	}

	public void countChangedMetro() {
		currentLog.countChangedMetro();
	}
	
	public void countStopBtn() {
		currentLog.countStopBtn();
	}

	public void countPlayBtn() {
		currentLog.countPlayBtn();
	}

	public void countLoadBtn() {
		currentLog.countLoadBtn();
	}
	
	public void countSaveBtn() {
		currentLog.countSaveBtn();
	}
}
