package soundengine.util;

import frontend.ZenStates;
import processing.core.PApplet;
import themidibus.MidiBus;

public class MidiIO {

	private static MidiBus myBus;
	private static int[] ccValues;

	public static void setup(PApplet p) {
		MidiBus.list();
		// myBus = new MidiBus(p, 0, 1);
		// myBus = new MidiBus(p, 2, 1);
		myBus = new MidiBus(p, ZenStates.MIDI_IN, ZenStates.MIDI_OUT);
		myBus.sendTimestamps(false);
		ccValues = new int[16];
	}

	public static void outputNoteOn(int channel, int pitch, int velocity) {
		myBus.sendNoteOn(channel, pitch, velocity);
	}

	public static void outputNoteOff(int channel, int pitch, int velocity) {
		myBus.sendNoteOff(channel, pitch, velocity);
	}

	public static void outputControllerChange(int channel, int number, int value) {
		myBus.sendControllerChange(channel, number, value);
	}

	public static void outputProgramChange() {
		int status_byte = 0xA0; // For instance let us send aftertouch
		int channel_byte = 0; // On channel 0 again
		int first_byte = 64; // The same note;
		int second_byte = 80; // But with less velocity

		myBus.sendMessage(status_byte, channel_byte, first_byte, second_byte);
	}

	public static void inputNoteOn(int channel, int pitch, int velocity) {
//		System.out.println();
//		System.out.println("Note On:");
//		System.out.println("--------");
//		System.out.println("Channel:" + channel);
//		System.out.println("Pitch:" + pitch);
//		System.out.println("Velocity:" + velocity);

	}

	public static void inputNoteOff(int channel, int pitch, int velocity) {
//		System.out.println();
//		System.out.println("Note Off:");
//		System.out.println("--------");
//		System.out.println("Channel:" + channel);
//		System.out.println("Pitch:" + pitch);
//		System.out.println("Velocity:" + velocity);

	}

	public static int getCCValue (int index) {
		return ccValues[index];
	}
	
	public static void inputControllerChange(int channel, int number, int value) {
//		System.out.println();
//		System.out.println("Controller Change:");
//		System.out.println("--------");
//		System.out.println("Channel:" + channel);
//		System.out.println("Number:" + number);
//		System.out.println("Value:" + value);
		
		ccValues[number-1] = value;
	}
	

}
