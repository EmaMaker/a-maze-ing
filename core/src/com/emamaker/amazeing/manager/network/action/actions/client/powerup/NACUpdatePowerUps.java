package com.emamaker.amazeing.manager.network.action.actions.client.powerup;

import java.util.Arrays;

import com.emamaker.amazeing.manager.network.NetworkCommon;
import com.emamaker.amazeing.manager.network.NetworkCommon.PowerUpUpdate;
import com.emamaker.amazeing.manager.network.NetworkHandler;
import com.emamaker.amazeing.manager.network.action.NetworkAction;
import com.emamaker.amazeing.player.powerups.PowerUp;
import com.esotericsoftware.kryonet.Connection;

public class NACUpdatePowerUps extends NetworkAction {

	String[] a;
	String name;
	float x, y, z;
	boolean found = true;

	static String[] oldList = new String[0];

	protected NACUpdatePowerUps(NetworkHandler parent, Connection c, Object incomingMsg_, Object responsePacket_,
			Object endPacket_, boolean oneTime) {
		super(parent, c, incomingMsg_, responsePacket_, endPacket_, oneTime);
	}

	public NACUpdatePowerUps(NetworkHandler parent_) {
		super(parent_, new NetworkCommon.PowerUpUpdate(), null, null, true);
	}

	@Override
	public void resolveAction() {
		super.resolveAction();

		// This part works exactly the same way as remote player adding and removal
		// works

		if (!Arrays.equals(((PowerUpUpdate) incomingMsg).powerups, oldList)) {

			//now check if the currently used powerups are in the list received from the server, if not they have to be removed because they are not present anymore in the game
			for(PowerUp p : client().gameManager.powerups) {
				found = false;
				
				//This could result quite computationally expensive and could be optimized in some way, but now we just need the thing working
				for (String s : ((PowerUpUpdate) incomingMsg).powerups) {
					a = s.split("-");
					name = a[0];
					x = Float.valueOf(a[1]);
					y = Float.valueOf(a[2]);
					z = Float.valueOf(a[3]);

					found = p.getPosition().x == x && p.getPosition().z == z && p.name.equals(name); 
					if(found) break;
				}
				if(!found) client().gameManager.removePowerUp(p);
			}
			
			//check if there are new powerups to be added
			for (String s : ((PowerUpUpdate) incomingMsg).powerups) {
				a = s.split("-");
				name = a[0];
				x = Float.valueOf(a[1]);
				y = Float.valueOf(a[2]);
				z = Float.valueOf(a[3]);
				if (!client().gameManager.thereIsPowerUpInPos((int) x, (int) z)) {
					client().gameManager.spawnPowerUpByName(name, x, z);
				}
			}
			
			oldList = ((PowerUpUpdate) incomingMsg).powerups;
		}
	}

	@Override
	public void responseReceived(Connection conn, Object msg) {
		super.responseReceived(conn, msg);
	}

	@Override
	public NetworkAction newInstance() {
		return new NACUpdatePowerUps(client(), incomingConnection, incomingMsg, responsePacket, endPacket, oneTime);
	}

}
