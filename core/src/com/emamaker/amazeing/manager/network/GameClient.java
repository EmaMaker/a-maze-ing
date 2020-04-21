package com.emamaker.amazeing.manager.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.manager.network.NetworkCommon.AddNewPlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO2;
import com.emamaker.amazeing.manager.network.NetworkCommon.RemovePlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.StartGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateMap;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerTransform;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.emamaker.amazeing.player.MazePlayerRemote;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class GameClient {

	public AMazeIng main;
	volatile boolean clientRunning = false;

	public String addr;
	public int port;

	boolean startGame = false;
	String map = "";

	// Hashtable of remote players present in the match. This will be used to update
	// other players' transform when server reports about it
	Hashtable<String, MazePlayerRemote> remotePlayers = new Hashtable<>();
//	Hashtable<String, MazePlayerLocal> localPlayers = new Hashtable<>();
	MazePlayerLocal player;

	ArrayList<MazePlayer> players = new ArrayList<MazePlayer>();

	volatile HashSet<String> toAdd = new HashSet<>();
	volatile HashSet<String> toRemove = new HashSet<>();

	GameManager gameManager;
	Client client;
	// UUID is represented using a string, for kryonet ease of use
	String uuid = "";

	public GameClient(AMazeIng main_) {
		main = main_;
	}

	public void start(String addr_, int port_) {
		port = port_;
		addr = addr_;

		clientRunning = true;
		startGame = false;
		client = new Client();
		client.start();
		uuid = "";
		player = new MazePlayerLocal(main, Keys.W, Keys.S, Keys.A, Keys.D);
		remotePlayers.clear();

		NetworkCommon.register(client);

		client.addListener(new Listener() {
			public void connected(Connection connection) {
			}

			public void received(Connection connection, Object object) {
				if (object instanceof LoginAO2) {
					uuid = ((LoginAO2) object).uuid;
					client.sendTCP(object);
					System.out.println("Received UUID " + uuid.toString() + " from server, giving confirmation!");
				} else if (object instanceof AddNewPlayer) {
					AddNewPlayer msg = (AddNewPlayer) object;
					if ((!msg.uuid.equals(uuid))) {
						toAdd.add(msg.uuid);
						System.out
								.println("Remote player with uuid " + msg.uuid.toString() + " has joined the game :)");
					}
				} else if (object instanceof RemovePlayer) {
					RemovePlayer msg = (RemovePlayer) object;
					if ((!msg.uuid.equals(uuid))) {
						toRemove.add(msg.uuid);
						System.out
								.println("Remote player with uuid " + msg.uuid.toString() + " is leaving the game :(");
					}
				} else if (object instanceof NetworkCommon.UpdatePlayerTransformServer) {
					NetworkCommon.UpdatePlayerTransformServer s = (NetworkCommon.UpdatePlayerTransformServer) object;
					System.out.println("Received a forced position update for self!");
					if (s.uuid.equals(uuid)) {
						player.setPlaying();
						player.setTransform(s.tx, s.ty, s.tz, s.rx, s.ry, s.rz, s.rw);
					} else {
						remotePlayers.get(s.uuid).setPlaying();
						remotePlayers.get(s.uuid).setTransform(s.tx, s.ty, s.tz, s.rx, s.ry, s.rz, s.rw);
					}
				} else if (object instanceof UpdatePlayerTransform) {
					UpdatePlayerTransform msg = (UpdatePlayerTransform) object;
					if (!msg.uuid.equals(uuid)) {
						remotePlayers.get(msg.uuid).setPlaying();
						remotePlayers.get(msg.uuid).setTransform(msg.tx, msg.ty, msg.tz, msg.rx, msg.ry, msg.rz,
								msg.rw);
						System.out.println("R: " + msg.tx + ", " + msg.ty + ", " + msg.tz);
						System.out.println("Updating remote player with uuid " + msg.uuid.toString());
					}
				} else if (object instanceof UpdateMap) {
					map = ((UpdateMap) object).map;
					System.out.println("Map update!");
				} else if (object instanceof StartGame) {
					startGame = true;
					map = ((StartGame) object).map;
					System.out.println("Starting the online game!");
				}
			}

			public void disconnected(Connection connection) {
				for (MazePlayerRemote p : remotePlayers.values()) {
					p.dispose();
				}
			}
		});

		try {
			client.connect(5000, addr, port);
			if (uuid.equals("")) {
				client.sendTCP(new LoginAO());
				System.out.println("Connecting to server...");
			} else {
				System.out.println("Already connected, to need to connect again");
			}
			// Server communication after connection can go here, or in
			// Listener#connected().
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Update must be called from Main thread and used for applications on main
	// thread
	public void update() {
		if (clientRunning) {
			try {
				for (String s : toAdd) {
					if (!s.equals(uuid))
						remotePlayers.put(s, new MazePlayerRemote(main, s));
				}
				toAdd.clear();
				for (String s : toRemove) {
					remotePlayers.get(s).dispose();
					remotePlayers.remove(s);
				}
				toRemove.clear();
			} catch (Exception e) {

			}

			if (startGame) {
				gameManager = new GameManager(main, GameType.CLIENT);
				for (MazePlayerRemote p : remotePlayers.values())
					if (!players.contains(p))
						players.add(p);

//				for (MazePlayerLocal p : localPlayers.values())
//					if (!players.contains(p))
//						players.add(p);

				players.add(player);

				if (main.getScreen() != null) {
					main.getScreen().hide();
					main.setScreen(null);
				}

				for (MazePlayer p : players)
					p.setPlaying();
				System.out.println(Arrays.toString(players.toArray()));

				gameManager.generateMaze(new HashSet<MazePlayer>(players));
				startGame = false;
			}
			if (!map.equals("")) {
				System.out.println("Setting map");
				gameManager.mazeGen.show(gameManager.mazeGen.runLenghtDecode(map));
				map = "";
			}
			if (gameManager != null)
				gameManager.update();
		}
	}

	public void updateLocalPlayer(MazePlayerLocal p) {
		if (this.gameManager != null && this.gameManager.gameStarted && clientRunning && p.isPlaying()) {
			UpdatePlayerTransform pu = new UpdatePlayerTransform();
			Vector3 pos = p.ghostObject.getWorldTransform().getTranslation(new Vector3());
			Quaternion rot = p.ghostObject.getWorldTransform().getRotation(new Quaternion());
			pu.tx = pos.x;
			pu.ty = pos.y;
			pu.tz = pos.z;
			pu.rx = rot.x;
			pu.ry = rot.y;
			pu.rz = rot.z;
			pu.rw = rot.w;
			pu.uuid = uuid;

			client.sendTCP(pu);
		}
	}

	public void stop() {
		if (clientRunning) {
			client.stop();
			clientRunning = false;
		}
	}

}
