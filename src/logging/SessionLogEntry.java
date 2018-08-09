package logging;

import java.sql.Timestamp;

import com.google.gson.*;

class SessionLogEntry {
	
	private Timestamp begin;
	private Timestamp end;
	private String userID;
	private String closingInfo;
	private String detailedClosingInfo;
	
	protected SessionLogEntry(String userID) {
		this.userID = userID;
		this.begin = new Timestamp(System.currentTimeMillis());
	}
	
	protected void close() {
		this.end = new Timestamp(System.currentTimeMillis());
		this.closingInfo = "None";
		this.detailedClosingInfo = "No errors occured";
	}
	
	protected void close(Exception e) {
		this.close();
		this.closingInfo = e.toString();
		this.detailedClosingInfo = e.getStackTrace().toString();
	}
}
