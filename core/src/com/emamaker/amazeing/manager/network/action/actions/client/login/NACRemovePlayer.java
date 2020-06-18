package com.emamaker.amazeing.manager.network.action.actions.client.login;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PlayerRemoved;
import com.emamaker.amazeing.manager.network.NetworkCommon.RemovePlayer;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NACRemovePlayer extends NetworkAction {

	String uuid;

	protected NACRemovePlayer(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime, String uuid_) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		this.uuid = uuid_;
	}

	public NACRemovePlayer(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.RemovePlayer(), new NetworkCommon.PlayerRemoved(), false);
	}

	/*
	 * Here startAction needs to be overrided to allow for the uuid argument to be
	 * passed from one instance to another. This also removes the alreadyPending statement instead of setting 
	 * the oneAtTheTie flag.
	 */
	public void startAction(String s_) {
		this.startAction(incomingConnection, incomingMsg, s_);
	}

	public void startAction(Connection conn, Object msg, String uuid_) {
		this.incomingConnection = conn;
		this.incomingMsg = msg;
		this.uuid = uuid_;
		System.out.println("Starting action: " + this);

		parent.addToPending(newInstance());
	}

	@Override
	public void resolveAction() {
		super.resolveAction();
//		System.out.println("Asking the server to remove player " + uuid);
		((RemovePlayer) responsePacket).uuid = uuid;
		client().client.sendUDP(responsePacket);
	}

	/*
	 * We also have to override the responseReceived package, because this action
	 * has to finish only if the correct uuid has been received
	 */
	public void responseReceived(Connection conn, Object msg) {
		this.endConnection = conn;
		this.endMsg = msg;
		if ( endMsg != null && (((PlayerRemoved) endMsg).uuid == null || ((PlayerRemoved) endMsg).uuid.equals(uuid))) {
			if (client().players.containsKey(uuid)) {
				client().players.get(uuid).dispose();
				client().players.remove(uuid);
				client().localPlayers.remove(uuid);
			}
			detachFromParent();
		}
		System.out.println("Response received for " + this);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACRemovePlayer(client(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime, uuid);
	}

}
