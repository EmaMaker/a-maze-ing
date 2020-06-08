package com.emamaker.amazeing.manager.network.action.actions.client.player;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.esotericsoftware.kryonet.Connection;

public class NACUpdatePlayersPos extends NetworkAction {

	protected NACUpdatePlayersPos(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		enableTimeout(false);
	}

	public NACUpdatePlayersPos(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.UpdatePlayerPosition(), null, false);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		if (parent.gameManager.gameStarted)
			for (String s : client().localPlayers) {
				if (parent.players.containsKey(s) && ((MazePlayerLocal) parent.players.get(s)).pressed) {
					responsePacket = parent.updatePlayer(s, parent.players.get(s), false);
					client().client.sendUDP(responsePacket);
				}
			}
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACUpdatePlayersPos(client(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}
	
}
