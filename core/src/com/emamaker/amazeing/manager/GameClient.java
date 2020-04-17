package com.emamaker.amazeing.manager;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.emamaker.amazeing.AMazeIng;

public class GameClient {

	volatile boolean clientRunning = false;
	public Socket socket;
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
					/*
					 * https://www.gamefromscratch.com/post/2014/03/11/LibGDX-Tutorial-10-Basic-
					 * networking.aspx
					 */
					if (socket == null) {
						SocketHints socketHints = new SocketHints();
						socketHints.connectTimeout = 0;
						socket = Gdx.net.newClientSocket(Protocol.TCP, addr, port, socketHints);
						System.out.println("Connected to server!");
					} else {
						if (socket.isConnected()) {
							// Send messages to the server:
							try {
								// write our entered message to the stream
								socket.getOutputStream().write("AO\n".getBytes());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
		clientThread.start();

	}

	public void stop() {
		clientRunning = false;
	}
}
