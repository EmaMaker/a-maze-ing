package com.emamaker.amazeing.manager.network.action.actions.client.player;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerPosition;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NACUpdateOtherPlayerPos extends NetworkAction {

	protected NACUpdateOtherPlayerPos(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NACUpdateOtherPlayerPos(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.UpdatePlayerPosition(), null, null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		String uuid = ((UpdatePlayerPosition) incomingMsg).uuid;
		float px = ((UpdatePlayerPosition) incomingMsg).px;
		float py = ((UpdatePlayerPosition) incomingMsg).py;
		float pz = ((UpdatePlayerPosition) incomingMsg).pz;
		if (parent.players.containsKey(uuid) && !client().localPlayers.contains(uuid)
				&& parent.gameManager.gameStarted) {
			parent.players.get(uuid).setPos(px, py, pz);
		}
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACUpdateOtherPlayerPos(client(), incomingConnection, incomingMsg, responsePacket, endPacket,
				oneTime);
	}

}
