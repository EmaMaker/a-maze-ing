package com.emamaker.amazeing.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.emamaker.amazeing.AMazeIng;

public class GameClient {

	volatile boolean clientRunning = false;
	volatile String nextMessage = "";
	public Socket socket;
	String s = "", s1 = "";
	public String addr;
	public int port;
	Thread clientThread;
	public AMazeIng main;

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
						socketHints.connectTimeout = 0;
						socket = Gdx.net.newClientSocket(Protocol.TCP, addr, port, socketHints);
						System.out.println("Connected to server!");
						sendMessagetoClients("AO");
					} else {
						if (socket.isConnected()) {
							// Receive messages from the server
							try {
								if (socket.getInputStream().available() > 0) {
									BufferedReader buffer = new BufferedReader(
											new InputStreamReader(socket.getInputStream()));

									// Read to the next newline (\n) and display that text on labelMessage
									s = buffer.readLine().toString();
									System.out.println("Received message from server: " + socket + "!\n" + s);
									if(s.startsWith("Map")) {
										s1 = s.replace("Map", "");
										main.gameManager.generateMaze(main.gameManager.mazeGen.runLenghtDecode(s1));
										//System.out.println(s1);
										
									}
								}
							} catch (IOException e) {
							}
							// Send messages to the client
							if (!nextMessage.equals(""))
								try {
									// write our entered message to the stream
									socket.getOutputStream().write((nextMessage + "\n").getBytes());
								} catch (IOException e) {
//									e.printStackTrace();
								}
						}
						nextMessage = "";
					}
				}
			}
		});
		clientThread.start();
	}

	public void sendMessagetoClients(String s) {
		nextMessage += (s + "\n");
	}

	public void stop() {
		clientRunning = false;
	}
}
