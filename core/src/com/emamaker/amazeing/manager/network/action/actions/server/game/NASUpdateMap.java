package com.emamaker.amazeing.manager.network.action.actions.server.game;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateMap;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NASUpdateMap extends NetworkAction {

	protected NASUpdateMap(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NASUpdateMap(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.UpdateMap(), null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();
		((UpdateMap) responsePacket).map = server().gameManager.mazeGen.runLenghtEncode();
		server().server.sendToAllUDP(responsePacket);
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NASUpdateMap(server(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
