package com.emamaker.amazeing.manager.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.managers.GameManagerClient;
import com.emamaker.amazeing.manager.network.NetworkCommon.AddNewPlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO2;
import com.emamaker.amazeing.manager.network.NetworkCommon.RemovePlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.StartGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerTransform;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerTransformServer;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.emamaker.amazeing.player.MazePlayerRemote;
import com.emamaker.amazeing.player.PlayerUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;

public class GameClient extends NetworkHandler {

	Client client;
	String addr;

	boolean updateMobilePlayers = false;

	boolean startGame = false;
	String map = "";

	ArrayList<String> localPlayers = new ArrayList<String>();

	// Returns true if the server started successfully
	public boolean start(String addr_, int port_) {
		addr = addr_;
		port = port_;
		running = true;
		try {
			client = new Client();

			// For consistency, the classes to be sent over the network are
			// registered by the same method for both the client and server.
			NetworkCommon.register(client);
			client.start();
			client.addListener(connectionListener);
			client.connect(5000, addr, port, port + 1);

			gameManager = new GameManagerClient();

			if (AMazeIng.isMobile())
				updateMobilePlayers = false;

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean startGame() {
		return false;
	}

	@Override
	public void stop() {
		if (running) {
			for (String s : players.keySet()) {
				if (players.get(s) instanceof MazePlayerLocal) {
					RemovePlayer request = new RemovePlayer();
					request.uuid = s;
					client.sendTCP(request);
				}
				players.get(s).dispose();
			}

			players.clear();

			client.stop();
			running = false;
		}
	}

	@Override
	public void onLoginAO(Connection c) {
	}

	@Override
	public void onLoginAO2(Connection c) {
		String uuid = ((LoginAO2) message).uuid;
		System.out.println(
				"Server has responded with uuid " + uuid + ", assigning it to the first local player in queue");

		// Accept uuid
		if (!localPlrQueue.isEmpty()) {
			players.put(uuid, localPlrQueue.get(0));
			players.get(uuid).uuid = uuid;
			localPlrQueue.remove(0);
			localPlayers.add(uuid);

			// Resend message to notify that uuid has been accepted
			client.sendTCP(message);
		}
	}

	@Override
	public void onConnectionRefused(Connection c) {
	}

	@Override
	public void onAddNewPlayer(Connection c) {
		String uuid = ((AddNewPlayer) message).uuid;
		if (!players.containsKey(uuid)) {
			MazePlayerRemote player = new MazePlayerRemote(uuid);
			players.put(uuid, player);
		}
	}

	@Override
	public void onRemovePlayer(Connection c) {
		String uuid = ((RemovePlayer) message).uuid;
		// Remove the player from the server
		if (players.containsKey(uuid)) {
			players.remove(uuid);
			System.out.println("Player with UUID " + uuid + " is leaving the game :(");
		}
	}

	@Override
	public void onUpdateTransform(Connection c) {
		String uuid = ((UpdatePlayerTransform) message).uuid;
		if (players.containsKey(uuid) && !localPlayers.contains(uuid)) {

			System.out.println("Updating player with uuid " + uuid);
			players.get(uuid).setPos(((UpdatePlayerTransform) message).tx, ((UpdatePlayerTransform) message).ty,
					((UpdatePlayerTransform) message).tz);
		}
	}

	@Override
	public void onUpdateTransformServer(Connection c) {
		String uuid = ((UpdatePlayerTransformServer) message).uuid;
		if (players.containsKey(uuid)) {
			players.get(uuid).setPos(((UpdatePlayerTransformServer) message).tx,
					((UpdatePlayerTransformServer) message).ty, ((UpdatePlayerTransformServer) message).tz);
		}
	}

	@Override
	public void onStartGame(Connection c) {
		startGame = true;
		map = ((StartGame) message).map;
	}

	@Override
	public void onEndGame(Connection c) {
		gameManager.setFinished();
	}

	@Override
	public void onUpdateMap(Connection c) {
	}

	@Override
	public void onUpdateSettings(Connection c) {
	}

	@Override
	public void onConnected(Connection c) {
	}

	@Override
	public void update() {
		super.update();
		if (gameManager != null) {
			if (!gameManager.gameStarted) {
				checkForNewPlayers();

				if (startGame) {
					gameManager.generateMaze(new HashSet<MazePlayer>(players.values()));
					startGame = false;
				}
			} else {
				if (!gameManager.anyoneWon) {
					if (!map.equals("")) {
						gameManager.mazeGen.show(gameManager.mazeGen.runLenghtDecode(map));
						map = "";
					}
					for (String s : players.keySet())
						if (localPlayers.contains(s))
							updateLocalPlayerToServer((MazePlayerLocal) players.get(s));
				}
			}
		}
	}

	/* CHECKING FOR NEW PLAYERS */
	MazePlayerLocal p;
	ArrayList<MazePlayerLocal> localPlrQueue = new ArrayList<MazePlayerLocal>();

	private void checkForNewPlayers() {
		checkForNewPlayersDesktop();
		checkForNewPlayersMobile();
	}

	private void checkForNewPlayersDesktop() {
		if (AMazeIng.isDesktop()) {
			// Search for keyboard players (WASD and ARROWS) on Desktop
			if (PlayerUtils.wasdPressed()) {
				p = PlayerUtils.getPlayerWithKeys(new HashSet<>(players.values()), PlayerUtils.WASDKEYS);
				if (p != null) {
					RemovePlayer msg = new RemovePlayer();
					msg.uuid = p.uuid;
					client.sendTCP(msg);
				} else {
					localPlrQueue.add(new MazePlayerLocal(PlayerUtils.WASDKEYS));
					client.sendTCP(new LoginAO());
				}
			}

			if (PlayerUtils.arrowsPressed()) {
				p = PlayerUtils.getPlayerWithKeys(new HashSet<>(players.values()), PlayerUtils.ARROWKEYS);
				if (p != null) {
					RemovePlayer msg = new RemovePlayer();
					msg.uuid = p.uuid;
					client.sendTCP(msg);
				} else {
					localPlrQueue.add(new MazePlayerLocal(PlayerUtils.ARROWKEYS));
					client.sendTCP(new LoginAO());
				}
			}
		}
	}

	private void checkForNewPlayersMobile() {
		if (AMazeIng.isMobile()) {
			// Search for mobile players
			if (updateMobilePlayers) {
				for (int i = 0; i < MazeSettings.MAXPLAYERS; i++) {
					p = PlayerUtils.getPlayerWithTouchCtrl(i, new HashSet<>(players.values()));
					if (i < MazeSettings.MAXPLAYERS_MOBILE) {
						// if yhe player wasn't there before, but wants to join: add it
						if (p == null) {
							localPlrQueue.add(new MazePlayerLocal(new Touchpad(0f, main.uiManager.skin), i));
							client.sendTCP(new NetworkCommon.LoginAO());
						}
					} else {
						// The player was there before, but has left: remove it
						if (p != null && players.containsValue(p)) {
							NetworkCommon.RemovePlayer msg = new NetworkCommon.RemovePlayer();
							msg.uuid = p.uuid;
							client.sendTCP(msg);
						}
						// Otherwise just do nothing
					}
				}
				updateMobilePlayers = false;
			}
		}
	}

	@Override
	public void onAddPowerUp(Connection c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemovePowerUp(Connection c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAssignPowerUp(Connection c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartUsingPowerUp(Connection c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndUsingPowerUp(Connection c) {
	}

	public void updateLocalPlayerToServer(MazePlayerLocal p) {
		if (this.gameManager != null && this.gameManager.gameStarted) {
			UpdatePlayerTransform pu = new UpdatePlayerTransform();
			Vector3 pos = p.ghostObject.getWorldTransform().getTranslation(new Vector3());
			Quaternion rot = p.ghostObject.getWorldTransform().getRotation(new Quaternion());
			pu.tx = pos.x;
			pu.ty = pos.y;
			pu.tz = pos.z;
			pu.rx = rot.x;
			pu.ry = rot.y;
			pu.rz = rot.z;
			pu.rw = rot.w;
			pu.uuid = p.uuid;

			client.sendUDP(pu);
		}
	}

}
