package com.emamaker.amazeing.manager.network;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.emamaker.amazeing.manager.managers.GameManagerServer;
import com.emamaker.amazeing.manager.network.action.actions.server.game.NASGameStatusUpdate;
import com.emamaker.amazeing.manager.network.action.actions.server.game.NASServerClosed;
import com.emamaker.amazeing.manager.network.action.actions.server.game.NASUpdateMap;
import com.emamaker.amazeing.manager.network.action.actions.server.login.NASLoginAO2;
import com.emamaker.amazeing.manager.network.action.actions.server.login.NASLoginUUID;
import com.emamaker.amazeing.manager.network.action.actions.server.login.NASRemovePlayer;
import com.emamaker.amazeing.manager.network.action.actions.server.player.NASUpdatePlayerPos;
import com.emamaker.amazeing.manager.network.action.actions.server.player.NASUpdatePlayerPosForced;
import com.emamaker.amazeing.player.MazePlayer;
import com.esotericsoftware.kryonet.Server;

public class GameServer extends NetworkHandler {

	public Server server;
	
	public ConcurrentHashMap<String, Boolean> positionUpdate = new ConcurrentHashMap<String, Boolean>();

	// Returns true if the server started successfully
	public boolean start(int port_) {
		port = port_;
		running = true;
		try {
			server = new Server();
			// For consistency, the classes to be sent over the network are
			// registered by the same method for both the client and server.
			NetworkCommon.register(server);
			server.bind(port, port + 1);
			server.start();

			gameManager = new GameManagerServer();

			registerActions();
			startDefaultActions();

			System.out.println("Server registered and running on port " + port);
			return true;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void registerActions() {
		super.registerActions();
		actions.add(new NASLoginUUID(this));
		actions.add(new NASLoginAO2(this));
		actions.add(new NASRemovePlayer(this));
		actions.add(new NASGameStatusUpdate(this));
		actions.add(new NASUpdateMap(this));
		actions.add(new NASUpdatePlayerPosForced(this));
		actions.add(new NASUpdatePlayerPos(this));
		actions.add(new NASServerClosed(this));
	}

	@Override
	public void startDefaultActions() {
		getActionByClass(NASGameStatusUpdate.class).startAction(null, null);
//		getActionByClass(NAServerUpdatePlayers.class).startAction(null, null);
	}

	@Override
	public void periodicNonGameUpdate() {
	}

	@Override
	public void periodicGameUpdate() {
		getActionByClass(NASUpdateMap.class).startAction(null, null);
	}

	@Override
	public boolean startGame() {
		System.out.println("Starting game. Players are: " + Arrays.toString(players.values().toArray()));
		if (!players.isEmpty()) {
			this.gameManager.generateMaze(new HashSet<MazePlayer>(players.values()));

			for (String s : players.keySet()) {
				((NASUpdatePlayerPosForced) getActionByClass(NASUpdatePlayerPosForced.class))
						.startAction(null, null, s);
			}

			return true;
		}
		return false;
	}

	@Override
	public void stop() {
		for (MazePlayer p : players.values())
			p.dispose();
		players.clear();
		if (isRunning()) {
			getActionByClass(NASServerClosed.class).startAction(null, null);
			server.stop();
			running = false;
		}
	}
	
	public boolean canUpdatePos(String u) {
		return positionUpdate.containsKey(u) && positionUpdate.get(u);
	}
	
	public void setUpdatePos(String uuid, boolean b) {
		if(positionUpdate.containsKey(uuid)) positionUpdate.replace(uuid, b);
		else positionUpdate.put(uuid, b);
	}

}
