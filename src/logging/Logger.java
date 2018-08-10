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

public class Logger {

	private Vector<SessionLogEntry> allLogs;
	// private SessionLogEntry[] oldLogs;

	transient private String userID;
	transient private SessionLogEntry currentLog;
	transient private File file;
	transient private static Gson gson = new Gson();

	transient private static final Charset encoding = StandardCharsets.UTF_8;

	protected Logger(String userID) {
		this.userID = userID;
		this.currentLog = new SessionLogEntry(userID);
		this.loadOldLogs();
	}

	private void loadOldLogs() {
		String filename = "./data/logs/" + userID + ".json";
		String json = readJson(filename);

		if (json == "")
			allLogs = new Vector<SessionLogEntry>();
		// oldLogs = new SessionLogEntry[1];
		else
			allLogs = gson.fromJson(json, getOldLogType());
		// oldLogs = gson.fromJson(json, SessionLogEntry[].class);
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
		// copyingArray();
		allLogs.add(currentLog);
		writeJsonToFile();
	}

	// private void copyingArray() {
	// SessionLogEntry[] updated = new SessionLogEntry[oldLogs.length+1];
	// java.lang.System.arraycopy(oldLogs, 0, updated, 0, oldLogs.length);;
	// this.oldLogs = updated;
	// oldLogs[oldLogs.length-1] = currentLog;
	// }

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

}
