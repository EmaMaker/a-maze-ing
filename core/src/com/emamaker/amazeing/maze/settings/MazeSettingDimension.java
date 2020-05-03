package com.emamaker.amazeing.maze.settings;

import com.emamaker.amazeing.ui.UIManager;

public class MazeSettingDimension extends MazeSetting{
	
	/* Maze dimension settings
	 * Provide Options in the x*z format
	 */
	
	public MazeSettingDimension(String name_, String[] options_, int defaultOption, UIManager uiManager_) {
		super(name_, options_, defaultOption, uiManager_);
	}

	
	public MazeSettingDimension(String name_, String[] options_, UIManager uiManager_) {
		super(name_, options_, uiManager_);
	}
	@Override
	public void parseOptionString(String opt) {
		super.parseOptionString(opt);
		String[] split = opt.split("x");
		MazeSettings.MAZEX = Integer.valueOf(split[0]);
		MazeSettings.MAZEZ = Integer.valueOf(split[1]);
	}
	
}
