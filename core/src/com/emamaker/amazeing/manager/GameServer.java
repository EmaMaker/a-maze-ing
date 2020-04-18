package com.emamaker.amazeing.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.emamaker.amazeing.player.MazePlayerRemote;

public class GameServer {

	public AMazeIng main;

	volatile boolean serverRunning = false;
	volatile String nextMessage = "";
	volatile String rMsg;
	
	public ServerSocket serverSocket = null;
	public int port;
	UUID uuid ;
	
	Socket s;
	Thread serverThread;

	// An HashTable that stores remote players, starting from a UUID and the
	// relative socket
	// Player must be added here only once received the last acknowledgment message
	// from client
	volatile Hashtable<Socket, FullPlayerEntry> remotePlayers = new Hashtable<>();
	// An HashTable that stores remote players waiting to be accepted, starting from
	// a UUID and the
	// relative socket. The integer is needed to store the current phase the
	// acknowledgment is at (Starting from 0), if it's not equals to the expected
	// one the connectiond gets dropped and socket removed from here
	volatile Hashtable<Socket, TmpPlayerEntry> tmpPlayers = new Hashtable<>();
	// New players to be added into remotePlayers in the main thread, holds Sockets
	// used to get player info from tmpPlayers
	volatile ArrayList<Socket> newPlayers = new ArrayList<>();

	volatile TmpPlayerEntry currentTmpEntry;

	SocketHints socketHints = new SocketHints();
	ServerSocketHints serverSocketHint = new ServerSocketHints();

	public GameServer(AMazeIng main_) {
		main = main_;
		uuid = UUID.randomUUID();
	}

	public void startServer(int port_) {
		port = port_;
		serverSocketHint.acceptTimeout = 1000;
		socketHints.connectTimeout = 10000;
		serverThread = new Thread(new Runnable() {

			@Override
			public void run() {
				serverRunning = true;
				while (serverRunning) {
					if (serverSocket == null) {

						serverSocket = Gdx.net.newServerSocket(Protocol.TCP, port, serverSocketHint);
						System.out.println("Server started and listening for connections!");
					}

					try {
						Socket socket = serverSocket.accept(socketHints);
						if (socket != null && !tmpPlayers.contains(socket)
								&& !remotePlayers.keySet().contains(socket)) {
							tmpPlayers.put(socket, new TmpPlayerEntry(socket, UUID.randomUUID(), 0));
							System.out.println(
									"Accepted new tmp client: " + socket + " with ip " + socket.getRemoteAddress());
						}
					} catch (GdxRuntimeException se) {
//						System.out.println("No new clients connected in the last 1000 milliseconds");
					}

//					System.out.println(Arrays.toString(tmpPlayers.keySet().toArray()));
//					System.out.println(Arrays.toString(remotePlayers.keySet().toArray()));
					if (!main.gameManager.gameStarted) {
						for (Socket s : tmpPlayers.keySet()) {
							if (s.isConnected()) {
								rMsg = receiveMessageFromSocket(s);
								System.out.println(rMsg);
								if (rMsg.equals("AO")) {
									// Step 0
									if (tmpPlayers.get(s).step == 0) {
										// We're at the right step, send uuid to client
										sendMessageToSocket(s, "UUID" + tmpPlayers.get(s).uuid.toString());
									} else {
										// Wrong step
										System.out.println("Client already connected, ignoring request");
									}
									// If receiving multiple AOs, just start back from this point
									tmpPlayers.get(s).step = 1;
								} else if (rMsg.equals("AO2")) {
									if (tmpPlayers.get(s).step == 1) {
										System.out.println("Client " + s + " accepted uuid");
										sendMessageToSocket(s, "AO3");
										tmpPlayers.get(s).step = 2;
									}
								} else if (rMsg.equals("AO4")) {
									if (tmpPlayers.get(s).step == 2) {
										// Player can now be added to the remote player list, but this has to be done
										// inside the main thread, and this bit of code is executed outside, so pass it
										// to another function for use
										newPlayers.add(s);
									}
								}
							} else {
								// Cliented timed out, remove it from the list :(
								tmpPlayers.remove(s);
							}
						}
					}

					nextMessage = "";
					if (!messages.isEmpty())
						nextMessage = messages.remove();
					for (Socket s : remotePlayers.keySet()) {
						if (s.isConnected()) {
							if (!nextMessage.equals("")) {
								sendMessageToSocket(s, nextMessage);
							}

						} else {
							// Cliented timed out, remove it from the list :(
							remotePlayers.remove(s);
						}
					}
				}
			}

		});
		serverThread.start();
		
		//Also start client to play on the local machine
		main.gameManager.client.start("localhost", port);
	}

	// Update must be called from Main thread and used for applications on main
	// thread, such as spawning new players
	public void update() {
		// Spawn new players if needed
		for (Socket s : newPlayers) {
			remotePlayers.put(s,
					new FullPlayerEntry(s, tmpPlayers.get(s).uuid, new MazePlayerRemote(main, tmpPlayers.get(s).uuid)));
			System.out.println("Accepted new player: " + s.toString() + " " + tmpPlayers.get(s).uuid.toString());
			tmpPlayers.remove(s);
		}
		newPlayers.clear();
	}

	// Once the server has started accepting connections from other players, the
	// host should decide when to start the gmae
	// A proper ui should be added, but for now we can just start the game without
	// showing any players and just show the map across all the clients
	public void startGame() {
		update();
		ArrayList<MazePlayer> players = new ArrayList<>();
		for (FullPlayerEntry p : remotePlayers.values())
			players.add(p.player);
		sendMessagetoClients("Players" + buildUUIDList());
		
		main.gameManager.generateMaze(new HashSet<MazePlayer>(players));
		main.gameManager.setShowGame(false);

		sendMessagetoClients("Map" + main.gameManager.mazeGen.runLenghtEncode());
	}

	public String buildUUIDList() {
		String s = uuid.toString() + "!";
		for (FullPlayerEntry p : remotePlayers.values()) {
			s += p.uuid.toString() + "!";
		}
		System.out.println(s);
		return s;
	}

	public void stop() {
		if (serverSocket != null) {
			serverSocket.dispose();
			serverSocket = null;
		}
		serverRunning = false;
	}

	Queue<String> messages = new LinkedList<>();

	public void sendMessagetoClients(String s) {
		messages.add(s + "\n");
		System.out.println("Sending message to clients: " + nextMessage);
	}

	void sendMessageToSocket(Socket s, String msg) {
		if (s != null && s.isConnected())
			if (!msg.equals(""))
				try {
					// write our entered message to the stream
					System.out.println("Server sending msg: " + msg + " to " + s);
					s.getOutputStream().write((msg + "\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
	}

	String tmprs;

	String receiveMessageFromSocket(Socket s) {
		if (s != null && s.isConnected()) {
			// Receive messages from the client
			try {
				if (s.getInputStream().available() > 0) {
					BufferedReader buffer = new BufferedReader(new InputStreamReader(s.getInputStream()));
					// Read to the next newline (\n) and display that text on labelMessage
					tmprs = buffer.readLine();
					System.out.println("Server received message From: " + s + "! " + tmprs);
					return tmprs;
				}
			} catch (IOException e) {
			}
		}
		return "";
	}
}

class TmpPlayerEntry {
	public UUID uuid;
	public Socket socket;
	public int step = 0;
	public long lastTimeHeard;
	/*
	 * (Look at the notes) Step 0: AO client->server first request Step 1: UUID
	 * server->client response with uuid Step 2: AO2 client->server client accepted
	 * uuid Step 3: AO3 server->client server accepted client
	 */

	public TmpPlayerEntry(Socket s, UUID u, int t) {
		uuid = u;
		socket = s;
		step = t;
		lastTimeHeard = System.currentTimeMillis();
	}
}

class FullPlayerEntry {
	public UUID uuid;
	public Socket socket;
	public MazePlayer player;

	public FullPlayerEntry(Socket s, UUID u, MazePlayer p) {
		uuid = u;
		socket = s;
		player = p;
	}
}
