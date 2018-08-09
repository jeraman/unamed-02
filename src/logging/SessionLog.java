package logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;

import com.google.gson.*;

class SessionLog {
	
	private Timestamp begin;
	private Timestamp end;
	private String userID;
	private String closingInfo;
	private String detailedClosingInfo;
	
	transient private File file;
	transient private static Gson gson = new Gson();
	
	protected SessionLog(String userID) {
		this.userID = userID;
		this.begin = new Timestamp(System.currentTimeMillis());
		this.loadFile();
	}
	
	private void loadFile() {
		String filename = "./data/logs/" + userID + ".json";
		this.file = new File(filename);
	}

	protected void updateCloseInfo() {
		this.end = new Timestamp(System.currentTimeMillis());
		this.closingInfo = "None";
		this.detailedClosingInfo = "No errors occured";
	}
	
	protected void updateCloseInfo(Exception e) {
		this.updateCloseInfo();
		this.closingInfo = e.toString();
		this.detailedClosingInfo = e.getStackTrace().toString();
	}
	
	public void close() {
		updateCloseInfo();
		writeJsonToFile();
	}
	
	public void close(Exception e) {
		updateCloseInfo(e);
		writeJsonToFile();
	}
	
	private void writeJsonToFile() {
		try {
			PrintWriter printer = new PrintWriter(file);
			printer.println(toJson());
			printer.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private String toJson() {
		String json = gson.toJson(this);
		System.out.println(json);
		return json;
	}
}
