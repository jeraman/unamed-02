package frontend.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import controlP5.Canvas;
import controlP5.Group;
import controlP5.ListBox;
import controlP5.ListBox.ListBoxView;
import frontend.Main;
import frontend.core.Blackboard;
import oscP5.OscMessage;
import processing.core.PApplet;
import soundengine.util.Util;

public class BlackboardWindowUi extends AbstractElementUi {
	
	private transient Group g;
	
	private int mywidth;
	private int myheight;
	private int x;
	private int y;
	private int numberOfItems;
	private Blackboard bb;
	private ArrayList<String> blacklisted;

	transient private PApplet p;
	
	public BlackboardWindowUi (Blackboard bb, PApplet p) {
		this.mywidth = 6 * font_size;
		this.myheight = 2 * font_size;
		this.numberOfItems = 0;
		this.bb = bb;
		this.build(p);
		this.blacklisted = new ArrayList<String>();
	}
	
	public void addToBlacklist(String item) {
		this.blacklisted.add(item);
	}
	
	public void setPosition(int x, int y) {
		if (g != null)
			g.setPosition(x, y);
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
		if(debug())
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

	private boolean blacklisted(String varname) {
		return this.blacklisted.contains(varname);
	}

	void drawItem(String var_name, Object var_value, int posx, int posy, int mywidth, int myheight) {
		int xoffset = mywidth;
		posx += xoffset / 4;
		mywidth = this.getWidth() / 2;

		p.noStroke();
		p.fill(AbstractElementUi.blackboardBackgroundColor);
		p.rectMode(p.CENTER);
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

		p.text(var_name, posx, posy);
		p.text(value_string, posx + mywidth + 5, posy);
	}
	
	public void createUi() {
		g = cp5.addGroup("blackboard")
				.setPosition(250, 100)
				.setWidth(this.getWidth())
				.activateEvent(true)
				.setHeight(20)
				.setBackgroundColor(blackboardBackgroundColor)
				.setColorBackground(blackboardHeaderColor)
				.setBackgroundHeight(250)
				.setMoveable(true)
				.setVisible(true)
				.setLabel("BLACKBOARD");
		
		cp5.addTextlabel("test")
		.setText("Time: ")
		.setPosition(0, 400)
		.setGroup(g);
		
		g.getCaptionLabel().align(cp5.CENTER, cp5.CENTER);
		g.getCaptionLabel().setColor(blackboardHeaderTextColor);

		//option 1 - using list box. the problem is that i was not able to create two rows as required by the blackboard
		// ListBox lb = cp5.addListBox("listbox")
		// .setPosition(10, 10)
		// .setItemHeight(23)
		// .setBarHeight(0)
		// .setBackgroundColor(blackboardBackgroundColor)
		// .setColorBackground(blackboardBackgroundColor)
		// .open()
		// .setWidth(this.getWidth()-20)
		// .setGroup(g);
		//
		// experimental_draw_bb_items(lb);
		
		//option 3 - using cp5's canvas. the problem is using scrolling
		// MyCanvas cc = new MyCanvas();
		// cc.pre(); // use cc.post(); to draw on top of existing controllers.
		// cp5.addCanvas(cc); // add the canvas to cp5		
		
		this.setPosition(p.width/2, p.height/2);
	}
	
	
	void experimental_draw_bb_items(ListBox lb ) {
		draw_header_gui();
		int i = 0;

		List<String> ordered = new ArrayList<String>(bb.keySet());
		Collections.sort(ordered);

		for (String val : ordered) {
			if (!this.blacklisted(val)) {
				lb.addItem(val + " - " + bb.get(val), i);
				i++;
			}
		}

		this.numberOfItems = i;
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
