package com.emamaker.amazeing.manager.network.action.actions.server.powerup;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpUseRequest;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpUseRequestOk;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NASPowerUpUseRequestOk extends NetworkAction {

	protected NASPowerUpUseRequestOk(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		setOneAtTheTime(false);
	}

	public NASPowerUpUseRequestOk(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.PowerUpUseRequest(), new NetworkCommon.PowerUpUseRequestOk(), null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		((PowerUpUseRequestOk) responsePacket).uuid = ((PowerUpUseRequest) incomingMsg).uuid;
		((PowerUpUseRequestOk) responsePacket).canuse = !parent.players
				.containsKey(((PowerUpUseRequest) incomingMsg).uuid)
				|| parent.players.get(((PowerUpUseRequest) incomingMsg).uuid).currentPowerUp == null ? false
						: parent.players.get(((PowerUpUseRequest) incomingMsg).uuid).currentPowerUp.name
								.equals(((PowerUpUseRequest) incomingMsg).pupname);

		if (((PowerUpUseRequestOk) responsePacket).canuse) {
			parent.players.get(((PowerUpUseRequest) incomingMsg).uuid).usePowerUp();
		} else {
			server().revokePowerUpRequest(parent.players.get(((PowerUpUseRequest) incomingMsg).uuid));
		}
		incomingConnection.sendUDP(responsePacket);

	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NASPowerUpUseRequestOk(server(), incomingConnection, incomingMsg, responsePacket, endPacket,
				oneTime);
	}

}
