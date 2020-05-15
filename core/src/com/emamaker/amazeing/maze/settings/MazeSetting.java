package com.emamaker.amazeing.maze.settings;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.emamaker.amazeing.ui.UIManager;

public class MazeSetting {
	
	/*This object holds whatever is needed for a single setting to be changed, this includes:
	 * A set of possible options
	 * Thread buttons to go back and forth between the options and reset them
	 * A label with the current option
	 * A label with the name of the option
	 * Methods to set the correct variables, this should be overwritten by every single class
	 */
	
	protected UIManager uiManager;
	protected int currentOption = 0;
	protected String[] options;
	protected String name;
	protected Table table;
	private int defaultOption;
	private int prevState;
	
	Label nameLabel, currentOptLabel;
	TextButton backBtn, forthBtn, resetBtn;
	
	public MazeSetting(String name_, String[] options_, UIManager uiManager_) {
		this(name_, options_, 0, uiManager_);
	}
	
	public MazeSetting(String name_, String[] options_, int defaultOption, UIManager uiManager_) {
		this.defaultOption = defaultOption;
		this.currentOption = defaultOption;
		this.name = name_;
		this.uiManager = uiManager_;
		setOptions(options_, defaultOption);


		//Build the Table which will be later used to add this to the screen
		table = new Table();
		nameLabel = new Label(this.name, uiManager.skin);
		currentOptLabel = new Label(this.options[currentOption], uiManager.skin);
		backBtn = new TextButton("<", uiManager.skin);
		forthBtn = new TextButton(">", uiManager.skin);
		resetBtn = new TextButton("R", uiManager.skin);


		
		update();
		// Add actions to the buttons
		backBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				currentOption--;
				update();
				return true;
			}
		});
		forthBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				currentOption++;
				update();
				return true;
			}
		});
		resetBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				reset();
				return true;
			}
		});
		
		buildTable();
	}
	
	public Table getTable() {
		return table;
	}
	
	public void reset() {
		currentOption = defaultOption;
		update();
	}
	
	public void update() {
		currentOption = (currentOption+options.length)%options.length;
		if(currentOptLabel != null) currentOptLabel.setText(options[currentOption]);
		parseOptionString(options[currentOption]);
	}
	
	public void saveState() {
		prevState = currentOption;
	}
	
	public void restoreState() {
		currentOption = prevState;
	}
	
	public void buildTable() {
		table.clear();
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		
		float d = (float) Math.sqrt(width*width + height*height);
		float labScale = d * .00080f;
		float buttonDim = d * 0.025f;
		float space = buttonDim * 0.5f;

		nameLabel.setFontScale(labScale);
		currentOptLabel.setFontScale(labScale);
		backBtn.getLabel().setFontScale(labScale);
		forthBtn.getLabel().setFontScale(labScale);
		resetBtn.getLabel().setFontScale(labScale);
		
		table.row().colspan(2);
		table.add(nameLabel);
		table.add(backBtn).width(buttonDim).height(buttonDim).space(space);
		table.add(currentOptLabel);
		table.add(forthBtn).width(buttonDim).height(buttonDim).space(space);
		table.add(resetBtn).width(buttonDim).height(buttonDim).space(space);
	}

	public void setOptions(String[] opts){
		this.setOptions(opts, 0);
	}

	public void setOptions(String[] opts, int defaultOption){
		this.options = Arrays.copyOf(opts, opts.length);
		this.defaultOption = defaultOption;
		reset();
	}

	public void parseOptionString(String opt) {}
	public String getOptionString() {
		return options[currentOption];
	}

}
