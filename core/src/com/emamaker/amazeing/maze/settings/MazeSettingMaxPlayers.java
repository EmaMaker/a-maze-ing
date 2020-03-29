package com.emamaker.amazeing.maze.settings;

import com.emamaker.amazeing.ui.UIManager;

public class MazeSettingMaxPlayers extends MazeSetting{
	
	/* Game max. number of players settings*/
	
	public MazeSettingMaxPlayers(String name_, String[] options_, UIManager uiManager_) {
		super(name_, options_, uiManager_);
	}

	@Override
	public void customUpdate(){
		MazeSettings.MAXPLAYERS = Integer.valueOf(options[currentOption]);
	}
	
}
