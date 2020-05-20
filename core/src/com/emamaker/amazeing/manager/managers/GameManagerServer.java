package com.emamaker.amazeing.manager.managers;

import java.util.Set;

import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.ui.screens.PreGameScreen;

public class GameManagerServer extends GameManager {


	public GameManagerServer() {
		super(AMazeIng.getMain(), GameType.SERVER);
	}

	@Override
	public void generateMaze(Set<MazePlayer> pl, int todraw[][]) {
		super.generateMaze(pl, todraw);

		spreadPlayers();
		mazeGen.setupEndPoint();
		powerups.clear();
		spawnPowerUps();

		if (todraw != null && getShowGame())
			mazeGen.show(todraw);

	}


	@Override
	public void inGameUpdate() {
		super.inGameUpdate();

		assignPowerUps();
		
		renderWorld();
		hudUpdate();
		
		main.world.modelBatch.begin(main.world.cam);

		renderPlayers();
		renderPowerUps();

		main.world.modelBatch.end();

		if (getFinished()) {
			((PreGameScreen) main.uiManager.preGameScreen).setGameType(GameType.SERVER);
			main.setScreen(main.uiManager.preGameScreen);
		}
	}
	
	//Protecting against myself since this feature doesn't exist yet
	@Override
	public void assignPowerUps() {
	}

}
