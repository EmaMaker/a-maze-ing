package com.emamaker.amazeing.manager.network.action.actions.server.game;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.GameStatusUpdate;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NASGameStatusUpdate extends NetworkAction {

	protected NASGameStatusUpdate(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		enableTimeout(false);
	}

	public NASGameStatusUpdate(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.GameStatusUpdate(), null, false);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		try {
			((GameStatusUpdate) responsePacket).playersUUIDs = server().players.keySet()
					.toArray(new String[server().players.size()]);
			((GameStatusUpdate) responsePacket).gameStarted = server().gameManager.gameStarted;
			((GameStatusUpdate) responsePacket).anyoneWon = server().gameManager.anyoneWon;

			server().server.sendToAllUDP(responsePacket);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NASGameStatusUpdate(server(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
