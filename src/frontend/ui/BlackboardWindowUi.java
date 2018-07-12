package frontend.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import controlP5.Group;
import frontend.Main;
import frontend.core.Blackboard;
import oscP5.OscMessage;
import processing.core.PApplet;
import soundengine.util.Util;

public class BlackboardWindowUi extends AbstractElementUi implements Serializable {
	private int mywidth;
	private int myheight;
	private int x;
	private int y;
	private int numberOfItems;
	private transient PApplet p;
	private Blackboard bb;
	
	public BlackboardWindowUi (Blackboard bb, PApplet p) {
		this.mywidth = 6 * font_size;//((Main) p).get_font_size();
		this.myheight = 2 * font_size;
		this.numberOfItems = 0;
		this.bb = bb;
		this.build(p);
	}
	
	public int getWidth() {
		return mywidth * 3;
	}

	public int getHeight() {
		return myheight * (this.numberOfItems + 3);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void build(PApplet p) {
		System.out.println("@TODO [BLACKBOARD] verify what sorts of things needs to be initialize when loaded from file");
		this.p = p;
		this.x = ((Main) p).width - (int) (mywidth * 2.8);
		this.y = myheight;
	}
	
	// draws both the header and the items
	public void draw() {
		// if the blackboard wasn't loaded yet
		if (p == null)
			return;

		draw_header_gui();
		draw_bb_items();
	}

	// draws the header
	void draw_header_gui() {

		p.noStroke();
		p.fill(255, 200);
		p.rectMode(p.CENTER);
		p.rect(x + mywidth, y, (mywidth * 3), myheight);
		p.rectMode(p.CORNERS);

		p.fill(50);
		p.textAlign(p.CENTER, p.CENTER);
		p.text("BLACKBOARD", x + mywidth, y);
	}

	// draws the items
	void draw_bb_items() {
		draw_header_gui();
		int i = 0;

		List<String> ordered = new ArrayList<String>(bb.keySet());
		Collections.sort(ordered);

		for (String val : ordered) {
			if (!this.blacklisted(val)) {
				drawItem(val, bb.get(val), x, y + (myheight * (i + 1)) + i + 1, mywidth, myheight);
				i++;
			}
		}
		
		this.numberOfItems = i;
	}

	// list of memory items that should not be displyed to the use
	boolean blacklisted(String varname) {
		// String varname = element.getKey().toString();
		// if ((varname.contains("frequency") && varname.length() > 25)
		// || (varname.contains("amplitude") && varname.length() > 25)

		if (varname.equals("note") || varname.equals("interval") || varname.equals("chord"))
			// add a new item here
			return false;// true;
		else
			return false;
	}

	void drawItem(String var_name, Object var_value, int posx, int posy, int mywidth, int myheight) {
		int xoffset = mywidth;
		posx += xoffset / 4;
		mywidth = this.getWidth() / 2;

		// header
		p.noStroke();
		p.fill(AbstractElementUi.blackboardBackgroundColor);
		p.rectMode(p.CENTER);
		// p.rect(posx, posy, mywidth, myheight);
		// p.rect(posx + xoffset, posy, mywidth, myheight);
		// p.rect(posx + xoffset + xoffset, posy, mywidth, myheight);
		p.rect(posx, posy, mywidth, myheight);
		p.rect(posx + 1 + mywidth, posy, mywidth - 1, myheight);

		p.fill(AbstractElementUi.whiteColor);
		p.textAlign(p.CENTER, p.CENTER);

		String type_name = var_value.getClass().getName();
		// Object value = element.getValue();
		String value_string = var_value.toString();

		// in case it's a float, only exhibits two decimal points.
		if (var_value instanceof Float)
			value_string = Util.round((float) var_value, 2).toString();
		if (var_value instanceof Double)
			value_string = Util.round((float) ((double) var_value), 2).toString();

		// p.text(type_name.replace("java.lang.", ""), posx, posy);
		// p.text(var_name, posx + xoffset, posy);
		// p.text(value_string, posx + xoffset + xoffset + 5, posy);
		p.text(var_name, posx, posy);
		p.text(value_string, posx + mywidth + 5, posy);
	}

	// adding input osc support to the blackboard
	public void oscEvent(OscMessage msg) {
		if (bb.debug) {
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

			if (!bb.containsKey(name + "_" + i))
				bb.put(name + "_" + i, value);
			else
				bb.replace(name + "_" + i, value);
		}
	}

	@Deprecated
	@Override
	public boolean hasChanged() {
		return false;
	}

	@Deprecated
	@Override
	public void setLastValue() {
	}

	@Deprecated
	@Override
	public void createUI(String id, String label, int localx, int localy, int w, Group g) {
		// TODO Auto-generated method stub

	}

}
