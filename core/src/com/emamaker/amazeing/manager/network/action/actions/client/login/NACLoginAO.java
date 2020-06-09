package com.emamaker.amazeing.manager.network.action.actions.client.login;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NACLoginAO extends NetworkAction {

	protected NACLoginAO(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_, Object endPacket_,
			boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NACLoginAO(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.ClientLoginAO(), new NetworkCommon.ServerLoginUUID(), false);
	}
	
	
	@Override
	public void resolveAction() {
		super.resolveAction();
		client().client.sendUDP(responsePacket);
		System.out.println("Asking the server for uuid");
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACLoginAO(client(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}
	
}
