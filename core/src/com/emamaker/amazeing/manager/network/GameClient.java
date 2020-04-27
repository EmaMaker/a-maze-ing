package com.emamaker.amazeing.manager.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.manager.network.NetworkCommon.AddNewPlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.EndGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.JustConnected;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO2;
import com.emamaker.amazeing.manager.network.NetworkCommon.RemovePlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.StartGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdateMap;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerTransform;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.emamaker.amazeing.player.MazePlayerRemote;
import com.emamaker.amazeing.player.PlayerUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class GameClient {

	public AMazeIng main;
	volatile boolean clientRunning = false;

	public String addr;
	public int port;

	boolean startGame = false;
	boolean showPreGame = false;
	String map = "";

	// Hashtable of players present in the match
	public Hashtable<String, MazePlayer> players = new Hashtable<>();
	ArrayList<MazePlayer> localPlrQueue = new ArrayList<MazePlayer>();

	volatile HashSet<String> toAdd = new HashSet<>();
	volatile HashSet<String> toRemove = new HashSet<>();

	public GameManager gameManager;
	Client client;

	public GameClient(AMazeIng main_) {
		main = main_;
	}

	public boolean start(String addr_, int port_) {
		port = port_;
		addr = addr_;

		clientRunning = true;
		startGame = false;
		client = new Client();
		client.start();

		NetworkCommon.register(client);

		client.addListener(new Listener() {
			public void connected(Connection connection) {
			}

			public void received(Connection connection, Object object) {
				if (object instanceof LoginAO2) {
					localPlrQueue.get(0).uuid = ((LoginAO2) object).uuid;
					toAdd.add("Local" + localPlrQueue.get(0).uuid);
					client.sendTCP(object);

					System.out.println("Received UUID "  + localPlrQueue.get(0).uuid + " for player " + localPlrQueue.get(0) + " giving confirmation");

					// When we receive the connection accept from the server, we can show the
					// pre-game screen listing the players' names, setting this flag to let the main
					// thread to it
					showPreGame = true;
				} else if (object instanceof AddNewPlayer) {
					AddNewPlayer msg = (AddNewPlayer) object;
					if (!players.containsKey(msg.uuid) && !toAdd.contains("Local"+msg.uuid)) {
						toAdd.add("Remote" + msg.uuid);
						System.out.println("Remote player with uuid " + msg.uuid.toString() + " has joined the game :)");
					}
				} else if (object instanceof RemovePlayer) {
					RemovePlayer msg = (RemovePlayer) object;
					if (players.containsKey(msg.uuid)) {
						toRemove.add(msg.uuid);
						System.out.println("Player with uuid " + msg.uuid.toString() + " is leaving the game :(");
					}else {
						System.out.println("Player remove received, but I don't know that player :/");
					}
				} else if (object instanceof NetworkCommon.UpdatePlayerTransformServer) {
					NetworkCommon.UpdatePlayerTransformServer s = (NetworkCommon.UpdatePlayerTransformServer) object;
					System.out.println("Received a forced position update for self!");
					if (players.containsKey(s.uuid)) {
						players.get(s.uuid).setPlaying();
						players.get(s.uuid).setTransform(s.tx, s.ty, s.tz, s.rx, s.ry, s.rz, s.rw);
					}
				} else if (object instanceof UpdatePlayerTransform) {
					UpdatePlayerTransform msg = (UpdatePlayerTransform) object;
					if (players.containsKey(msg.uuid) && players.get(msg.uuid) instanceof MazePlayerRemote) {
						players.get(msg.uuid).setPlaying();
						players.get(msg.uuid).setTransform(msg.tx, msg.ty, msg.tz, msg.rx, msg.ry, msg.rz, msg.rw);
					}
				} else if (object instanceof UpdateMap) {
					map = ((UpdateMap) object).map;
					System.out.println("Map update!");
				} else if (object instanceof StartGame) {
					startGame = true;
					map = ((StartGame) object).map;
					System.out.println("Starting the online game!");
				} else if (object instanceof EndGame) {
					System.out.println("EndGame Received!");
					if (gameManager != null) {
						gameManager.gameStarted = false;
						gameManager.anyoneWon = true;
						showPreGame = true;
					}
				}
			}

			public void disconnected(Connection connection) {
				toRemove.addAll(players.keySet());
			}
		});

		try {
			client.connect(5000, addr, port);
			System.out.println("Connecting to server...");
			//Tell the server you just connected, but still no players have to be add
			client.sendTCP(new JustConnected());
			return true;
			// Server communication after connection can go here, or in
			// Listener#connected().
		} catch (

		IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	// Update must be called from Main thread and used for applications on main
	// thread
	MazePlayerLocal p;

	public void update() {
		if (clientRunning) {
			try {
				for (String s : toAdd) {
					if (!(players.containsKey(s.replace("Local", "")) || players.containsKey(s.replace("Remote", "")))) {
						if (s.startsWith("Local")) {
//							System.out.println(s + " | " + s.replace("Local", "") + " | " + localPlrQueue.get(0).uuid);
							if (localPlrQueue.get(0) != null) {
								players.put(s.replace("Local", ""), localPlrQueue.get(0));
								System.out.println("Added local player " + localPlrQueue.get(0));
								localPlrQueue.remove(0);
							}
						} else if (s.startsWith("Remote")) {
							players.put(s.replace("Remote", ""), new MazePlayerRemote(s.replace("Remote", "")));
						}
					}
				}
				toAdd.clear();
				for (String s : toRemove) {
					if (players.containsKey(s)) {
						players.get(s).dispose();
						players.remove(s);
					}
				}
				toRemove.clear();
			} catch (Exception e) {

			}

			if (showPreGame) {
				// We are taking care of specifying what type of game we are running. Server is
				// the server is running in the same instance, client if not
				// In this way server host is shown the start game button.
				if (!main.server.isRunning())
					main.uiManager.preGameScreen.setGameType(GameType.CLIENT);
				main.setScreen(main.uiManager.preGameScreen);
				showPreGame = false;
			}

			if (startGame) {
				gameManager = new GameManager(main, GameType.CLIENT);

				if (main.getScreen() != null) {
					main.getScreen().hide();
					main.setScreen(null);
				}

				for (MazePlayer p : players.values())
					p.setPlaying();

				gameManager.generateMaze(new HashSet<MazePlayer>(players.values()));
				startGame = false;
			}

			if (gameManager != null) {
				gameManager.update();

				if (gameManager.gameStarted) {
					if (!map.equals("")) {
						System.out.println("Setting map");
						gameManager.mazeGen.show(gameManager.mazeGen.runLenghtDecode(map));
						map = "";
					}
				}
			}

			if (gameManager == null || (gameManager != null && !gameManager.gameStarted)) {
				// Consantly search for new players to be added
				// First search for keyboard players (WASD and ARROWS)
				if (Gdx.input.isKeyJustPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.A)
						|| Gdx.input.isKeyJustPressed(Keys.S) || Gdx.input.isKeyJustPressed(Keys.D)) {
					p = PlayerUtils.getPlayerWithKeys(new HashSet<>(players.values()), Keys.W, Keys.S, Keys.A, Keys.D);
					if (p != null) {
						RemovePlayer msg = new RemovePlayer();
						msg.uuid = p.uuid;
						client.sendTCP(msg);
						System.out.println("I should be deleting local player with uuid " + p.uuid);
					} else {
						localPlrQueue.add(new MazePlayerLocal(Keys.W, Keys.S, Keys.A, Keys.D));
						client.sendTCP(new LoginAO());
					}
				}

				if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.LEFT)
						|| Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
					p = PlayerUtils.getPlayerWithKeys(new HashSet<>(players.values()), Keys.UP, Keys.DOWN, Keys.LEFT,
							Keys.RIGHT);
					if (p != null) {
						RemovePlayer msg = new RemovePlayer();
						msg.uuid = p.uuid;
						client.sendTCP(msg);
					} else {
						localPlrQueue.add(new MazePlayerLocal(Keys.UP, Keys.DOWN, Keys.LEFT, Keys.RIGHT));
						client.sendTCP(new LoginAO());
					}
				}
			}
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
			pu.uuid = p.uuid;

			client.sendTCP(pu);
		}
	}

	public boolean isRunning() {
		return clientRunning;
	}

	public void stop() {
		if (clientRunning) {
			for (String s : players.keySet()) {
				if (players.get(s) instanceof MazePlayerLocal) {
					RemovePlayer request = new RemovePlayer();
					request.uuid = s;
					client.sendTCP(request);
				}

				players.get(s).dispose();
			}

			players.clear();

			client.stop();
			clientRunning = false;
		}
	}

}
