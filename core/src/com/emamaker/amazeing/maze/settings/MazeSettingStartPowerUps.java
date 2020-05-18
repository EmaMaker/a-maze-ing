package com.emamaker.amazeing.maze.settings;

import com.emamaker.amazeing.ui.UIManager;

public class MazeSettingStartPowerUps extends MazeSetting{
	
	/* Game max. number of players settings*/

	public MazeSettingStartPowerUps(String name_, String[] options_, UIManager uiManager_) {
		super(name_, options_, uiManager_);
	}
	public MazeSettingStartPowerUps(String name_, String[] options_, int defaultOption, UIManager uiManager_) {
		super(name_, options_, defaultOption, uiManager_);
	}

	@Override
	public void parseOptionString(String opt) {
		MazeSettings.START_POWERUPS = Integer.valueOf(opt);
	}
	
}
