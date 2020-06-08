package com.emamaker.amazeing.manager.network.action.actions.server.player;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateForcedPlayerPositionOk;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NASUpdatePlayerPosForced extends NetworkAction {

	String uuid;

	protected NASUpdatePlayerPosForced(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime, String uuid_) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		this.uuid = uuid_;
		server().setUpdatePos(uuid, false);
	}

	public NASUpdatePlayerPosForced(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.UpdateForcedPlayerPosition(), new NetworkCommon.UpdateForcedPlayerPositionOk(), false);
	}

	/*
	 * Here startAction needs to be overrided to allow for the uuid argument to be
	 * passed from one instance to another and to remove the alreadyPending
	 * limitation. Here we need to be able to call multiple instances of this at the
	 * same time
	 */
	public void startAction(String s_) {
		this.startAction(incomingConnection, incomingMsg);
	}

	public void startAction(Connection conn, Object msg, String uuid_) {
		this.incomingConnection = conn;
		this.incomingMsg = msg;
		this.uuid = uuid_;
//		System.out.println("Starting action: " + this);

		parent.addToPending(newInstance());
	}

	@Override
	public void resolveAction() {
		super.resolveAction();
		responsePacket = parent.updatePlayer(uuid, parent.players.get(uuid), true);
		server().server.sendToAllUDP(responsePacket);
	}

	/*
	 * We also have to override the responseReceived package, because this action
	 * has to finish only if the correct uuid has been received
	 */
	public void responseReceived(Connection conn, Object msg) {
		this.endConnection = conn;
		this.endMsg = msg;
		if (((UpdateForcedPlayerPositionOk) endMsg).uuid.equals(uuid)) {
			detachFromParent();
			server().setUpdatePos(uuid, true);
			System.out.println("Response received for " + this);
		}
	}

	@Override
	public NetworkAction newInstance() {
		return new NASUpdatePlayerPosForced(server(), incomingConnection, incomingMsg, responsePacket, endPacket,
				oneTime, uuid);
	}

}
