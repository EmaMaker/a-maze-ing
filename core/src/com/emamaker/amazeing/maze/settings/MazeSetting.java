package com.emamaker.amazeing.maze.settings;

import java.util.Arrays;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.emamaker.amazeing.ui.UIManager;

public class MazeSetting {
	
	/*This object holds whatever is needed for a single setting to be changed, this includes:
	 * A set of possible options
	 * Two buttons to go back and forth between the options
	 * A label with the current option
	 * A label with the name of the option
	 * Methods to set the correct variables, this should be overwritten by every single class
	 */
	
	protected int currentOption = 0;
	protected String[] options;
	protected String name;
	protected Table table;
	private UIManager uiManager;
	private Label currentOptLabel;
	private int defaultOption;
	private int prevState;
	
	public MazeSetting(String name_, String[] options_, UIManager uiManager_) {
		this(name_, options_, 0, uiManager_);
	}
	
	public MazeSetting(String name_, String[] options_, int defaultOption, UIManager uiManager_) {
		this.defaultOption = defaultOption;
		this.currentOption = defaultOption;
		this.name = name_;
		this.options = Arrays.copyOf(options_, options_.length);
		this.uiManager = uiManager_;
		
		//Build the Table which will be later used to add this to the screen
		table = new Table();
		Label nameLabel = new Label(this.name, uiManager.skin);
		currentOptLabel = new Label(this.options[currentOption], uiManager.skin);
		TextButton backBtn = new TextButton("<", uiManager.skin);
		TextButton forthBtn = new TextButton(">", uiManager.skin);
		TextButton resetBtn = new TextButton("R", uiManager.skin);
		
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
		
		table.row().expandX().fillX();
		table.add(nameLabel).fillX();
		table.add(backBtn).fillX();
		table.add(currentOptLabel).fillX();
		table.add(forthBtn).fillX();
		table.add(resetBtn).fillX();
	}
	
	public Table getTable() {
		return table;
	}
	
	public void reset() {
		currentOption = defaultOption;
		update();
	}
	
	public void update() {
		preUpdate();
		customUpdate();
	}
	
	public void saveState() {
		prevState = currentOption;
	}
	
	public void restoreState() {
		currentOption = prevState;
	}
	
	public void preUpdate() {
		this.currentOption = (this.currentOption+this.options.length)%this.options.length;
		this.currentOptLabel.setText(this.options[currentOption]);
	}
	
	//This is the method to overwrite to update the MazeSettings variables
	public void customUpdate() {}

}
