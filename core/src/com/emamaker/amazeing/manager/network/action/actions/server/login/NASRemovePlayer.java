package com.emamaker.amazeing.manager.network.action.actions.server.login;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PlayerRemoved;
import com.emamaker.amazeing.manager.network.NetworkCommon.RemovePlayer;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NASRemovePlayer extends NetworkAction {

	String uuid;

	protected NASRemovePlayer(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NASRemovePlayer(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.RemovePlayer(), new NetworkCommon.PlayerRemoved(), null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		uuid = ((RemovePlayer) incomingMsg).uuid;
		System.out.println("Removing player with uuid " + uuid);
		if (server().players.containsKey(uuid)) {
			server().players.get(uuid).dispose();
			server().players.remove(uuid);
			((PlayerRemoved) responsePacket).uuid = uuid;
			System.out.println("Removed player " + uuid);
		} else {
			System.out.println("That player is not here");
			((PlayerRemoved) responsePacket).uuid = null;
		}

		incomingConnection.sendUDP(responsePacket);
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NASRemovePlayer(server(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
