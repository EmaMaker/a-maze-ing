package com.emamaker.amazeing.manager.network;

import java.util.Hashtable;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.managers.GameManager;
import com.emamaker.amazeing.manager.network.NetworkCommon.AddNewPlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.ConnectionRefused;
import com.emamaker.amazeing.manager.network.NetworkCommon.EndGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO2;
import com.emamaker.amazeing.manager.network.NetworkCommon.RemovePlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.StartGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateMap;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerTransform;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerTransformServer;
import com.emamaker.amazeing.player.MazePlayer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public abstract class NetworkHandler {

	public Hashtable<String, MazePlayer> players = new Hashtable<String, MazePlayer>();
	public GameManager gameManager;
	public AMazeIng main = AMazeIng.getMain();

	int port;
	boolean running = false;

	long time = 0;
	int UPDATE_PERIOD = 2000;

	/*
	 * Since Kryonet defaults the use of a TCP port: Login UUID negotation, game
	 * starting and ending, player adding and removal settings update are done using
	 * TCP since we can't afford to lose packets on them, and they're done in a
	 * phase of the game when there's few UPD traffic if not at all Player Transform
	 * Updates and map updates will be done in UDP, since there can be a lot in a
	 * short period of time and they're not essential to the game (meaning some
	 * updates can be skipped)
	 */

	public abstract void onLoginAO(Connection c);

	public abstract void onLoginAO2(Connection c);

	public abstract void onConnectionRefused(Connection c);

	public abstract void onAddNewPlayer(Connection c);

	public abstract void onRemovePlayer(Connection c);

	public abstract void onUpdateTransform(Connection c);

	public abstract void onUpdateTransformServer(Connection c);

	public abstract void onStartGame(Connection c);

	public abstract void onEndGame(Connection c);

	public abstract void onUpdateMap(Connection c);

	public abstract void onUpdateSettings(Connection c);

	public abstract void onConnected(Connection c);

	public abstract void onAddPowerUp(Connection c);

	public abstract void onRemovePowerUp(Connection c);

	public abstract void onAssignPowerUp(Connection c);

	public abstract void onStartUsingPowerUp(Connection c);

	public abstract void onEndUsingPowerUp(Connection c);

	Object message;

	public void onReceived(Connection c, Object object) {
		message = object;

		if (object instanceof LoginAO)
			onLoginAO(c);
		else if (object instanceof LoginAO2)
			onLoginAO2(c);
		else if (object instanceof ConnectionRefused)
			onConnectionRefused(c);
		else if (object instanceof UpdatePlayerTransform)
			onUpdateTransform(c);
		else if (object instanceof UpdatePlayerTransformServer)
			onUpdateTransformServer(c);
		else if (object instanceof StartGame)
			onStartGame(c);
		else if (object instanceof EndGame)
			onEndGame(c);
		else if (object instanceof UpdateMap)
			onUpdateMap(c);
		else if (object instanceof AddNewPlayer)
			onAddNewPlayer(c);
		else if (object instanceof RemovePlayer)
			onRemovePlayer(c);
	}

	Listener connectionListener = new Listener() {
		public void received(com.esotericsoftware.kryonet.Connection arg0, Object arg1) {
			onReceived(arg0, arg1);
		};

		public void connected(com.esotericsoftware.kryonet.Connection arg0) {
			onConnected(arg0);
		};
	};

	public void update() {
		if (gameManager != null && gameManager.gameStarted)
			gameManager.update();

		if (gameManager != null && System.currentTimeMillis() - time > UPDATE_PERIOD) {
			if (gameManager.gameStarted)
				periodicGameUpdate();
			else
				periodicNonGameUpdate();
			time = System.currentTimeMillis();
		}
	}

	public void periodicGameUpdate() {
	}

	public void periodicNonGameUpdate() {
	}

	public boolean isRunning() {
		return running;
	}

	public abstract boolean startGame();

	public abstract void stop();

	public Object updatePlayer(String uuid, MazePlayer p, boolean force) {
		if (force) {
			NetworkCommon.UpdatePlayerTransformServer pu = new NetworkCommon.UpdatePlayerTransformServer();
			Vector3 pos = p.getPos();
			Quaternion rot = p.getRotation();
			pu.tx = pos.x;
			pu.ty = pos.y;
			pu.tz = pos.z;
			pu.rx = rot.x;
			pu.ry = rot.y;
			pu.rz = rot.z;
			pu.rw = rot.w;
			pu.uuid = uuid;
			System.out.println("Forcing position update to all clients for player " + uuid);
			return pu;
		} else {
			UpdatePlayerTransform pu = new UpdatePlayerTransform();
			Vector3 pos = p.getPos();
			Quaternion rot = p.getRotation();
			pu.tx = pos.x;
			pu.ty = pos.y;
			pu.tz = pos.z;
			pu.rx = rot.x;
			pu.ry = rot.y;
			pu.rz = rot.z;
			pu.rw = rot.w;
			pu.uuid = uuid;
			System.out.println("Sending position update to all clients for player " + uuid);
			return pu;
		}
	}

}
