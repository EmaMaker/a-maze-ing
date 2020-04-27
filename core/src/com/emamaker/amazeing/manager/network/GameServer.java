package com.emamaker.amazeing.manager.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.UUID;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.manager.network.NetworkCommon.AddNewPlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.ConnectionRefused;
import com.emamaker.amazeing.manager.network.NetworkCommon.EndGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.JustConnected;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO;
import com.emamaker.amazeing.manager.network.NetworkCommon.LoginAO2;
import com.emamaker.amazeing.manager.network.NetworkCommon.RemovePlayer;
import com.emamaker.amazeing.manager.network.NetworkCommon.StartGame;
import com.emamaker.amazeing.manager.network.NetworkCommon.UpdatePlayerTransform;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerRemote;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer {

	public AMazeIng main;

	volatile boolean serverRunning = false;
	boolean endGameCalled = false;
	public int port;
	UUID uuid;

	public GameManager gameManager;
	Server server;

	// Hashtable of remote players present in the match. This will be used to update
	// other players' transform when server reports about it
	public Hashtable<String, MazePlayerRemote> remotePlayers = new Hashtable<>();

	public GameServer(AMazeIng main_) {
		main = main_;
		uuid = UUID.randomUUID();
	}

	// Returns true if the server started successfully
	public boolean startServer(int port_) {
		port = port_;
		serverRunning = true;
		try {
			server = new Server() {
				protected Connection newConnection() {
					// Notify connection about previously connected clients

					AddNewPlayer response = new AddNewPlayer();
					for (String s : remotePlayers.keySet()) {
						response.uuid = s;
						server.sendToAllTCP(response);
					}

					// By providing our own connection implementation, we can store per
					// connection state without a connection ID to state look up.
					return new ConnectionPlayer();
				}
			};

			// For consistency, the classes to be sent over the network are
			// registered by the same method for both the client and server.
			NetworkCommon.register(server);

			server.addListener(new Listener() {
				public void received(Connection c, Object object) {
					ConnectionPlayer connection = (ConnectionPlayer) c;

					if(object instanceof JustConnected) {
						//Notify the newly connected client about all other clients already present here
						System.out.println("New client just connected, updating it with info about other clients!");	
						AddNewPlayer response = new AddNewPlayer();
						for (String s : remotePlayers.keySet()) {
							response.uuid = s;
							c.sendTCP(response);
							System.out.println("Updated about: " + s);
						}					
					}else if (object instanceof LoginAO) { 
						// Give player its UUID and wait for response. Once the LoginAO2 response is
						// received, move the
						// UUID to the list of players, create a new one and notify clients about it
						connection.uuid = UUID.randomUUID().toString();

						LoginAO2 response = new LoginAO2();
						response.uuid = connection.uuid;
						System.out.println("Server received connection request! Giving client UUID " + connection.uuid);
						c.sendTCP(response);

					} else if (object instanceof LoginAO2) {
						// Ignore is there's no uuid or it's different from the login message one
						// If there's still space left for players to join
						if (remotePlayers.values().size() < MazeSettings.MAXPLAYERS) {
							remotePlayers.put(((LoginAO2) object).uuid,
									new MazePlayerRemote(((LoginAO2) object).uuid, false));

							System.out.println(
									"Client with UUID " + ((LoginAO2) object).uuid + " is connected and ready to play :)");
							
							AddNewPlayer response = new AddNewPlayer();
							response.uuid = ((LoginAO2) object).uuid;
							server.sendToAllTCP(response);
						} else {
							// Send connection refused
							c.sendTCP(new ConnectionRefused());
						}

					} else if (object instanceof RemovePlayer) {
						// Otherwise remove the player and notify all clients about it
						if (remotePlayers.containsKey(((RemovePlayer) object).uuid.toString())) {
							remotePlayers.get(((RemovePlayer) object).uuid).dispose();
							remotePlayers.remove(((RemovePlayer) object).uuid);

							System.out.println("Client with UUID " + connection.uuid + " is leaving the server :(");
							server.sendToAllTCP(object);
						}else {
							System.out.println("Server received delete message for player with UUID " + connection.uuid + " but player wasn't playing");
						}
					} else if (object instanceof UpdatePlayerTransform) {
						UpdatePlayerTransform transform = (UpdatePlayerTransform) object;
						if (gameManager.gameStarted) {
							// Otherwise Update the transport and notify clients about it
							MazePlayerRemote player = remotePlayers.get((transform.uuid));
							player.setTransform(transform.tx, transform.ty, transform.tz, transform.rx, transform.ry,
									transform.rz, transform.rw);
							updatePlayer(transform.uuid, remotePlayers.get(transform.uuid), false);
							System.out.println("Updating client " + connection.uuid + " position!");
						}
					}
				}

				public void disconnected(Connection c) {
					ConnectionPlayer connection = (ConnectionPlayer) c;
					if (connection.uuid != null) {
						if (remotePlayers.get(connection.uuid) != null) {
							remotePlayers.get(connection.uuid).dispose();
						}
						remotePlayers.remove(connection.uuid);
						RemovePlayer remove = new RemovePlayer();
						remove.uuid = connection.uuid;
						server.sendToAllTCP(remove);
					}
				}
			});

			server.bind(port);
			server.start();
			System.out.println("Server registered and running on port " + port);

			// Also launch the client to have a player play on host. We return the result of
			// starting, so server doesn't start if local client has problems
			if (main.client.start("localhost", port))
				return true;
			else {
				server.stop();
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Update must be called from Main thread and used for applications on main
	// thread, such as spawning new players
	public void update() {
		if (serverRunning) {
			if (gameManager != null) {
				gameManager.update();
				if (gameManager.anyoneWon && !endGameCalled) {
					server.sendToAllTCP(new EndGame());
					endGameCalled = true;
				}
			}
		}
	}

	// Once the server has started accepting connections from other players, the
	// host should decide when to start the gmae
	// A proper ui should be added, but for now we can just start the game without
	// showing any players and just show the map across all the clients
	public void startGame() {
		if (serverRunning) {
			update();
			// Start game stuff
			this.gameManager = new GameManager(main, GameType.SERVER);
			this.gameManager.generateMaze(new HashSet<MazePlayer>(remotePlayers.values()));
			endGameCalled = false;

			StartGame request = new StartGame();
			request.map = this.gameManager.mazeGen.runLenghtEncode();
			server.sendToAllTCP(request);

			if (gameManager.gameStarted)
				for (String p : remotePlayers.keySet())
					updatePlayer(p, remotePlayers.get(p), true);

			if (main.getScreen() != null) {
				main.getScreen().hide();
				main.setScreen(null);
			}
		} else {
			System.out.println("Server not started yet, game cannot start");
		}
	}

	public void updatePlayer(String uuid, MazePlayerRemote p, boolean force) {
		if (serverRunning && this.gameManager != null && this.gameManager.gameStarted) {
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
				server.sendToAllTCP(pu);
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
				server.sendToAllTCP(pu);
			}
		}
	}

	public void stop() {
		for (MazePlayerRemote p : remotePlayers.values())
			if (!p.isDisposed())
				p.dispose();
		remotePlayers.clear();
		if (serverRunning) {
			main.client.stop();
			server.stop();
			serverRunning = false;
		}
	}

	public boolean isRunning() {
		return serverRunning;
	}
}

class ConnectionPlayer extends Connection {
	public String uuid;
}
