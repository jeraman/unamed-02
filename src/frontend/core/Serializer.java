
package frontend.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import frontend.Main;
import frontend.ZenStates;
import soundengine.util.Util;

/******************************************************
 ** Class that implements file saving/loading *********
 ******************************************************
 ** this code was written by Sofian and incorporated * into my prototype in DEc.
 * 1st *********************
 *****************************************************/
public class Serializer {

	Main p;

	// variable to handle autosave
	int autosavetime = 1; // minutes
	int timestamp;
	File autosave_file;

	public Serializer(Main zenStates) {
		this.p = zenStates;
		//setup_autosave();
	}

	// public Serializer(ZenStates zenStates) {
	// // TODO Auto-generated constructor stub
	// }

	File lastSaveFile = null;

	public void save() {
		if (lastSaveFile == null)
			saveAs();
		else
			_saveAs(lastSaveFile);
	}

	void saveAs() {
		p.selectOutput("Select file: ", "_saveAs", lastSaveFile, this);
	}

	public void load() {
		p.selectInput("Select file: ", "_load", lastSaveFile, this);
	}

	public boolean check_if_file_exists_in_sketchpath(String name) {
		File f = new File(p.sketchPath() + "/data/patches/" + name);
		return f.exists();
	}

	public void delete(String name) {
		File f = new File(p.sketchPath() + "/data/patches/" + name);
		if (f.exists())
			f.delete();
	}

	public File returnFileTosave(File f) {
		return f;
	}

	private boolean debug() {
		return ZenStates.debug;
	}

	void setup_autosave() {
		timestamp = p.minute();
		autosave_file = new File(p.sketchPath() + "/data/patches/_temp.zen");

		if (debug())
			System.out.println(p.sketchPath());
	}

	public void autosave() {
		int time_elapsed = p.abs(p.minute() - timestamp);

		if (time_elapsed > autosavetime) {
			_saveAs(autosave_file, ZenStates.canvas.root, ZenStates.canvas.timeCounter, false);
			timestamp = p.minute();
			if (debug())
				System.out.println("saving!");
		}
	}

	public void update_last_saved(File file) {
		lastSaveFile = file;
		autosave_file = file;
	}

	public void _saveAs(File file) {
		// _saveAs(file, p.canvas.root, true);
		_saveAs(file, ZenStates.canvas.root, ZenStates.canvas.timeCounter, true);
		update_last_saved(file);
	}

	// public void _saveAs (String filename, StateMachine sm) {
	public void _saveAs(String filename, StateMachine sm) {
		File f = new File(p.sketchPath() + "/data/patches/" + filename);
		_saveAs(f, sm, ZenStates.canvas.timeCounter, true);
	}

	// public void _saveAs(File file, StateMachine sm, boolean should_rename) {
	public void _saveAs(File file, StateMachine sm, TempoControl tempo, boolean should_rename) {
		if (file == null)
			return;
		try {
			// renames if necessary
			if (should_rename)
				sm.update_title(file.getName());
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			// oos.writeObject(p.board());
			// oos.writeObject(p.canvas());
			oos.writeObject(sm);
			oos.writeObject(tempo);
			oos.close();
		} catch (Exception e) {
			System.out.println("ERROR saving to file: " + file + " [exception: " + e.toString() + "].");
			e.printStackTrace();
		}
	}

	public File loadsSubStateMachineFile(String filename) {
		File file = new File(p.sketchPath() + "/data/patches/" + filename);
		return file;
	}

	public boolean existsSubStateMachineInFile(String filename) {
		// if there the file exists, returns true. otherwise, returns false
		return (loadsSubStateMachineFile(filename).exists()) ? true : false;
	}

	public StateMachine loadSubStateMachine(String filename) {

		// loading the file
		File file = loadsSubStateMachineFile(filename);

		// if the file does not exist, return null!
		if (!file.exists())
			return null;

		StateMachine result = null;

		try {
			// p.is_loading = true;
			// p.canvas.hide();
			// p.cp5.setAutoDraw(false);

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			result = (StateMachine) ois.readObject();
			result.build(p, ZenStates.cp5);
			if (debug())
				System.out.println("loading any new substatemachine");
			result.check_if_any_substatemachine_needs_to_be_reloaded_from_file();
			ois.close();
			
			Main.log.countLoadedExistingSM();

		} catch (Exception e) {
			System.out.println("ERROR loading sub-statemachine: " + file + " [exception: " + e.toString() + "].");
			e.printStackTrace();
		}

		if (debug())
			System.out.println("done loading subpatch!");
		return result;
	}

	public void _load(File file) {

		// if there is no file to open, forget about it!
		if (file == null)
			return;

		try {
			ZenStates.is_loading = true;
			Util.delay(100);
			ZenStates.canvas.hide();
			ZenStates.cp5.setAutoDraw(false);

			ZenStates.canvas.clear();
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));

			ZenStates.canvas.setup((StateMachine) ois.readObject(), (TempoControl) ois.readObject());

			// p.print("loading a new substatemachine");
			ZenStates.canvas.root.check_if_any_substatemachine_needs_to_be_reloaded_from_file();

			// lastSaveFile = file;
			update_last_saved(file);

			ois.close();

		} catch (Exception e) {
			System.out.println("ERROR loading file: " + file + " [exception: " + e.toString() + "].");
			e.printStackTrace();
			// p.board = new Blackboard(p);
			// p.canvas = new MainCanvas(p, p.cp5);
			ZenStates.canvas.setup();
		}

		ZenStates.canvas.board.reset();
		ZenStates.canvas.show();
		ZenStates.cp5.setAutoDraw(true);
		ZenStates.is_loading = false;
		if (debug())
			System.out.println("done loading!");
	}

}
