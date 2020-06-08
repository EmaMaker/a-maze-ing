package com.emamaker.amazeing.manager.network.action.actions.client.game;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.esotericsoftware.kryonet.Connection;

public class NACServerClosed extends NetworkAction {

	protected NACServerClosed(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NACServerClosed(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.ServerClosed(), null, null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		System.out.println("Server stopped!");
		parent.stop();
		main.uiManager.srvJoinScreen.showErrorDlg(0);
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACServerClosed(client(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
