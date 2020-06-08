package com.emamaker.amazeing.manager.network.action.actions.client.player;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateForcedPlayerPosition;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateForcedPlayerPositionOk;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NACUpdatePlayerPosForced extends NetworkAction {

	String uuid;

	protected NACUpdatePlayerPosForced(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NACUpdatePlayerPosForced(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.UpdateForcedPlayerPosition(), new NetworkCommon.UpdateForcedPlayerPositionOk(),
				null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		uuid = ((UpdateForcedPlayerPosition) incomingMsg).uuid;
		float px = ((UpdateForcedPlayerPosition) incomingMsg).px;
		float py = ((UpdateForcedPlayerPosition) incomingMsg).py;
		float pz = ((UpdateForcedPlayerPosition) incomingMsg).pz;
		if (parent.players.containsKey(uuid)) {
			parent.players.get(uuid).setPos(px, py, pz);
			((UpdateForcedPlayerPositionOk) responsePacket).uuid = uuid;
			if (client().localPlayers.contains(uuid)) {
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
		return new NACUpdatePlayerPosForced(client(), incomingConnection, incomingMsg, responsePacket, endPacket,
				oneTime);
	}

}
