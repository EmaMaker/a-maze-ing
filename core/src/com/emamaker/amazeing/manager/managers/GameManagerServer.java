package com.emamaker.amazeing.manager.managers;

import java.util.Set;

import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.powerups.PowerUp;

public class GameManagerServer extends GameManager {

	long lastPowerUpTime = 0;

	public GameManagerServer() {
		super(AMazeIng.getMain(), GameType.SERVER);
	}

	@Override
	public void generateMaze(Set<MazePlayer> pl, int todraw[][]) {
		super.generateMaze(pl, todraw);

		spreadPlayers();
		mazeGen.setupEndPoint();
		clearPowerUps();
		spawnPowerUps();

		if (todraw != null)
			mazeGen.show(todraw);
	}

	@Override
	public void inGameUpdate() {
		super.inGameUpdate();

		if(System.currentTimeMillis() - lastPowerUpTime > MazeSettings.POWERUP_SPAWN_FREQUENCY) {
			lastPowerUpTime = System.currentTimeMillis();
			spawnRandomPowerUp();
		}
		
		assignPowerUps();
	}

	@Override
	public void assignPowerUp(MazePlayer p, PowerUp p1, boolean immediateUse) {
		if (p.currentPowerUp == null) {
			toDeletePowerUps.add(p1);
			p.currentPowerUp = p1;
			main.server.assignPowerUpRequest(p, p1, immediateUse);
		}
	}

	@Override
	public void revokePowerUp(MazePlayer p) {
		main.server.revokePowerUpRequest(p);
	}
	
}
