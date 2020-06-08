package com.emamaker.amazeing.manager.network.action.actions.client.game;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateMap;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NACUpdateMap extends NetworkAction {

	String map;

	protected NACUpdateMap(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NACUpdateMap(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.UpdateMap(), null, null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		if (client().gameManager.gameStarted) {
			map = ((UpdateMap) incomingMsg).map;
			client().gameManager.mazeGen.show(client().gameManager.mazeGen.runLenghtDecode(map));
		}
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACUpdateMap(client(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
