package com.emamaker.amazeing.manager.network.action.actions.server.game;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NASServerClosed extends NetworkAction {

	protected NASServerClosed(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NASServerClosed(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.ServerClosed(), null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();
		System.out.println("Server stopped?");
		server().server.sendToAllTCP(responsePacket);
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NASServerClosed(server(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
