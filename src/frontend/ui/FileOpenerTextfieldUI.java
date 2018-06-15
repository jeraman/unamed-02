package frontend.ui;

import java.io.File;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Textfield;
import frontend.Main;
import frontend.tasks.SampleGenTask;
import frontend.tasks.Task;

public class FileOpenerTextfieldUI extends UiElement {
	
	transient private Textfield textfield;
	String filename;
	String lastFilename;

	public static final String defaultSoundFile = "123go.mp3";
	private File file;
	
	public FileOpenerTextfieldUI() {
		file = new File(Main.instance().dataPath(""));
		this.filename = defaultSoundFile;
		this.lastFilename = "";
	}
	
	public void setFilename(String newFilename) {
		this.filename = newFilename;
	}
	
	public String getValue() {
		return filename;
	}

	@Override
	public boolean hasChanged() {
		return !this.lastFilename.trim().equalsIgnoreCase(this.filename);
	}

	@Override
	public void setLastValue() {
		this.lastFilename = this.filename;
	}

	@Override
	public void createUI(String id, String label, int localx, int localy, int w, Group g) {
		this.textfield = (cp5.addTextfield(id + "/" + label)
				.setPosition(localx, localy)
				.setSize(w, (int) (font_size * 1.25))
				.setGroup(g)
				.setText(defaultSoundFile)
				.setAutoClear(false)
				.setLabel(label)
				.align(ControlP5.CENTER, ControlP5.CENTER, ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
				.onClick(callback())
				.onChange(callback())
				);
	}
	
	private boolean hasCorrectExtension(String name) {
		return (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".aiff") || name.endsWith(".aif")); 
	}
	
	public void fileSelected(File selection) {
		if (selection == null) 
			System.out.println("Window was closed or the user hit cancel.");
		else if (!this.hasCorrectExtension(selection.getName())) {
			System.out.println("Bad data file...");
			this.textfield.setText(selection.getName());
			textfield.setColorBackground(errorColor); 
		} else  {
			this.textfield.clear();
			System.out.println("User selected " + "\n name: " + selection.getName()+ "\n path: " + selection.getAbsolutePath());
			this.setFilename(selection.getName());
			this.textfield.setText(selection.getName());
			textfield.setColorBackground(defaultColor); 
		 }
		this.textfield.setFocus(false);
	}
	
	private void openFileDialog() {
		Main.instance().selectInput("Select file: ", "fileSelected", file, this);
	}
	
	private CallbackListener callback() {
		return new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				System.out.println("open file!");
				openFileDialog();
			}
		};
	}

}
