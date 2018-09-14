package frontend;

/************************************************
 ** My main!
 ************************************************
 ** jeraman.info, Sep. 30 2016 ******************
 ************************************************
 ************************************************/

import processing.core.PApplet;
import processing.core.PFont;
import soundengine.SoundEngine;
import soundengine.util.MidiIO;
import oscP5.*;
import java.math.BigDecimal;
import java.util.Vector;

import javax.script.*;

import controlP5.*;
import ddf.minim.*;
import frontend.core.Blackboard;
import frontend.core.Expression;
import frontend.core.MainCanvas;
import frontend.core.Serializer;
import frontend.ui.*;
import logging.Logger;

public class Main extends PApplet {

	public ZenStates zenstates;
	public static Serializer serializer;
	public static Logger log;
	private static Main inst;

	public static void main(String[] args) {
		PApplet.main("frontend.Main");
	}

	public void settings() {
		fullScreen(1);
		// size(800, 600);
//		size(1280, 720);
	}

	public void setup() {
		frame.setTitle("KeyStates");
		try {
			this.initVariables();
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	private void initVariables() {
		inst = this;
		zenstates = new ZenStates(this);
		serializer = new Serializer(this);
		log = new Logger(this, ZenStates.USER_ID);
	}

	public void draw() {
		try {
			log.update();
			zenstates.draw();
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}
	
	public void exit() {
		if (log != null)
			log.close();
		super.exit();
	}

	public void exit(Exception e) {
		if (log != null)
			log.close(e);
		super.exit();
	}

	public void keyPressed() {
		try {
			zenstates.keyPressed();
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	public void keyReleased() {
		try {
			zenstates.keyReleased();
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	public void mousePressed() {
		try {
			zenstates.mousePressed();
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	public void mouseDragged() {
		try {
			zenstates.mouseDragged();
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	public void mouseReleased() {
		try {
			zenstates.mouseReleased();
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	public void noteOn(int channel, int pitch, int velocity) {
		try {
			zenstates.noteOn(channel, pitch, velocity);
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	public void noteOff(int channel, int pitch, int velocity) {
		try {
			zenstates.noteOff(channel, pitch, velocity);
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	public void controllerChange(int channel, int number, int value) {
		try {
			zenstates.controllerChange(channel, number, value);
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	public void oscEvent(OscMessage msg) {
		try {
			zenstates.oscEvent(msg);
		} catch (Exception e) {
			e.printStackTrace();
			exit(e);
		}
	}

	public boolean user_pressed_minus() {
		return zenstates.user_pressed_minus();
	}

	public static Main instance() {
		return inst;
	}
}
