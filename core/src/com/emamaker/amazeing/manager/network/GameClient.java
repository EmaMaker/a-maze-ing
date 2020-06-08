package com.emamaker.amazeing.manager.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.managers.GameManagerClient;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.emamaker.amazeing.manager.network.action.actions.client.game.NACGameStatusUpdate;
import com.emamaker.amazeing.manager.network.action.actions.client.game.NACServerClosed;
import com.emamaker.amazeing.manager.network.action.actions.client.game.NACUpdateMap;
import com.emamaker.amazeing.manager.network.action.actions.client.login.NACLoginAO;
import com.emamaker.amazeing.manager.network.action.actions.client.login.NACLoginAO2;
import com.emamaker.amazeing.manager.network.action.actions.client.login.NACRemovePlayer;
import com.emamaker.amazeing.manager.network.action.actions.client.player.NACUpdateOtherPlayerPos;
import com.emamaker.amazeing.manager.network.action.actions.client.player.NACUpdatePlayerPosForced;
import com.emamaker.amazeing.manager.network.action.actions.client.player.NACUpdatePlayersPos;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.emamaker.amazeing.player.PlayerUtils;
import com.esotericsoftware.kryonet.Client;

public class GameClient extends NetworkHandler {

	public Client client;
	String addr;

	boolean updateMobilePlayers = false;

	public CopyOnWriteArrayList<String> localPlayers = new CopyOnWriteArrayList<String>();

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
			client.connect(5000, addr, port, port + 1);

			gameManager = new GameManagerClient();

			if (AMazeIng.isMobile())
				updateMobilePlayers = true;

			registerActions();
			startDefaultActions();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void registerActions() {
		super.registerActions();
		actions.add(new NACLoginAO(this));
		actions.add(new NACLoginAO2(this));
		actions.add(new NACRemovePlayer(this));
		actions.add(new NACGameStatusUpdate(this));
		actions.add(new NACUpdateMap(this));
		actions.add(new NACUpdatePlayerPosForced(this));
		actions.add(new NACUpdatePlayersPos(this));
		actions.add(new NACUpdateOtherPlayerPos(this));
		actions.add(new NACServerClosed(this));
	}

	@Override
	public void startDefaultActions() {
		getActionByClass(NACUpdatePlayersPos.class).startAction(null, null);
	}

	@Override
	public void update() {
		super.update();

		if (isRunning()) {
			if (gameManager != null) {
				if (gameManager.gameStarted) {
				} else {
					checkForNewPlayers();
				}
			}
		}
	}

	@Override
	public boolean startGame() {
		if (!players.isEmpty()) {
			this.gameManager.generateMaze(new HashSet<MazePlayer>(players.values()));

			return true;
		}
		return false;
	}

	@Override
	public void stop() {
		if (isRunning()) {
			for (String s : localPlayers) {
				((NACRemovePlayer) getActionByClass(NACRemovePlayer.class)).startAction(null, null, s);
			}

			for (MazePlayer p : players.values())
				p.dispose();
			players.clear();

			for (NetworkAction n : pendingActions)
				n.onParentClosing();

			while (!pendingActions.isEmpty())
				update();

			client.stop();
			running = false;
//			}

		}
	}

	/* CHECKING FOR NEW PLAYERS */
	MazePlayerLocal p;
	public ArrayList<MazePlayerLocal> localPlrQueue = new ArrayList<MazePlayerLocal>();

	private void checkForNewPlayers() {
		checkForNewPlayersDesktop();
		checkForNewPlayersMobile();
	}

	private void checkForNewPlayersDesktop() {
		if (AMazeIng.isDesktop()) {
			// Search for keyboard players (WASD and ARROWS) on Desktop

			if (PlayerUtils.wasdJustPressed()) {
				p = PlayerUtils.getPlayerWithKeys(new HashSet<>(players.values()), PlayerUtils.WASDKEYS);
				if (p != null) {
					((NACRemovePlayer) getActionByClass(NACRemovePlayer.class)).startAction(null, null, p.uuid);
				} else {
					localPlrQueue.add(new MazePlayerLocal(PlayerUtils.WASDKEYS));
					getActionByClass(NACLoginAO.class).startAction(null, null);
				}
			}

			if (PlayerUtils.arrowsJustPressed()) {
				p = PlayerUtils.getPlayerWithKeys(new HashSet<>(players.values()), PlayerUtils.ARROWKEYS);
				if (p != null) {
					((NACRemovePlayer) getActionByClass(NACRemovePlayer.class)).startAction(null, null, p.uuid);
				} else {
					localPlrQueue.add(new MazePlayerLocal(PlayerUtils.ARROWKEYS));
					getActionByClass(NACLoginAO.class).startAction(null, null);
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
						// if the player wasn't there before, but wants to join: add it
						if (p == null) {
							localPlrQueue.add(new MazePlayerLocal(new Touchpad(0f, main.uiManager.skin), i));
							getActionByClass(NACLoginAO.class).addToQueue();
						}
					} else {
						// The player was there before, but has left: remove it
						if (p != null && players.containsValue(p)) {
							((NACRemovePlayer) getActionByClass(NACRemovePlayer.class)).startAction(null, null, p.uuid);
						}
						// Otherwise just do nothing
					}
				}
				updateMobilePlayers = false;
			}
		}
	}

	public void setUpdateMobilePlayers() {
		updateMobilePlayers = true;
	}

}
