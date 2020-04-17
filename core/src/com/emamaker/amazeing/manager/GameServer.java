package com.emamaker.amazeing.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.emamaker.amazeing.AMazeIng;

public class GameServer {

	volatile boolean serverRunning = false;
	String nextMessage = "oa";

	public ServerSocket serverSocket = null;
	public int port;
	ArrayList<Socket> clientSockets = new ArrayList<Socket>();
	Thread serverThread;
	public AMazeIng main;
	Socket s;

	SocketHints socketHints = new SocketHints();
	ServerSocketHints serverSocketHint = new ServerSocketHints();

	public GameServer(AMazeIng main_) {
		main = main_;
	}

	public void startServer(int port_) {
		port = port_;
		serverSocketHint.acceptTimeout = 100;
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
						Socket socket = serverSocket.accept(null);
						if (socket != null && !clientSockets.contains(socket)) {
							clientSockets.add(socket);
							System.out.println("Accepted new client: " + socket);
						}

					} catch (GdxRuntimeException se) {
						// System.out.println("No new clients connected in the last 100 milliseconds");
					}
					for (Socket s : clientSockets) {
						System.out.println(s);
						// Receive messages from the server
						try {
							BufferedReader buffer = new BufferedReader(new InputStreamReader(s.getInputStream()));

							// Read to the next newline (\n) and display that text on labelMessage
							System.out.println("Server received message From: " + s + "!\n" + buffer.readLine());
						} catch (IOException e) {
//							e.printStackTrace();
						}
					}
				}
			}

		});
		serverThread.start();
	}

	public void startGame() {
		// Once the server has started accepting connections from other players, the
		// host should decide when to start the gmae
		// A proper ui should be added, but for now we can just start the game without
		// showing any players and just show the map across all the clients
		// To spread the map we can just encode in a string the todraw[][] array in
		// MazeGenerator, in future run-lenght encoding should be considered
		main.gameManager.generateMaze(null);
		nextMessage = "Map" + Arrays.deepToString(main.gameManager.mazeGen.todraw);
	}

	public void stop() {
		if (serverSocket != null) {
			serverSocket.dispose();
			serverSocket = null;
		}
		serverRunning = false;
	}
}
