package com.emamaker.amazeing.manager.network.action.actions.server.login;

import java.util.UUID;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.ClientLoginAO2;
import com.emamaker.amazeing.manager.network.NetworkCommon.ServerLoginUUID;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.esotericsoftware.kryonet.Connection;

public class NASLoginUUID extends NetworkAction {

	String uuid;
	
	protected NASLoginUUID(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_, Object endPacket_,
			boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}
	
	public NASLoginUUID(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.ClientLoginAO(), new NetworkCommon.ServerLoginUUID(), new NetworkCommon.ClientLoginAO2(), false);
	}
	
	
	@Override
	public void resolveAction() {
		super.resolveAction();

		if (server().players.size() < MazeSettings.MAXPLAYERS && !parent.gameManager.gameStarted) {
			uuid = UUID.randomUUID().toString();
			System.out.println("Client requested adding new player, giving it uuid " + uuid);
			((ServerLoginUUID)responsePacket).uuid = uuid;
			incomingConnection.sendUDP(responsePacket);
		}else {
			responseReceived(null, null);
		}
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NASLoginUUID(server(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}
	
}
