package com.emamaker.amazeing.maze.settings;

import java.util.ArrayList;

import com.emamaker.amazeing.AMazeIng;

public class MazeSettings {

	//This must only hold public static variables to eventually their getters and setters	
	public static int MAZEX = 20;
	public static int MAZEZ = 20;
	public static int MAXPLAYERS = 8;
	
	public static ArrayList<MazeSetting> settings = new ArrayList<MazeSetting>();
	public static MazeSetting setDim;
	public static MazeSetting setPlayers;
	
	public MazeSettings() {
		//Add various settings here
		setDim = new MazeSettingDimension("MAZE DIMENSIONS:", new String[] {
				"10x10", "20x20", "30x30"
		}, 1, AMazeIng.getMain().uiManager);
		if(AMazeIng.PLATFORM == AMazeIng.Platform.DESKTOP)
			setPlayers = new MazeSettingMaxPlayers("MAX NUMBER OF PLAYERS: ", new String[] {
					"2", "4", "6", "8", "10", "15", "20"
			}, AMazeIng.getMain().uiManager);
		else
			setPlayers = new MazeSettingMaxPlayers("MAX NUMBER OF PLAYERS: ", new String[] {
					"2", "4"
			}, AMazeIng.getMain().uiManager);
		
		settings.add(setDim);
		settings.add(setPlayers);
	}

	public static void saveStates() {
		for(MazeSetting m : settings) m.saveState();
	}
	public static void restoreStates() {
		for(MazeSetting m : settings) m.saveState();
	}
	public static void resetAll() {
		for(MazeSetting m : settings) m.reset();
	}
	
	
}
