package com.emamaker.amazeing.manager.network.action.actions.server.powerup;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpRevokeRequest;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpRevokeRequestOk;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NASPowerUpRevokeRequest extends NetworkAction {

	String uuid;

	protected NASPowerUpRevokeRequest(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime, String uuid_) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		this.uuid = uuid_;
	}

	public NASPowerUpRevokeRequest(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.PowerUpRevokeRequest(), new NetworkCommon.PowerUpRevokeRequestOk(),
				false);
	}

	/*
	 * Here startAction needs to be overrided to allow for the uuid argument to be
	 * passed from one instance to another. This also removes the alreadyPending
	 * statement instead of setting the oneAtTheTie flag.
	 */
	public void startAction(String u_) {
		this.startAction(incomingConnection, incomingMsg, u_);
	}

	public void startAction(Connection conn, Object msg, String uuid_) {
		this.incomingConnection = conn;
		this.incomingMsg = msg;
		this.uuid = uuid_;

		parent.addToPending(newInstance());
	}

	@Override
	public void resolveAction() {
		super.resolveAction();
		((PowerUpRevokeRequest) responsePacket).uuid = uuid;
		System.out.println("server: Revoking powerup for " + uuid);
		parent.players.get(uuid).disablePowerUp();
		server().server.sendToAllUDP(responsePacket);
	}

	/*
	 * We also have to override the responseReceived package, because this action
	 * has to finish only if the correct uuid has been received
	 */
	public void responseReceived(Connection conn, Object msg) {
		this.endConnection = conn;
		this.endMsg = msg;
		if (endMsg == null || ((PowerUpRevokeRequestOk) endMsg).uuid.equals(uuid)) {
			detachFromParent();
		}
	}

	@Override
	public NetworkAction newInstance() {
		return new NASPowerUpRevokeRequest(server(), incomingConnection, incomingMsg, responsePacket, endPacket,
				oneTime, uuid);
	}

}
