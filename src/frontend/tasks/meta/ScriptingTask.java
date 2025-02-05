package frontend.tasks.meta;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import controlP5.ControlP5;
import controlP5.Group;
import frontend.Main;
import frontend.ZenStates;
import frontend.core.Blackboard;
import frontend.core.State;
import frontend.tasks.Task;
import frontend.ui.FileOpenerWithButtonUi;
import frontend.ui.ToggleUi;
import processing.core.PApplet;
import soundengine.SoundEngine;


public class ScriptingTask extends AbstractMetaTask {

	private static final long serialVersionUID = 1L;
	private transient static ScriptEngine engine;
	private transient static ScriptContext context;
	private transient FileReader script;
	
	private FileOpenerWithButtonUi filename;
	private ToggleUi shouldRepeat;
	
	private static final String defaultScriptFile = "example.js";

	public ScriptingTask(PApplet p, ControlP5 cp5, String taskname, SoundEngine eng) {
		super(p, cp5, taskname, eng);
		
		this.filename = new FileOpenerWithButtonUi(defaultScriptFile);
		this.shouldRepeat = new ToggleUi();
		
		loadScript();
		setContext();
		
		Main.log.countJsScript();
	}
	
	@Override
	public void build(PApplet p, ControlP5 cp5, SoundEngine en) {
		super.build(p, cp5, eng);
		loadScript();
		setContext();
	}

	public Task clone_it() {
		return new ScriptingTask(p, cp5, this.name, this.eng);
	}

	@Override
	public void run() {
		boolean wasFirstTime = first_time;
		super.run();
		if (shouldRepeat.getValue() || wasFirstTime)
			evaluateScript();
	}

	// loading the required engines
	void loadScript() {
		// if it's loading the engine for the first time
		if (engine == null)
			engine = new ScriptEngineManager().getEngineByName("nashorn");

		try {
			script = new FileReader(p.sketchPath() + "/data/" + name);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void setContext() {
		context = engine.getContext();
	}

	void updateContext() {
		Blackboard b = ZenStates.board();
		List<String> ordered = new ArrayList<String>(b.keySet());

		for (String val : ordered) 
			context.setAttribute(val, b.get(val), ScriptContext.ENGINE_SCOPE);

		context.setAttribute("blackboard", b, ScriptContext.ENGINE_SCOPE);
	}
	
	void evaluateScript() {
		try {
			loadScript();
			updateContext();
			if(debug())
				System.out.println("evaluating script " + name);
			engine.eval(script, context);

		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	private void processRepeatChange() {
		if (shouldRepeat.update() && debug())
			System.out.println("update loop " + shouldRepeat.getValue());
	}
	
	@Override
	protected void processAllParameters() {
		processRepeatChange();
	}
	
	/////////////////////////////////
	// UI config
	public Group load_gui_elements(State s) {
		this.textlabel = "JS Script";

		String id = get_gui_id();
		Group g = super.load_gui_elements(s);
		int width = g.getWidth() - (localx * 2);

		this.backgroundheight = (int) (localoffset * 3.2);
		g.setBackgroundHeight(backgroundheight);

		filename.createUI(id, "filename", localx, localy + (0 * localoffset), width, g);		
		shouldRepeat.createUI(id, "once - repeat", localx, localy + (int)(1.7 * localoffset), width, g);

		return g;
	}
	
}