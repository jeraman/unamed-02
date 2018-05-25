package musicalTasksTest;

import processing.core.PApplet;
import ddf.minim.*;
import generators.AudioFileGenerator;


public class AudioFileGeneratorTest extends PApplet {
			
	Minim       minim;	//my minim variable
	AudioOutput out;
		
	public static void main(String[] args) {
		PApplet.main("tests.AudioFileGeneratorTest");
		
    }
	
	public void settings() {
		size(800, 600);
	}
	
	public void setup() {
		background(0);
		minim = new Minim(this);
		out = minim.getLineOut();
	}

	public void draw() {
		background(0);

	}
	
	AudioFileGenerator audio;
	
	public void mousePressed() {
		audio = new AudioFileGenerator("123go.mp3");
		audio.patchOutput(out);
		audio.play();
	}
	public void mouseReleased() {
		audio.stop();
		audio.unpatchOutput(out);
	}

}
