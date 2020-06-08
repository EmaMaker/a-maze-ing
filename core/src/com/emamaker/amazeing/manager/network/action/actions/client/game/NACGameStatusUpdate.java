package com.emamaker.amazeing.manager.network.action.actions.client.game;

import java.util.ArrayList;
import java.util.Arrays;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.GameStatusUpdate;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.emamaker.amazeing.player.MazePlayerRemote;
import com.esotericsoftware.kryonet.Connection;

public class NACGameStatusUpdate extends NetworkAction {

	protected NACGameStatusUpdate(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NACGameStatusUpdate(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.GameStatusUpdate(), null, null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		ArrayList<String> uuids = new ArrayList<String>(Arrays.asList(((GameStatusUpdate) incomingMsg).playersUUIDs));

		// Then check if older players have to be removed
		for (String s : client().players.keySet()) {
			if (!uuids.contains(s) && !client().localPlayers.contains(s)) {
				client().players.get(s).dispose();
				client().players.remove(s);
			}
		}

		// First of all check if new players have to be added to the game
		for (String s : uuids) {
			if (!client().players.containsKey(s) && !client().localPlayers.contains(s)) {
				client().players.put(s, new MazePlayerRemote(s));
			}
		}

		// Now check if the game has started. If so, start it
		if (client().gameManager.gameStarted != ((GameStatusUpdate) incomingMsg).gameStarted
				&& !((GameStatusUpdate) incomingMsg).anyoneWon)
			client().startGame();

		// Update the vars
		client().gameManager.gameStarted = ((GameStatusUpdate) incomingMsg).gameStarted;
		client().gameManager.anyoneWon = ((GameStatusUpdate) incomingMsg).anyoneWon;

	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACGameStatusUpdate(client(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
