package com.emamaker.amazeing.manager.network.action.actions.client.powerup;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpRevokeRequest;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpRevokeRequestOk;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NACPowerUpRevokeRequestOk extends NetworkAction {

	protected NACPowerUpRevokeRequestOk(NetworkHandler parent, Connection c, Object incomingMsg_,
			Object responsePacket_, Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		setOneAtTheTime(false);
	}

	public NACPowerUpRevokeRequestOk(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.PowerUpRevokeRequest(), new NetworkCommon.PowerUpRevokeRequestOk(), null,
				true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		((PowerUpRevokeRequestOk) responsePacket).uuid = ((PowerUpRevokeRequest) incomingMsg).uuid;
		if (parent.players.containsKey(((PowerUpRevokeRequest) incomingMsg).uuid)) {
			parent.players.get(((PowerUpRevokeRequest) incomingMsg).uuid).disablePowerUp();
			System.out.println("client: Revoked powerup for "
					+ parent.players.get(((PowerUpRevokeRequest) incomingMsg).uuid).uuid);
		}
		client().client.sendUDP(responsePacket);

	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACPowerUpRevokeRequestOk(client(), incomingConnection, incomingMsg, responsePacket, endPacket,
				oneTime);
	}

}
