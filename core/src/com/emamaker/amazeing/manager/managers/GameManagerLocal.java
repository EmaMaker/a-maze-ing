package com.emamaker.amazeing.manager.managers;

import java.util.Set;

import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.player.MazePlayer;

public class GameManagerLocal extends GameManager {

	public GameManagerLocal() {
		super(AMazeIng.getMain(), GameType.LOCAL);
	}

	@Override
	public void generateMaze(Set<MazePlayer> pl, int todraw[][]) {
		super.generateMaze(pl, todraw);

		spreadPlayers();
		mazeGen.setupEndPoint();
		clearPowerUps();
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
		
		if (getFinished())
			main.setScreen(main.uiManager.playersScreen);
	}
}
