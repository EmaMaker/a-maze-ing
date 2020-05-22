package com.emamaker.amazeing.manager.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import com.badlogic.gdx.math.Vector3;
import com.emamaker.amazeing.manager.managers.GameManagerServer;
import com.emamaker.amazeing.manager.network.NetworkCommon.AddNewPlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.AddPowerUp;
import com.emamaker.amazeing.manager.network.NetworkCommon.EndGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO2;
import com.emamaker.amazeing.manager.network.NetworkCommon.RemovePlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.RemovePowerUp;
import com.emamaker.amazeing.manager.network.NetworkCommon.StartGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateMap;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerTransform;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerRemote;
import com.emamaker.amazeing.player.powerups.PowerUp;
import com.emamaker.amazeing.utils.MathUtils;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class GameServer extends NetworkHandler {

	Server server;

	// Returns true if the server started successfully
	public boolean start(int port_) {
		port = port_;
		running = true;
		try {
			server = new Server();
			// For consistency, the classes to be sent over the network are
			// registered by the same method for both the client and server.
			NetworkCommon.register(server);
			server.addListener(connectionListener);
			server.bind(port, port + 1);
			server.start();

			gameManager = new GameManagerServer();

			System.out.println("Server registered and running on port " + port);
			return true;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onLoginAO(Connection c) {
		if (players.size() < MazeSettings.MAXPLAYERS) {
			String uuid = UUID.randomUUID().toString();
			System.out.println("Client requested adding new player, giving it uuid " + uuid);
			LoginAO2 response = new LoginAO2();
			response.uuid = uuid;
			c.sendTCP(response);
		}
	}

	@Override
	public void onLoginAO2(Connection c) {
		String uuid = ((LoginAO2) message).uuid;
		if (!players.containsKey(uuid)) {
			MazePlayerRemote player = new MazePlayerRemote(uuid);
			players.put(uuid, player);

			// Update everyone about the new player
			AddNewPlayer response = new AddNewPlayer();
			response.uuid = uuid;
			server.sendToAllTCP(response);
		}
	}

	@Override
	public void onConnectionRefused(Connection c) {
	}

	@Override
	public void onAddNewPlayer(Connection c) {
	}

	@Override
	public void onRemovePlayer(Connection c) {
		String uuid = ((RemovePlayer) message).uuid;
		// Remove the player from the server
		if (players.containsKey(uuid)) {
			players.remove(uuid);
			System.out.println("Player with UUID " + uuid + " is leaving the game :(");
			// And update everyone about it
			server.sendToAllTCP(message);
		}
	}

	Vector3 newPos = Vector3.Zero;

	@Override
	public void onUpdateTransform(Connection c) {
		String uuid = ((UpdatePlayerTransform) message).uuid;
		if (players.containsKey(uuid)) {
			// Check if the position is in a possible one, or if the player has teleported
			// from one spot to another
			newPos.set(((UpdatePlayerTransform) message).tx, ((UpdatePlayerTransform) message).ty,
					((UpdatePlayerTransform) message).tz);
			if (MathUtils.vectorDistance(players.get(uuid).getPos(), newPos) < 10) {
				players.get(uuid).setPos(newPos);
				server.sendToAllUDP(message);
			} else {
				server.sendToAllUDP(updatePlayer(uuid, players.get(uuid), true));
			}
		}
	}

	@Override
	public void onUpdateTransformServer(Connection c) {
	}

	@Override
	public void onStartGame(Connection c) {
	}

	@Override
	public void onEndGame(Connection c) {
	}

	@Override
	public void onUpdateMap(Connection c) {
	}

	@Override
	public void onUpdateSettings(Connection c) {
	}

	@Override
	public void stop() {
		for (MazePlayer p : players.values())
			p.dispose();
		players.clear();
		if (isRunning()) {
			main.client.stop();
			server.stop();
			running = false;
		}
	}

	@Override
	public void onConnected(Connection c) {
		System.out.println("New client connected, updating him about the others!");
		for (String s : players.keySet()) {
			AddNewPlayer request = new AddNewPlayer();
			request.uuid = s;
			c.sendTCP(request);
		}
	}

	@Override
	public void onAddPowerUp(Connection c) {
	}

	@Override
	public void onRemovePowerUp(Connection c) {
	}

	@Override
	public void onAssignPowerUp(Connection c) {
	}

	@Override
	public void onStartUsingPowerUp(Connection c) {
	}

	@Override
	public void onEndUsingPowerUp(Connection c) {
	}

	@Override
	public boolean startGame() {
		if (!players.isEmpty()) {
			this.gameManager.generateMaze(new HashSet<MazePlayer>(players.values()));
			StartGame response = new StartGame();
			response.map = this.gameManager.mazeGen.runLenghtEncode();
			server.sendToAllTCP(response);

			for (String s : players.keySet()) {
				Object pu = updatePlayer(s, players.get(s), true);
				server.sendToAllTCP(pu);
			}

			periodicGameUpdate();

			return true;
		}
		return false;
	}

	@Override
	public void update() {
		super.update();
		if (gameManager != null) {
			if (gameManager.anyoneWon)
				server.sendToAllUDP(new EndGame());
		}
	}

	@Override
	public void periodicGameUpdate() {
		UpdateMap response = new UpdateMap();
		response.map = gameManager.mazeGen.runLenghtEncode();
		server.sendToAllUDP(response);
		
		for (PowerUp p : gameManager.powerups) {
			AddPowerUp response1 = new AddPowerUp();
			response1.name = p.name;
			response1.x = p.getPosition().x;
			response1.z = p.getPosition().z;
			
			server.sendToAllUDP(response1);
		}
	}

	public void removePowerUp(PowerUp pup) {
		if(pup != null) {
			RemovePowerUp response = new RemovePowerUp();
			response.x =  pup.getPosition().z;
			response.z = pup.getPosition().z;
			server.sendToAllUDP(response);
		}
	}

}
