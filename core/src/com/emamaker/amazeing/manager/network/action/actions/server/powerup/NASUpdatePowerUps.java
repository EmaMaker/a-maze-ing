package com.emamaker.amazeing.manager.network.action.actions.server.powerup;

import java.util.ArrayList;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpUpdate;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.emamaker.amazeing.player.powerups.PowerUp;
import com.esotericsoftware.kryonet.Connection;

public class NASUpdatePowerUps extends NetworkAction {

	ArrayList<String> powerups = new ArrayList<String>();

	protected NASUpdatePowerUps(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
		enableTimeout(false);
	}

	public NASUpdatePowerUps(NetworkHandler parent_) {
		super(parent_, null, new NetworkCommon.PowerUpUpdate(), null, false);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		try {
			powerups.clear();

			if (server().gameManager.powerups != null && !server().gameManager.powerups.isEmpty()) {
				for (PowerUp p : server().gameManager.powerups) {
					powerups.add(p.name + "-" + p.getPosition().x + "-" + p.getPosition().y + "-" + p.getPosition().z);
				}

				((PowerUpUpdate) responsePacket).powerups = powerups.toArray(new String[powerups.size()]);
				server().server.sendToAllUDP(responsePacket);
			}
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
		return new NASUpdatePowerUps(server(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
