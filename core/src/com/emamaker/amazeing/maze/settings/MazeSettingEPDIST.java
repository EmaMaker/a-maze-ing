package com.emamaker.amazeing.maze.settings;

import com.emamaker.amazeing.ui.UIManager;

public class MazeSettingEPDIST extends MazeSetting{

	/* Game max. number of players settings*/

	public MazeSettingEPDIST(String name_, String[] options_, UIManager uiManager_) {
		super(name_, options_, uiManager_);
	}
	public MazeSettingEPDIST(String name_, String[] options_, int defaultOption, UIManager uiManager_) {
		super(name_, options_, defaultOption, uiManager_);
	}

	@Override
	public void parseOptionString(String opt) {
		if(Integer.valueOf(opt) <= MazeSettings.MAZEX * 0.75) {
			MazeSettings.EPDIST = Integer.valueOf(opt);
		}else{
			MazeSettings.EPDIST = 5;
		}
	}
	
}
