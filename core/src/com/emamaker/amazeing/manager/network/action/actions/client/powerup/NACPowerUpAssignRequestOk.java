package com.emamaker.amazeing.manager.network.action.actions.client.powerup;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpAssignRequest;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpAssignRequestOk;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.emamaker.amazeing.player.powerups.PowerUps;
import com.esotericsoftware.kryonet.Connection;

public class NACPowerUpAssignRequestOk extends NetworkAction {

	protected NACPowerUpAssignRequestOk(NetworkHandler parent, Connection c, Object incomingMsg_,
			Object responsePacket_, Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		setOneAtTheTime(false);
	}

	public NACPowerUpAssignRequestOk(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.PowerUpAssignRequest(), new NetworkCommon.PowerUpAssignRequestOk(), null,
				true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		((PowerUpAssignRequestOk) responsePacket).uuid = ((PowerUpAssignRequest) incomingMsg).uuid;
		if (parent.players.containsKey(((PowerUpAssignRequest) incomingMsg).uuid)) {
			parent.players.get(((PowerUpAssignRequest) incomingMsg).uuid).currentPowerUp = PowerUps.pickByName(((PowerUpAssignRequest) incomingMsg).pupname);
			System.out.println("Assigning powerup to " + ((PowerUpAssignRequest) incomingMsg).uuid);
			if(((PowerUpAssignRequest) incomingMsg).immediateUse) client().requestUsePowerUp(parent.players.get(((PowerUpAssignRequest) incomingMsg).uuid));
		}
		
		
		client().client.sendUDP(responsePacket);

	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACPowerUpAssignRequestOk(client(), incomingConnection, incomingMsg, responsePacket, endPacket,
				oneTime);
	}

}
