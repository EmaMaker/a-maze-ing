package com.emamaker.amazeing.manager.managers;

import java.util.Set;

import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.powerups.PowerUp;
import com.emamaker.amazeing.ui.screens.PreGameScreen;

public class GameManagerClient extends GameManager {

	public GameManagerClient() {
		super(AMazeIng.getMain(), GameType.CLIENT);
		setupHud();
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

		main.world.modelBatch.begin(main.world.cam);

		renderPlayers();
		renderPowerUps();

		main.world.modelBatch.end();

		hudUpdate();
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

	@Override
	public void usePowerUp(MazePlayer p) {
//		System.out.println("using powerup for " + p + " " + p.currentPowerUp);
		main.client.requestUsePowerUp(p);
	}

	// These features don't exist in clients
	@Override
	public void assignPowerUps() {
	}
	@Override
	public void assignPowerUp(MazePlayer p, PowerUp p1) {
	}
	@Override
	public void assignPowerUp(MazePlayer p, PowerUp p1, boolean b) {
	}
	@Override
	public void requestChangeToMap(int[][] todraw) {
	}
	@Override
	public void revokePowerUp(MazePlayer p) {
	}
	

	@Override
	public void quitGameByBtn() {
		super.quitGameByBtn();
		main.server.stop();
		main.client.stop();
		main.setScreen(main.uiManager.titleScreen);
	}
	
}
