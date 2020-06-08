package com.emamaker.amazeing.manager.managers;

import java.util.Set;

import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.player.MazePlayer;

public class GameManagerLocal extends GameManager {

	public GameManagerLocal() {
		super(AMazeIng.getMain(), GameType.LOCAL);
	}

	@Override
	public void generateMaze(Set<MazePlayer> pl, int todraw[][]) {
		super.generateMaze(pl, todraw);

		addTouchScreenInput();
		spreadPlayers();
		mazeGen.setupEndPoint();
		clearPowerUps();
		spawnPowerUps();

		if (todraw != null && getShowGame())
			mazeGen.show(todraw);

		showed = false;
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

	}

	boolean showed = false;

	@Override
	public void generalUpdate() {
		if (getFinished() && !showed) {
			main.setScreen(main.uiManager.playersScreen);
			showed = true;
		}
	}
}
