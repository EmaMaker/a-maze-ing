package com.emamaker.amazeing.manager.network.action.actions.server.login;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.ClientLoginAO2;
import com.emamaker.amazeing.manager.network.NetworkCommon.ServerLoginAO2;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.emamaker.amazeing.player.MazePlayerRemote;
import com.esotericsoftware.kryonet.Connection;

public class NASLoginAO2 extends NetworkAction {

	String uuid;
	
	protected NASLoginAO2(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_, Object endPacket_,
			boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}
	
	public NASLoginAO2(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.ClientLoginAO2(), new NetworkCommon.ServerLoginAO2(), null, true);
	}
	
	@Override
	public void resolveAction() {
		super.resolveAction();

		uuid = ((ClientLoginAO2) incomingMsg).uuid;
		if (!server().players.containsKey(uuid)) {
			System.out.println("Client accepted uuid " + uuid);
			MazePlayerRemote player = new MazePlayerRemote(uuid);
			server().players.put(uuid, player);
			server().players.get(uuid).LAST_NETWORK_TIME = System.currentTimeMillis();
			
			server().setUpdatePos(uuid, false);
		}
		((ServerLoginAO2)responsePacket).uuid = uuid;
		server().server.sendToAllUDP(responsePacket);
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NASLoginAO2(server(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}
	
}
