package frontend.core;

import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import processing.core.PApplet;
import soundengine.util.MidiIO;
import soundengine.util.Util;

import java.util.regex.*;
import javax.script.ScriptException;
import frontend.Main;
import frontend.ZenStates;
import frontend.ui.AbstractElementUi;
import frontend.ui.BlackboardWindowUi;

import java.util.*;
import java.math.BigDecimal;
import oscP5.*;

/**
 * Stores global variables to be used anywhere in the system.
 * 
 * @author jeraman.info
 * @date Oct. 11 2016
 *
 * @update part of this code (support to expressions, and the ConcurrentHashMap)
 *         was written by Sofian and incorporated by him into the original code.
 */
public class Blackboard extends ConcurrentHashMap<String, Object> implements Serializable {
	public boolean debug = false;
	
	private BlackboardWindowUi ui;
	transient private PApplet p;
	
	public Blackboard(PApplet p) {
		this.setup_expression_loading_bug();
		this.ui = new BlackboardWindowUi(this, p);
		this.build(p);
	}
	
	// solves the freezing problem when loading the first expression
	private void setup_expression_loading_bug() {
		Expression test = new Expression("0");
		try {
			((Expression) test).eval(this);
		} catch (ScriptException e) {
			System.out.println("ScriptExpression thrown, unhandled update.");
		}
	}

	public int getWidth() {
		return ui.getWidth();
	}

	public int getHeight() {
		return ui.getHeight();
	}

	public int getX() {
		return ui.getX();
	}

	public int getY() {
		return ui.getY();
	}

	void set_debug(boolean b) {
		this.debug = b;
	}
	
	public void createUi() {
		ui.createUi();
	}

	public void build(PApplet p) {
		this.p = p;
		init_global_variables();
		this.ui.build(p);
	}
	
	public Object put (String key, Object value) {
		//Expression.addToEngine(key, value);
		return super.put(key, value);
	}
	
	public Object replace (String key, Object value) {
		//Expression.replaceInEngine(key, value);
		return super.replace(key, value);
	}
	
	public Object remove (String key) {
		//Expression.removeFromEngine(key);
		return super.remove(key);
	}

	private void init_global_variables() {
		this.initPcVariables();
//		if (!ZenStates.is_loading) {
			this.initKeyboardVariables();
			this.initTempoVariables();
//		}
	}

	public void update_global_variables() {
		// if the blackboard wasn't loaded yet
		if (p == null)
			return;
		
		//int test = p.mouseX + p.mouseY;
		//if (p.mousePressed)
		//	test *= 2;
		
		this.updatePcVariables();
		this.updateKeyboardVariables();
		this.updateTempoVariables();
	}
	
	private void initPcVariables() {
		this.put("mousePressed", p.mousePressed);
		this.put("mouseX", (float) p.mouseX / p.width);
		this.put("mouseY", (float) p.mouseY / p.height);
		this.put("pcKey", "\""+p.key+"\"");
		this.put("pcKeyPressed", p.keyPressed);
		
		// this.ui.addToBlacklist("mouseX");
		// this.ui.addToBlacklist("mouseY");
		this.ui.addToBlacklist("mousePressed");
		this.ui.addToBlacklist("pcKey");
		this.ui.addToBlacklist("pcKeyPressed");
	}
	
	private void updatePcVariables() {
		this.replace("mousePressed", p.mousePressed);
		this.replace("mouseX", (float) p.mouseX / p.width);
		this.replace("mouseY", (float) p.mouseY / p.height);
		this.replace("pcKey", "\""+p.key+"\"");
		this.replace("pcKeyPressed", p.keyPressed);
	}
	
	private void initTempoVariables() {
		this.put("beat", ZenStates.getBeat());
		this.put("bar", ZenStates.getBar());
		this.put("noteCount", ZenStates.getNoteCount());
		this.put("bpm", ZenStates.getBPM());
		this.put("time", ZenStates.getTime());
		this.put("seconds", ZenStates.getSeconds());
		this.put("minutes", ZenStates.getMinutes());
		
		this.ui.addToBlacklist("bpm");
		this.ui.addToBlacklist("seconds");
		this.ui.addToBlacklist("minutes");
	}
	
	private void updateTempoVariables() {
		this.replace("beat", ZenStates.getBeat());
		this.replace("bar", ZenStates.getBar());
		this.replace("bpm", ZenStates.getBPM());
		this.replace("noteCount", ZenStates.getNoteCount());
		this.replace("time", ZenStates.getTime());
		this.replace("seconds", ZenStates.getSeconds());
		this.replace("minutes", ZenStates.getMinutes());
	}
	
	private void initKeyboardVariables() {
		String playing = ZenStates.whatUserIsPlaying();
		this.put("playing", playing);
		String[] details = processPlaying(playing);
		this.put("note", details[0]);
		this.put("interval", details[1]);
		this.put("chord", details[2]);
		this.put("key", ZenStates.getLastPlayedNote());
		this.put("pressure", ZenStates.getLastVelocity());
		this.put("keyPressed", ZenStates.thereIsKeyDown());
		this.put("keyReleased", ZenStates.thereIsKeyReleased());
		this.put("numKeyPresses", ZenStates.numberOfKeyPressed());
		initKeyboardCC();
		
		this.ui.addToBlacklist("note");
		this.ui.addToBlacklist("interval");
		this.ui.addToBlacklist("chord");
		this.ui.addToBlacklist("keyReleased");
		this.ui.addToBlacklist("numKeyPresses");
	}
	
	private void initKeyboardCC() {
		for (int i = 0; i < 16; i++) {
			this.put("cc"+(i+1), MidiIO.getCCValue(i));
			this.ui.addToBlacklist("cc"+(i+1));
		}
	}
	
	private void updateKeyboardVariables() {
		String playing = ZenStates.whatUserIsPlaying();
		this.replace("playing", playing);
		String[] details = processPlaying(playing);
		this.replace("note", details[0]);
		this.replace("interval", details[1]);
		this.replace("chord", details[2]);
		String lastNote =  "\"" + ZenStates.getLastPlayedNote() + "\"";
		this.replace("key", lastNote);
		this.replace("pressure", ZenStates.getLastVelocity());
		this.replace("keyPressed", ZenStates.thereIsKeyDown());
		this.replace("keyReleased", ZenStates.thereIsKeyReleased());
		this.replace("numKeyPresses", ZenStates.numberOfKeyPressed());
		updateKeyboardCC();
	}
	
	private void updateKeyboardCC() {
		for (int i = 0; i < 16; i++)
			this.replace("cc"+(i+1), MidiIO.getCCValue(i));
	}
	
	
	private String[] processPlaying(String playing) {
		if (playing.contains("note"))
			return processNote(playing.substring("note:".length()));
		else if (playing.contains("intervals"))
			return processInterval(playing.substring("intervals:".length()));
		else if (playing.contains("interval"))
			return processInterval(playing.substring("interval:".length()));
		else if (playing.contains("chord"))
			return processChord(playing.substring("chord:".length()));
		else
			return processNothing();
	}

	private String[] processNote(String playing) {
		playing = "\"" + playing + "\"";
		return new String[]{playing, "\"none\"", "\"none\""};
	}
	
	private String[] processInterval(String playing) {
		playing = "\"" + playing + "\"";
		return new String[]{"\"none\"", playing, "\"none\""};
	}
	
	private String[] processChord(String playing) {
		playing = "\"" + playing + "\"";
		return new String[]{"\"none\"", "\"none\"", playing};
	}
	
	private String[] processNothing() {
		return new String[]{"\"none\"", "\"none\"", "\"none\""};
	}

	void reset() {
		this.clear();
		this.init_global_variables();
	}

	
//	  Replaces variable names in expression with pattern "$varName" or
//	  "${varName}" with values corresponding to these variables from the
//	  blackboard.
	 
	String processExpression(String expr) {

		// does not support strings
		Pattern pattern1 = Pattern.compile("(\\$(\\w+))");
		Pattern pattern2 = Pattern.compile("(\\$\\{(\\w+)\\})"); // " <-- this
																	// comment
																	// to avoid
																	// code-highlight
																	// issues in
																	// Atom
		// does not suppor 3 bb variables
		// Pattern pattern1 = Pattern.compile("([^\\\\]\\$(\\w+))");
		// Pattern pattern2 = Pattern.compile("([^\\\\]\\$\\{(\\w+)\\})"); //"
		// <-- this comment to avoid code-highlight issues in Atom

		expr = _processPattern(pattern1, expr);
		expr = _processPattern(pattern2, expr);
		return expr;
	}

	String _processPattern(Pattern pattern, String expr) {
		Matcher matcher = pattern.matcher(expr);
		while (matcher.find()) {
			String varName = matcher.group(2); // candidate var name in
												// blackboard
			if (containsKey(varName)) {
				String value = get(varName).toString();

				// if the result is another blackboard variable, try to get its
				// value
				if (!value.equals(varName) && value.contains("$"))
					try {
						value = (new Expression(value).eval(this)).toString();
					} catch (ScriptException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				expr = matcher.replaceFirst(value);
				matcher = pattern.matcher(expr);
			} else if (debug)
				System.out.println("Blackboard variable not found: " + varName);
		}
		if (debug)
			System.out.println("final expression: " + expr);

		return expr;
	}

	public void draw() {
		if (p == null)
			return;
		ui.draw();
	}

	public void oscEvent(OscMessage msg) {
		if (debug) {
			System.out.print("### received an osc message.");
			System.out.print(" addrpattern: " + msg.addrPattern());
			System.out.print(" typetag: " + msg.typetag());
		}

		String name = msg.addrPattern();
		name = name.substring(1, name.length());
		name = name.replace("/", "_");

		String typetag = msg.typetag();
		int typetag_size = typetag.length();

		for (int i = 0; i < typetag_size; i++) {
			Object value = null;

			if (typetag.charAt(i) == 'i')// integer
				value = msg.get(i).intValue();
			if (typetag.charAt(i) == 'f')// float
				value = msg.get(i).floatValue();
			if (typetag.charAt(i) == 'd')// double
				value = msg.get(i).doubleValue();
			if (typetag.charAt(i) == 's')// string
				value = msg.get(i).stringValue();
			if (typetag.charAt(i) == 'b')// boolean
				value = msg.get(i).booleanValue();
			if (typetag.charAt(i) == 'l')// long
				value = msg.get(i).longValue();
			if (typetag.charAt(i) == 'c')// char
				value = msg.get(i).charValue();

			if (!containsKey(name + "_" + i))
				put(name + "_" + i, value);
			else
				replace(name + "_" + i, value);
		}
	}

}
