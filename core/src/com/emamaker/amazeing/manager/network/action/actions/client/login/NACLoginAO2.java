package com.emamaker.amazeing.manager.network.action.actions.client.login;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.ClientLoginAO2;
import com.emamaker.amazeing.manager.network.NetworkCommon.ServerLoginUUID;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NACLoginAO2 extends NetworkAction {

	String uuid;

	protected NACLoginAO2(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NACLoginAO2(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.ServerLoginUUID(), new NetworkCommon.ClientLoginAO2(),
				new NetworkCommon.ServerLoginAO2(), false);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();
		uuid = ((ServerLoginUUID) incomingMsg).uuid;
		System.out.println(
				"Server has responded with uuid " + uuid + ", assigning it to the first local player in queue");

		// Accept uuid
		if (!client().localPlrQueue.isEmpty() && !client().players.containsKey(uuid)) {
			client().players.put(uuid, client().localPlrQueue.get(0));
			client().players.get(uuid).uuid = uuid;

			client().localPlrQueue.remove(0);
			client().localPlayers.add(uuid);
			System.out.println("Added player");
		}
		((ClientLoginAO2) responsePacket).uuid = uuid;
		client().client.sendUDP(responsePacket);
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACLoginAO2(client(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
