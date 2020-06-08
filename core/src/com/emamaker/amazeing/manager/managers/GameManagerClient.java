package com.emamaker.amazeing.manager.managers;

import java.util.Set;

import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.ui.screens.PreGameScreen;

public class GameManagerClient extends GameManager {

	public GameManagerClient() {
		super(AMazeIng.getMain(), GameType.CLIENT);
	}

	@Override
	public void generateMaze(Set<MazePlayer> pl, int todraw[][]) {
		super.generateMaze(pl, todraw);
		addTouchScreenInput();
		showed = false;
	}

	@Override
	public void inGameUpdate() {
		super.inGameUpdate();

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
		super.generalUpdate();
		if (getFinished() && !showed) {
			System.out.println("Game finished on client!");
			if (main.server.isRunning())
				((PreGameScreen) main.uiManager.preGameScreen).setGameType(GameType.SERVER);
			else
				((PreGameScreen) main.uiManager.preGameScreen).setGameType(GameType.CLIENT);

			main.setScreen(main.uiManager.preGameScreen);
			showed = true;
		}
	}

	// Protecting against myself since this feature doesn't exist yet
	@Override
	public void assignPowerUps() {
	}

	@Override
	public void checkWin() {
	}

}
