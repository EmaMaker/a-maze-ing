package com.emamaker.amazeing.manager.managers;

import java.util.Set;

import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.player.MazePlayer;

public class GameManagerServer extends GameManager {

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

		assignPowerUps();
	}

	@Override
	public void assignPowerUps() {
//		if (players != null && !players.isEmpty())
//			for (MazePlayer p : players) {
//				main.server.removePowerUp(assignPowerUp(p));
//			}
	}

}
