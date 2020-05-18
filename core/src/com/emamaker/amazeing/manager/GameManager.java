package com.emamaker.amazeing.manager;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.AMazeIng.Platform;
import com.emamaker.amazeing.maze.MazeGenerator;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.emamaker.amazeing.player.powerups.PowerUp;
import com.emamaker.amazeing.player.powerups.PowerUps;
import com.emamaker.amazeing.ui.screens.PreGameScreen;
import com.emamaker.voxelengine.block.CellId;
import com.emamaker.voxelengine.player.Player;

public class GameManager {

	AMazeIng main;
	public MazeGenerator mazeGen;

	public boolean gameStarted = false;
	public boolean showGame = true;
	public boolean anyoneWon = false;

	public Stage stage;

	Random rand = new Random();

	GameType type = GameType.LOCAL;

	public ArrayList<MazePlayer> players = new ArrayList<MazePlayer>();
	public ArrayList<PowerUp> powerups = new ArrayList<PowerUp>();
	ArrayList<MazePlayer> toDelete = new ArrayList<MazePlayer>();

	PowerUp pup;

	public GameManager(Game main_, GameType t) {
		main = (AMazeIng) main_;
		gameStarted = false;

		type = t;
		setShowGame(type != GameType.SERVER);

		mazeGen = new MazeGenerator(main, MazeSettings.MAZEX, MazeSettings.MAZEZ);
		stage = new Stage(new ScreenViewport());
	}

	public void generateMaze(Set<MazePlayer> pl, int todraw[][]) {
		main.setScreen(null);

		AMazeIng.getMain().multiplexer.removeProcessor(stage);

		anyoneWon = false;

		if (AMazeIng.PLATFORM == Platform.DESKTOP) {
			if (pl != null) {
				for (MazePlayer p : players)
					if (!pl.contains(p))
						toDelete.add(p);

				// Check if new players have to be added
				for (MazePlayer p : pl)
					if (!players.contains(p))
						players.add(p);

				// Fianlly delete players. A separated step is needed to remove the risk of a
				// ConcurrentModificationException
				for (MazePlayer p : toDelete) {
					p.dispose();
					players.remove(p);
				}
				toDelete.clear();
			}
		} else {
			for (MazePlayer p : players)
				p.dispose();
			players.clear();
			players.addAll(pl);
		}

		for (int i = 0; i < MazeSettings.MAZEX; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < MazeSettings.MAZEZ; k++) {
					main.world.worldManager.setCell(i, j, k, CellId.ID_AIR);
				}
			}
		}

		mazeGen.setMazeSize(MazeSettings.MAZEX, MazeSettings.MAZEZ);
		mazeGen.generateMaze();

		if (type != GameType.CLIENT) {
			spreadPlayers();
			mazeGen.setupEndPoint();
			powerups.clear();
			spawnPowerUps();
		}

		if (todraw != null && showGame == true) {
			mazeGen.show(todraw);
		}

		resetCamera();

		gameStarted = true;

		stage.clear();
		if (AMazeIng.PLATFORM == Platform.ANDROID)
			for (MazePlayer p : players) {
				if (p instanceof MazePlayerLocal)
					stage.addActor(((MazePlayerLocal) p).tctrl);
					stage.addActor(((MazePlayerLocal) p).touchpadPowerUp);
			}

		AMazeIng.getMain().multiplexer.addProcessor(stage);
	}

	public void update() {
		main.currentGameManager = this;

		if (gameStarted && !anyoneWon) {
			pup = null;

			if (getShowGame()) {
				main.world.render();
				resetCamera();
				setCamera(new Vector3(mazeGen.w / 2, (MazeSettings.MAZEX + MazeSettings.MAZEZ) * 0.45f, mazeGen.h / 2),
						new Vector3(0, -90, 0));

				stage.act();
				stage.draw();

			}

			main.world.modelBatch.begin(main.world.cam);
			if (getShowGame())
				for (PowerUp p : powerups)
					p.render(main.world.modelBatch, main.world.environment);

			if (players != null) {
				for (MazePlayer p : players) {
					if (!p.isDisposed()) {
						// Check if there's a power-up in the same spot, if so give it to the player
						for (PowerUp p1 : powerups)
							if ((int) p1.getPosition().x == (int) p.getPos().x
									&& (int) p1.getPosition().z == (int) p.getPos().z) {
								pup = p1;
								p.currentPowerUp = pup;
								break;
							}
						if (pup != null)
							powerups.remove(pup);

						if (getShowGame())
							p.render(main.world.modelBatch, main.world.environment);
					}

					anyoneWon = false;
					if (type != GameType.CLIENT) {
						if (checkWin(p)) {
							anyoneWon = true;
							gameStarted = false;
							break;
						}
					}
				}
			}

			if (anyoneWon) {
				System.out.println("Game Finished! " + type);
				if (type == GameType.LOCAL) {
					main.setScreen(main.uiManager.playersScreen);
				} else if (type == GameType.SERVER) {

					((PreGameScreen) main.uiManager.preGameScreen).setGameType(GameType.SERVER);
					main.setScreen(main.uiManager.preGameScreen);
				}
			}

			main.world.modelBatch.end();
		}
	}

	public void spreadPlayers() {
		for (MazePlayer p : players) {
			int x = 1, z = 1;
			do {
				x = (Math.abs(rand.nextInt() - 1) % (mazeGen.w));
				z = (Math.abs(rand.nextInt() - 1) % (mazeGen.h));
//				System.out.println(
//						thereIsPlayerInPos(x, z) + " - " + mazeGen.occupiedSpot(x, z) + " --- " + x + ", " + z);
			} while (thereIsPlayerInPos(x, z) || mazeGen.occupiedSpot(x, z));
			p.setPlaying();
			p.setPos(x + 0.5f, 2f, z + 0.5f);
		}
	}

	public void spawnPowerUps() {

		for (int i = 0; i < MazeSettings.START_POWERUPS; i++) {
			PowerUp p = PowerUps.pickRandomPU();
			int x = 1, z = 1;
			do {
				x = (Math.abs(rand.nextInt() - 1) % (mazeGen.w));
				z = (Math.abs(rand.nextInt() - 1) % (mazeGen.h));
			} while (thereIsPlayerInPos(x, z) || mazeGen.occupiedSpot(x, z) || thereIsPowerUpInPos(x, z));
			p.setPosition(x + 0.5f, 1.25f, z + 0.5f);
			powerups.add(p);
			System.out.println("Spawning power-up in " + x + ", " + z);
		}

	}

	Player generateNewPlayer(int kup, int kdown, int ksx, int kdx) {
		return generateNewPlayer(kup, kdown, ksx, kdx, "");
	}

	Player generateNewPlayer(int kup, int kdown, int ksx, int kdx, String name) {
		int x, z;
		do {
			x = (Math.abs(rand.nextInt() - 1) % (mazeGen.w));
			z = (Math.abs(rand.nextInt() - 1) % (mazeGen.h));
		} while (thereIsPlayerInPos(x, z) || mazeGen.occupiedSpot(x, z));
		if (name.equalsIgnoreCase(""))
			return new Player(kup, kdown, ksx, kdx, x + 0.5f, 4f, z + 0.5f);
		else
			return new Player(kup, kdown, ksx, kdx, x + 0.5f, 4f, z + 0.5f, name);
	}

	public boolean checkWin(MazePlayer p) {
		if ((int) p.getPos().x == mazeGen.WINX && (int) p.getPos().z == mazeGen.WINZ) {
			System.out.println(p.getName() + " won");
			return true;
		}
		return false;
	}

	public boolean thereIsPlayerInPos(int x, int z) {
		for (MazePlayer p : players) {
			if ((int) p.getPos().x == x || (int) p.getPos().z == z)
				return true;
		}
		return false;
	}

	public boolean thereIsPowerUpInPos(int x, int z) {
		for (PowerUp p : powerups) {
			if ((int) p.getPosition().x == x || (int) p.getPosition().z == z)
				return true;
		}
		return false;
	}

	public boolean areTherePlayersNearby(int x, int z, int range) {
		int i, k;
		for (MazePlayer p : players) {
			i = (int) p.getPos().x;
			k = (int) p.getPos().z;

			if ((x - i) * (x - i) + (k - z) * (k - z) <= range * range)
				return true;
		}
		return false;
	}

	public MazePlayer getRandomPlayer() {
		return players.get(Math.abs(rand.nextInt() % players.size()));
	}

	public void requestChangeToMap(int[][] todraw) {
		mazeGen.requestChangeToMap(todraw);
	}

	public void resetCamera() {
		main.world.cam.position.set(0f, 0f, 0.5f); // Set cam position at origin
		main.world.cam.lookAt(0, 0, 0); // Direction to look at, for setting direction (perhaps better to set manually)
//		 main.world.cam.near = 1f;            // Minimum distance visible
//		 main.world.cam.far = 300f;            // Maximum distance visible
		main.world.cam.up.set(0f, 1f, 0f); // Up is in y positive direction
		main.world.cam.view.idt(); // Reset rotation matrix
		main.world.cam.update();
	}

	public void setCamera(Vector3 position, Vector3 rotation) {
		main.world.cam.translate(position); // set cam absolute position
		main.world.cam.rotate(rotation.x, 0f, 1f, 0f); // set cam absolute rotation on axis X
		main.world.cam.rotate(rotation.y, 1f, 0f, 0f); // set cam absolute rotation on axis Y
		main.world.cam.rotate(rotation.z, 0f, 0f, 1f); // set cam absolute rotation on axis Z
		main.world.cam.update();
	}

	boolean getShowGame() {
		return showGame;
	}

	void setShowGame(boolean g) {
		showGame = g;
	}

	public void generateMaze() {
		generateMaze(null, null);
	}

	public void generateMaze(int todraw[][]) {
		generateMaze(null, todraw);
	}

	public void generateMaze(Set<MazePlayer> pl) {
		generateMaze(pl, null);
	}

	public void dispose() {
		for (MazePlayer p : players)
			if (!p.isDisposed())
				p.dispose();
		players.clear();
	}
}
