package com.emamaker.amazeing.manager.network.action.actions.client.powerup;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpUseRequest;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpUseRequestOk;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NACPowerUpUseRequest extends NetworkAction {

	String uuid, pupname;

	protected NACPowerUpUseRequest(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime, String uuid_, String pupname_) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		this.uuid = uuid_;
		this.pupname = pupname_;
	}

	public NACPowerUpUseRequest(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.PowerUpUseRequest(), new NetworkCommon.PowerUpUseRequestOk(), false);
	}

	/*
	 * Here startAction needs to be overrided to allow for the uuid argument to be
	 * passed from one instance to another. This also removes the alreadyPending statement instead of setting 
	 * the oneAtTheTie flag.
	 */
	public void startAction(String u_, String p_) {
		this.startAction(incomingConnection, incomingMsg, u_, p_);
	}

	public void startAction(Connection conn, Object msg, String uuid_, String pupname_) {
		this.incomingConnection = conn;
		this.incomingMsg = msg;
		this.uuid = uuid_;
		this.pupname = pupname_;

		parent.addToPending(newInstance());
	}

	@Override
	public void resolveAction() {
		super.resolveAction();
		((PowerUpUseRequest) responsePacket).uuid = uuid;
		((PowerUpUseRequest) responsePacket).pupname = pupname;
		client().client.sendUDP(responsePacket);
	}

	/*
	 * We also have to override the responseReceived package, because this action
	 * has to finish only if the correct uuid has been received
	 */
	public void responseReceived(Connection conn, Object msg) {
		this.endConnection = conn;
		this.endMsg = msg;
		if ( endMsg == null || ((PowerUpUseRequestOk) endMsg).uuid.equals(uuid)) {
			if (endMsg != null && ((PowerUpUseRequestOk) endMsg).canuse) {
				parent.players.get(uuid).usePowerUp();
				System.out.println("using powerup " + pupname + " on " + uuid);
			}
				detachFromParent();
		}
	}

	@Override
	public NetworkAction newInstance() {
		return new NACPowerUpUseRequest(client(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime, uuid, pupname);
	}

}
