package com.emamaker.amazeing.ui.screens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.emamaker.amazeing.ui.UIManager;

public class ServerLaunchScreen implements Screen{

	Stage stage;
	UIManager uiManager;
	
	public ServerLaunchScreen(UIManager uiManager_) {
		
		uiManager = uiManager_;
		
		stage = new Stage(new ScreenViewport());
		Container<Table> tableContainer = new Container<Table>();
		Table table = new Table();

		float cw = stage.getWidth();
		float ch = stage.getHeight();
		
		tableContainer.setSize(cw, ch);
		tableContainer.setPosition(0, 0);
		
		Label instLab = new Label("Enter the port the server should start on", uiManager.skin);
		TextButton backBtn = new TextButton("Main menu", uiManager.skin);
		TextButton connectBtn = new TextButton("Launch the server!", uiManager.skin);
		TextButton helpBtn = new TextButton("?", uiManager.skin);
		final TextArea srvPort = new TextArea("Server Port", uiManager.skin);

		// Add actions to the buttons
		backBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(uiManager.titleScreen);
				return true;
			}
		});
		helpBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Make this appear help dialog (TODO)");
				return true;
			}
		});
		connectBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				/*https://www.gamefromscratch.com/post/2014/03/11/LibGDX-Tutorial-10-Basic-networking.aspx*/
				
				// Now we create a thread that will listen for incoming socket connections
		        new Thread(new Runnable(){

		            @Override
		            public void run() {
		                ServerSocketHints serverSocketHint = new ServerSocketHints();
		                // 0 means no timeout.  Probably not the greatest idea in production!
		                serverSocketHint.acceptTimeout = 0;
		                
		                // Create the socket server using TCP protocol and listening on 9021
		                // Only one app can listen to a port at a time, keep in mind many ports are reserved
		                // especially in the lower numbers ( like 21, 80, etc )
		                ServerSocket serverSocket = Gdx.net.newServerSocket(Protocol.TCP, Integer.valueOf(srvPort.getText()), serverSocketHint);
		                
		                // Loop forever
		                while(true){
		                    // Create a socket
		                    Socket socket = serverSocket.accept(null);
		                    
		                    // Read data from the socket into a BufferedReader
		                    BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
		                    try {
		                        // Read to the next newline (\n) and display that text on labelMessage
			                    System.out.println("Server received message!");
		                        System.out.println(buffer.readLine());    
		                    } catch (IOException e) {
		                        e.printStackTrace();
		                    }
		                }
		            }
		        }).start(); // And, start the thread running
		        return true;
			}
		});
		
		
		
		Table firstRowTable = new Table();
		firstRowTable.add(backBtn).fillX().expandX().space(cw*0.005f);
		firstRowTable.add(instLab).height(50).fillX().expandX().space(cw*0.25f);
		firstRowTable.add(helpBtn).width(50).height(50).fillX().expandX().space(cw*0.005f);
		firstRowTable.setOrigin(Align.center | Align.top);

		table.row().colspan(4);
		table.add(firstRowTable);

		table.row().colspan(4);
		table.add(srvPort).fillX().expandX();
		
		table.row().colspan(4);
		table.add(connectBtn).fillX().expandX();
		
		tableContainer.setActor(table);
		stage.addActor(tableContainer);
	}

	@Override
	public void show() {
		uiManager.main.multiplexer.addProcessor(stage);
	}

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();		
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void hide() {
		uiManager.main.multiplexer.removeProcessor(stage);		
	}
	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
