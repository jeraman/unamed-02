package frontend.core;

import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import processing.core.PApplet;
import soundengine.util.MidiIO;
import soundengine.util.Util;

import java.util.regex.*;
import javax.script.ScriptException;
import frontend.Main;
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
		this.ui = new BlackboardWindowUi(this, p);
		this.build(p);
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

	public void build(PApplet p) {
		System.out.println("@TODO [BLACKBOARD] verify what sorts of things needs to be initialize when loaded from file");
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
		this.initKeyboardVariables();
		this.initTempoVariables();
	}

	public void update_global_variables() {
		// if the blackboard wasn't loaded yet
		if (p == null)
			return;
		
		this.updatePcVariables();
		this.updateKeyboardVariables();
		this.updateTempoVariables();
	}
	
	private void initPcVariables() {
		this.put("mouseX", (float) p.mouseX / p.width);
		this.put("mouseY", (float) p.mouseY / p.height);
		this.put("mousePressed", p.mousePressed);
		this.put("pcKey", "\""+p.key+"\"");
		this.put("pcKeyPressed", p.keyPressed);
	}
	
	private void updatePcVariables() {
		this.replace("mouseX", (float) p.mouseX / p.width);
		this.replace("mouseY", (float) p.mouseY / p.height);
		this.replace("mousePressed", p.mousePressed);
		this.replace("pcKey", "\""+p.key+"\"");
		this.replace("pcKeyPressed", p.keyPressed);
	}
	
	private void initTempoVariables() {
		this.put("beat", Main.instance().getBeat());
		this.put("bar", Main.instance().getBar());
		this.put("noteCount", Main.instance().getNoteCount());
		this.put("bpm", Main.instance().getBPM());
		this.put("time", Main.instance().getTime());
		this.put("seconds", Main.instance().getSeconds());
		this.put("minutes", Main.instance().getMinutes());
	}
	
	private void updateTempoVariables() {
		this.replace("beat", Main.instance().getBeat());
		this.replace("bar", Main.instance().getBar());
		this.replace("noteCount", Main.instance().getNoteCount());
		this.replace("time", Main.instance().getTime());
		this.replace("seconds", Main.instance().getSeconds());
		this.replace("minutes", Main.instance().getMinutes());
	}
	
	private void initKeyboardVariables() {
		String playing = Main.instance().whatUserIsPlaying();
		this.put("playing", playing);
		String[] details = processPlaying(playing);
		this.put("note", details[0]);
		this.put("interval", details[1]);
		this.put("chord", details[2]);
		this.put("key", Main.instance().getLastPlayedNote());
		this.put("keyPressed", Main.instance().thereIsKeyDown());
		this.put("keyReleased", Main.instance().thereIsKeyReleased());
		this.put("numKeyPresses", Main.instance().numberOfKeyPressed());
		initKeyboardCC();
		
	}
	
	private void initKeyboardCC() {
		for (int i = 0; i < 16; i++)
			this.put("cc"+(i+1), MidiIO.getCCValue(i));
	}
	
	private void updateKeyboardVariables() {
		String playing = Main.instance().whatUserIsPlaying();
		this.replace("playing", playing);
		String[] details = processPlaying(playing);
		this.replace("note", details[0]);
		this.replace("interval", details[1]);
		this.replace("chord", details[2]);
		String lastNote =  "\"" + Main.instance().getLastPlayedNote() + "\"";
		this.replace("key", lastNote);
		this.replace("keyPressed", Main.instance().thereIsKeyDown());
		this.replace("keyReleased", Main.instance().thereIsKeyReleased());
		this.replace("numKeyPresses", Main.instance().numberOfKeyPressed());
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
		// println("Converted expression1: " +expr);
		expr = _processPattern(pattern2, expr);
		// println("Converted expression2: " +expr);
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
		// return expr.replaceAll("\\\\\\$", "$");
	}

	/////////////////
	// gui methods

	// draws both the header and the items
	public void draw() {
		// if the blackboard wasn't loaded yet
		if (p == null)
			return;
		ui.draw();
	}

	// adding input osc support to the blackboard
	public void oscEvent(OscMessage msg) {
		if (debug) {
			System.out.print("### received an osc message.");
			System.out.print(" addrpattern: " + msg.addrPattern());
			System.out.print(" typetag: " + msg.typetag());
		}

		// gets the name
		String name = msg.addrPattern();
		// processing the address
		name = name.substring(1, name.length());
		name = name.replace("/", "_");

		String typetag = msg.typetag();
		int typetag_size = typetag.length();

		p.println(msg.typetag());

		for (int i = 0; i < typetag_size; i++) {

			// value will be stored in this variable
			Object value = null;

			// checks for the right data type

			// if (typetag.charAt(i).equals("i")) //integer
			if (typetag.charAt(i) == 'i')// integer
				value = msg.get(i).intValue();
			// else if (msg.checkTypetag("f")) //float
			if (typetag.charAt(i) == 'f')// float
				value = msg.get(i).floatValue();
			// else if (msg.checkTypetag("d")) //double
			if (typetag.charAt(i) == 'd')// double
				value = msg.get(i).doubleValue();
			// else if (msg.checkTypetag("s")) //string
			if (typetag.charAt(i) == 's')// string
				value = msg.get(i).stringValue();
			// else if (msg.checkTypetag("b")) //boolean
			if (typetag.charAt(i) == 'b')// boolean
				value = msg.get(i).booleanValue();
			// else if (msg.checkTypetag("l")) //long
			if (typetag.charAt(i) == 'l')// long
				value = msg.get(i).longValue();
			// else if (msg.checkTypetag("c")) //char
			if (typetag.charAt(i) == 'c')// char
				value = msg.get(i).charValue();

			if (!containsKey(name + "_" + i))
				put(name + "_" + i, value);
			else
				replace(name + "_" + i, value);
		}
	}

}
