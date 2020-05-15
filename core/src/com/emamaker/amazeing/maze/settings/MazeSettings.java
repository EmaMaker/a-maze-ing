package com.emamaker.amazeing.maze.settings;

import java.util.ArrayList;

import com.emamaker.amazeing.AMazeIng;

public class MazeSettings {

	//This must only hold public static variables to eventually their getters and setters	
	public static int MAZEX = 20;
	public static int MAZEZ = 20;
	public static int MAXPLAYERS = 8;
	public static int MAXPLAYERS_MOBILE = 1;
	public static int EPDIST = 5;
	
	public static ArrayList<MazeSetting> settings = new ArrayList<MazeSetting>();
	public static MazeSetting setDim;
	public static MazeSetting setPlayers;
	public static MazeSetting setPlayers_Mobile;
	public static MazeSetting setEpDist;

	public static String[] maxPlayersDesktop =  new String[] {
			"2", "4", "6", "8", "10", "15", "20"
	};
	public static String[] maxPlayersMobile =  new String[] {
			"1", "2", "3", "4"
	};
	
	public MazeSettings() {
		//Add various settings here
		setDim = new MazeSettingDimension("MAZE DIMENSIONS:", new String[] {
				"10x10", "20x20", "30x30"
		}, 1, AMazeIng.getMain().uiManager);

		setPlayers = new MazeSettingMaxPlayers("MAX NUMBER OF PLAYERS: ", maxPlayersDesktop, AMazeIng.getMain().uiManager);
		setPlayers_Mobile = new MazeSettingMaxPlayersMobile("PLAYERS JOINING FROM THIS DEVICE: ", maxPlayersMobile, 0, AMazeIng.getMain().uiManager);

		setEpDist = new MazeSettingEPDIST("END POINT DISTANCE:", new String[] {
				"1", "2", "5", "10", "20"
		}, 2,  AMazeIng.getMain().uiManager);

		settings.add(setDim);
		settings.add(setPlayers);
		settings.add(setEpDist);
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
