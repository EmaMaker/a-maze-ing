package com.emamaker.amazeing.manager.network;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.emamaker.amazeing.manager.managers.GameManagerServer;
import com.emamaker.amazeing.manager.network.action.actions.server.game.NASGameStatusUpdate;
import com.emamaker.amazeing.manager.network.action.actions.server.game.NASUpdateMap;
import com.emamaker.amazeing.manager.network.action.actions.server.login.NASLoginAO2;
import com.emamaker.amazeing.manager.network.action.actions.server.login.NASLoginUUID;
import com.emamaker.amazeing.manager.network.action.actions.server.login.NASRemovePlayer;
import com.emamaker.amazeing.manager.network.action.actions.server.player.NASUpdatePlayerPos;
import com.emamaker.amazeing.manager.network.action.actions.server.player.NASUpdatePlayerPosForced;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.MathUtils.Constants;
import com.esotericsoftware.kryonet.Server;

public class GameServer extends NetworkHandler {

	public Server server;

	public ConcurrentHashMap<String, Boolean> positionUpdate = new ConcurrentHashMap<String, Boolean>();

	// Returns true if the server started successfully
	public boolean start(int port_) {
		port = port_;
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

			running = true;
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
	}

	@Override
	public void startDefaultActions() {
		getActionByClass(NASGameStatusUpdate.class).startAction(null, null);
//		getActionByClass(NAServerUpdatePlayers.class).startAction(null, null);
	}

	@Override
	public void update() {
		super.update();
		
		if(isRunning()) {
			//Check if there's some player not responding that needs to be removed
			for(String s: players.keySet()) {
				if(System.currentTimeMillis() - players.get(s).LAST_NETWORK_TIME > Constants.COMMUNICATION_TIMEOUT_MILLIS) {
					players.get(s).dispose();
					players.remove(s);
				}
                System.out.println(Arrays.toString(players.values().toArray()));
			}
		}
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
				((NASUpdatePlayerPosForced) getActionByClass(NASUpdatePlayerPosForced.class)).startAction(null, null,
						s);
			}

			return true;
		}
		return false;
	}

	@Override
	public void stop() {
		if (isRunning()) {
			for (MazePlayer p : players.values()) {
				p.dispose();
				players.clear();
				server.stop();
				running = false;

				pendingActions.clear();
				todoActions.clear();
				actions.clear();
			}
		}
	}

	public boolean canUpdatePos(String u) {
		return positionUpdate.containsKey(u) && positionUpdate.get(u);
	}

	public void setUpdatePos(String uuid, boolean b) {
		if (positionUpdate.containsKey(uuid))
			positionUpdate.replace(uuid, b);
		else
			positionUpdate.put(uuid, b);
	}

}
