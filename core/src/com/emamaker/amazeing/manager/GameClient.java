package com.emamaker.amazeing.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.emamaker.amazeing.AMazeIng;

public class GameClient {

	public AMazeIng main;
	Thread clientThread;
	volatile boolean clientRunning = false;

	public String addr;
	public int port;
	public Socket socket;

	volatile String nextMessage = "";
	String s = "", s1 = "", rMsg = "";

	UUID uuid;
	int connectionStep;

	public GameClient(AMazeIng main_) {
		main = main_;
	}

	public void start(String addr_, int port_) {
		port = port_;
		addr = addr_;
		clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				clientRunning = true;
				while (clientRunning) {
					if (socket == null) {
						SocketHints socketHints = new SocketHints();
						socketHints.connectTimeout = 10000;
						socket = Gdx.net.newClientSocket(Protocol.TCP, addr, port, socketHints);

						sendMessageToSocket(socket, "AO");
						connectionStep = 0;
					} else {
						if (socket.isConnected()) {
							rMsg = receiveMessageFromSocket(socket);

							if (rMsg.startsWith("UUID")) {
								// UUID received from server, go on
								//it doesn't matter if this already happened, just start again
								//No new UUID will be generated
								s1 = rMsg.replace("UUID", "");
								uuid = UUID.fromString(s1);
								connectionStep = 1;
								System.out.println("Got UUID from server: " + uuid.toString());
								sendMessageToSocket(socket, "AO2");
							}
							if (rMsg.equals("AO3")) {
								if (connectionStep == 1) {
									connectionStep = 2;
									sendMessageToSocket(socket, "AO4");
									System.out.println("Connected with server!");
								}
							}

						}
					}
				}
			}
		});
		clientThread.start();
	}

	// Update must be called from Main thread and used for applications on main
	// thread
	public void update() {
	}

	public void sendMessagetoServer(String s) {
		nextMessage += (s + "\n");
	}

	public void stop() {
		clientRunning = false;
	}

	void sendMessageToSocket(Socket s, String msg) {
		if (s != null && s.isConnected())
			try {
				// write our entered message to the stream
				System.out.println("Client sending msg: " + msg + " to " + s);
				s.getOutputStream().write((msg + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	String tmprs;

	String receiveMessageFromSocket(Socket s) {
		if (s != null && s.isConnected())
			// Receive messages from the client
			try {
				if (s.getInputStream().available() > 0) {
					BufferedReader buffer = new BufferedReader(new InputStreamReader(s.getInputStream()));
					// Read to the next newline (\n) and display that text on labelMessage
					tmprs = buffer.readLine();
					System.out.println("Client received message From: " + s + "!\n" + tmprs);
					return tmprs;
				}
			} catch (IOException e) {
			}
		return "";
	}

//	// Receive messages from the server
//	try {
//		if (socket.getInputStream().available() > 0) {
//			BufferedReader buffer = new BufferedReader(
//					new InputStreamReader(socket.getInputStream()));
//
//			// Read to the next newline (\n) and display that text on labelMessage
//			s = buffer.readLine().toString();
//			//System.out.println("Received message from server: " + socket + "!\n" + s);
//			if(s.startsWith("Map")) {
//				s1 = s.replace("Map", "");
//				main.gameManager.generateMaze(main.gameManager.mazeGen.runLenghtDecode(s1));
//				
//			}
//		}
//	} catch (IOException e) {
//	}
//	// Send messages to the client
//	if (!nextMessage.equals(""))
//		try {
//			// write our entered message to the stream
//			socket.getOutputStream().write((nextMessage + "\n").getBytes());
//		} catch (IOException e) {
////			e.printStackTrace();
//		}
}
