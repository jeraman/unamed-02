package frontend.ui.visuals;

/******************************************************************************
 ******************************************************************************
 ** MULTILAYER PIE MENU *******************************************************
 ******************************************************************************
 *** jeraman.info, Jan. 12 2017 ***********************************************
 *****************************************************************************/

import processing.core.PApplet;

public class MultiLevelPieMenu {

	private String[] task_list = { "Blackboard", "", "", "", "Generators", "Augmenters", "Effects", "Meta" };
	private String[] effect_list = { "Filter", "", "", "", "Delay", "Flanger", "ADSR", "BitChrush" };
	private String[] generator_list = { "Sample", "", "", "Oscillator", "FM Synth", "Live Input" };
	private String[] augmenter_list = { "Chord", "", "Note", "Interval" };
	private String[] meta_list = { "OSC Message","", "", "State Mechine", "JS Script", "DMX Light" };
	private String[] blackboard_list = { "Default", "", "", "Random", "Oscillator", "Ramp" };

	private PieMenu main;
	private PieMenu effect;
	private PieMenu generator;
	private PieMenu augmenter;
	private PieMenu meta;
	private PieMenu blackboard;

	private boolean is_opening;
	private PApplet p;

	public MultiLevelPieMenu(PApplet p) {
		this.p = p;

		// status = MLP_Status.CLOSED;
		is_opening = true;

		main = new PieMenu(p);
		main.set_options(task_list);

		effect = new PieMenu(p, main.getX(), main.getY(), (int) (main.getDiam() * 1.75));
		effect.set_inner_circle_diam(main.getDiam());
		effect.set_options(effect_list);

		generator = new PieMenu(p, main.getX(), main.getY(), (int) (main.getDiam() * 1.75));
		generator.set_inner_circle_diam(main.getDiam());
		generator.set_options(generator_list);

		augmenter = new PieMenu(p, main.getX(), main.getY(), (int) (main.getDiam() * 1.75));
		augmenter.set_inner_circle_diam(main.getDiam());
		augmenter.set_options(augmenter_list);

		meta = new PieMenu(p, main.getX(), main.getY(), (int) (main.getDiam() * 1.75));
		meta.set_inner_circle_diam(main.getDiam());
		meta.set_options(meta_list);

		blackboard = new PieMenu(p, main.getX(), main.getY(), (int) (main.getDiam() * 1.75));
		blackboard.set_inner_circle_diam(main.getDiam());
		blackboard.set_options(blackboard_list);
	}

	void setup() {
		main.setup();
		effect.setup();
		generator.setup();
		augmenter.setup();
		meta.setup();
		blackboard.setup();

		hide();
	}

	public void draw() {
		p.noStroke();
		effect.draw();
		generator.draw();
		augmenter.draw();
		meta.draw();
		blackboard.draw();

		main.draw();

		// if is showing, try to update the second layer
		if (!is_hidden_or_fading())
			update_second_layer_selection();
	}

	// sets the diam of the inner circle
	public void set_inner_circle_diam(float newdiam) {
		this.main.set_inner_circle_diam(newdiam);
	}

	public boolean is_showing() {
		return main.is_showing();
	}

	public boolean is_fading_away() {
		return main.is_fading_away();
	}

	boolean is_hidden_or_fading() {
		return main.is_hidden_or_fading();
	}

	void update_second_layer_selection() {

		switch (get_main_selection()) {
		case 0: // Blackboard
			if (p.mousePressed)
				show_blackboard();
			is_opening = true;
			break;
		case 4: // Generator
			if (p.mousePressed)
				show_generator();
			is_opening = true;
			break;
		case 5: // Augmenter
			if (p.mousePressed)
				show_augmenter();
			is_opening = true;
			break;
		case 6: // Effects
			if (p.mousePressed)
				show_effects();
			is_opening = true;
			break;
		case 7: // Meta
			if (p.mousePressed)
				show_meta();
			is_opening = true;
			break;
		}
	}

	public void set_position(int x, int y) {
		main.set_position(x, y);
		effect.set_position(x, y);
		generator.set_position(x, y);
		augmenter.set_position(x, y);
		meta.set_position(x, y);
		blackboard.set_position(x, y);
	}

	// reimplement this according to the new structure
	int get_main_selection() {
		return main.get_selection();
	}

	// reimplement this according to the new structure
	public int get_selection() {
		int result = -1;

		// testing if selection is a sound message (between 10 and 19)
		result = generator.get_selection();
		if (result != -1)
			return result + 10;

		// testing if selection is a haptics message (between 20 and 29)
		result = augmenter.get_selection();
		if (result != -1)
			return result + 20;

		// testing if selection is a light message (between 30 and 39)
		result = effect.get_selection();
		if (result != -1)
			return result + 30;

		// testing if selection is a blackboard message (between 40 and 49)
		result = blackboard.get_selection();
		if (result != -1)
			return result + 40;

		// testing if selection is a meta message (between 50 and 59)
		result = meta.get_selection();
		if (result != -1)
			return result + 50;

		return result;
	}

	public void show() {
		main.show();
	}

	void show_effects() {
		generator.hide();
		augmenter.hide();
		blackboard.hide();
		meta.hide();
		effect.show();
	}

	void show_generator() {
		effect.hide();
		augmenter.hide();
		blackboard.hide();
		meta.hide();
		generator.show();
	}

	void show_augmenter() {
		effect.hide();
		generator.hide();
		blackboard.hide();
		meta.hide();
		augmenter.show();
	}

	void show_blackboard() {
		effect.hide();
		generator.hide();
		augmenter.hide();
		meta.hide();
		blackboard.show();
	}

	void show_meta() {
		effect.hide();
		generator.hide();
		augmenter.hide();
		blackboard.hide();
		meta.show();
	}

	public void hide() {
		main.hide();
		direct_hide_second_layer();
	}

	void direct_hide_second_layer() {
		effect.direct_hide();
		generator.direct_hide();
		augmenter.direct_hide();
		meta.direct_hide();
		blackboard.direct_hide();
		is_opening = false;
	}
}
