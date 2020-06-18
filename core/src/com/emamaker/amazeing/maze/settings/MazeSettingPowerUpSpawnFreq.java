package com.emamaker.amazeing.maze.settings;

import com.emamaker.amazeing.ui.UIManager;

public class MazeSettingPowerUpSpawnFreq extends MazeSetting{

	/* Game max. number of players settings*/

	public MazeSettingPowerUpSpawnFreq(String name_, String[] options_, UIManager uiManager_) {
		super(name_, options_, uiManager_);
	}
	public MazeSettingPowerUpSpawnFreq(String name_, String[] options_, int defaultOption, UIManager uiManager_) {
		super(name_, options_, defaultOption, uiManager_);
	}

	@Override
	public void parseOptionString(String opt) {
		if(opt.equals("Off")) {
			MazeSettings.POWERUP_SPAWN_FREQUENCY = Integer.MAX_VALUE;
		}else {
			MazeSettings.POWERUP_SPAWN_FREQUENCY = Integer.valueOf(opt)*1000;
		}
	}
	
}
