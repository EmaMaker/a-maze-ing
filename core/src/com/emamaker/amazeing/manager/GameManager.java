package com.emamaker.amazeing.manager;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.maze.MazeGenerator;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.voxelengine.block.CellId;
import com.emamaker.voxelengine.player.Player;

public class GameManager {

	/* Local manager for local games and server host in multiplayer games */

	public MazeGenerator mazeGen;
	public boolean gameStarted = false;
	Random rand = new Random();
	public GameServer server;
	public GameClient client;
	AMazeIng main;

	ArrayList<MazePlayer> players = new ArrayList<MazePlayer>();

	public GameManager(Game main_) {
		main = (AMazeIng) main_;
		gameStarted = false;
		server = new GameServer(main);
		client = new GameClient(main);
		// Maze Generation
		mazeGen = new MazeGenerator(main, MazeSettings.MAZEX, MazeSettings.MAZEZ);
	}

	boolean anyoneWon = false;

	public void update() {
		server.update();
		client.update();
		
		if (gameStarted) {
			main.world.cam.position.set(mazeGen.w / 2, (MazeSettings.MAZEX + MazeSettings.MAZEZ) * 0.45f,
					mazeGen.h / 2);
			main.world.cam.lookAt(MazeSettings.MAZEX / 2, 0, MazeSettings.MAZEX / 2);
			main.world.cam.update();
			main.world.render();

			main.world.modelBatch.begin(main.world.cam);
			if (players != null) {
				for (MazePlayer p : players) {
					p.render(main.world.modelBatch, main.world.environment);
					anyoneWon = false;
					if (checkWin(p)) {
						anyoneWon = true;
						gameStarted = false;
						break;
					}
				}
			}

			if (anyoneWon)
				main.setScreen(main.uiManager.playersScreen);
			main.world.modelBatch.end();
		}
	}

	ArrayList<MazePlayer> toDelete = new ArrayList<MazePlayer>();
	public void generateMaze(Set<MazePlayer> pl, int todraw[][]) {
		main.setScreen(null);
		anyoneWon = false;

		// Only add new players and dispose the old ones
		//Check if actually there are players to be deleted
		if(pl != null) {
			for (MazePlayer p : players)
				if (!pl.contains(p))
					toDelete.add(p);
			
			//Check if new players have to be added
			for (MazePlayer p : pl)
				if (!players.contains(p))
					players.add(p);
			
			//Fianlly delete players. A separated step is needed to remove the risk of a ConcurrentModificationException
			for(MazePlayer p : toDelete) {
				p.dispose();
				players.remove(p);
			}
			toDelete.clear();
		}
//		destroyPlayers();
//		players.addAll(p);

		for (int i = 0; i < MazeSettings.MAZEX; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < MazeSettings.MAZEZ; k++) {
					main.world.worldManager.setCell(i, j, k, CellId.ID_AIR);
				}
			}
		}

		mazeGen.setMazeSize(MazeSettings.MAZEX, MazeSettings.MAZEZ);
		mazeGen.generateMaze();
		
		spreadPlayers();
		mazeGen.setupEndPoint();
		
		if(todraw != null) {
		mazeGen.show(todraw);
		}
		
		
		gameStarted = true;
	}

	public void spreadPlayers() {
		for (MazePlayer p : players) {
			int x = 1, z = 1;
			do {
				x = (Math.abs(rand.nextInt() - 1) % (mazeGen.w));
				z = (Math.abs(rand.nextInt() - 1) % (mazeGen.h));
//				 System.out.println(thereIsPlayerInPos(x, z) + " - " + mazeGen.occupiedSpot(x,
//				 z) + " --- " + x + ", " + z);
				// while there's a wall in current location pick new location
			} while (thereIsPlayerInPos(x, z) || mazeGen.occupiedSpot(x, z));
			p.setPlaying();
			p.setPos(x + 0.5f, 2f, z + 0.5f);
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
			// System.out.println(thereIsPlayerInPos(x, z) + " - " + mazeGen.occupiedSpot(x,
			// z) + " --- " + x + ", " + z);
			// while there's a wall in current location pick new location
		} while (thereIsPlayerInPos(x, z) || mazeGen.occupiedSpot(x, z));
		if (name.equalsIgnoreCase(""))
			return new Player(kup, kdown, ksx, kdx, x + 0.5f, 4f, z + 0.5f);
		else
			return new Player(kup, kdown, ksx, kdx, x + 0.5f, 4f, z + 0.5f, name);
	}

//	public void destroyPlayers() {
//		for (MazePlayer p : players)
//			p.dispose();
//		players.clear();
//	}

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
		client.stop();
		server.stop();
		for (MazePlayer p : players)
			p.dispose();
	}

}
