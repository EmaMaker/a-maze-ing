package com.emamaker.amazeing.manager.network.action.actions.server.powerup;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpAssignRequest;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpAssignRequestOk;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NASPowerUpAssignRequest extends NetworkAction {

	String uuid, pupname;
	boolean immediateUse;
	
	protected NASPowerUpAssignRequest(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime, String uuid_, String pupname_, boolean immediateUse_) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		this.uuid = uuid_;
		this.pupname = pupname_;
		this.immediateUse = immediateUse_;
	}

	public NASPowerUpAssignRequest(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.PowerUpAssignRequest(), new NetworkCommon.PowerUpAssignRequestOk(), false);
	}

	/*
	 * Here startAction needs to be overrided to allow for the uuid argument to be
	 * passed from one instance to another. This also removes the alreadyPending
	 * statement instead of setting the oneAtTheTie flag.
	 */
	public void startAction(String u_, String p_, boolean immediateUse_) {
		this.startAction(incomingConnection, incomingMsg, u_, p_, immediateUse_);
	}

	public void startAction(Connection conn, Object msg, String uuid_, String pupname_, boolean immediateUse_) {
		this.incomingConnection = conn;
		this.incomingMsg = msg;
		this.uuid = uuid_;
		this.pupname = pupname_;
		this.immediateUse = immediateUse_;

		parent.addToPending(newInstance());
	}

	@Override
	public void resolveAction() {
		super.resolveAction();
		((PowerUpAssignRequest) responsePacket).uuid = uuid;
		((PowerUpAssignRequest) responsePacket).pupname = pupname;
		((PowerUpAssignRequest) responsePacket).immediateUse = immediateUse;
		System.out.println("requesting to assign powerup " + pupname + " to " + uuid);
		
		server().server.sendToAllUDP(responsePacket);
	}

	/*
	 * We also have to override the responseReceived package, because this action
	 * has to finish only if the correct uuid has been received
	 */
	public void responseReceived(Connection conn, Object msg) {
		this.endConnection = conn;
		this.endMsg = msg;
		if (endMsg == null || ((PowerUpAssignRequestOk) endMsg).uuid.equals(uuid)) 
			detachFromParent();
	}

	@Override
	public NetworkAction newInstance() {
		return new NASPowerUpAssignRequest(server(), incomingConnection, incomingMsg, responsePacket, endPacket,
				oneTime, uuid, pupname, immediateUse);
	}

}
