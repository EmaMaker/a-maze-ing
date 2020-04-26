package com.emamaker.amazeing.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.ui.UIManager;

public class ServerLaunchScreen implements Screen {

	Stage stage;
	UIManager uiManager;
	Screen thisScreen;
	
	public ServerLaunchScreen(UIManager uiManager_) {

		uiManager = uiManager_;
		thisScreen = this;
		
		stage = new Stage(new ScreenViewport());
		Container<Table> tableContainer = new Container<Table>();
		Table table = new Table();

		float cw = stage.getWidth();
		float ch = stage.getHeight();

		tableContainer.setSize(cw, ch);
		tableContainer.setPosition(0, 0);

		Label instLab = new Label("Enter the port the server must start on", uiManager.skin);
		TextButton backBtn = new TextButton("<", uiManager.skin);
		TextButton connectBtn = new TextButton("Launch the server!", uiManager.skin);
		TextButton setBtn = new TextButton("Settings", uiManager.skin);
		TextButton helpBtn = new TextButton("?", uiManager.skin);
		Label srvPortL = new Label("Port: ", uiManager.skin);
		final TextArea srvPort = new TextArea("", uiManager.skin);

		final Dialog helpDlg = new Dialog("Help", uiManager.skin);
		/* HELP DIALOG */
		helpDlg.text("Here you can start a server to play with your friends over the network.\n"
				+ "Choose a network port to start the server on and start the server.\n"
				+ "In the next screen you will be given the address and port other players have to connect to play on this server.\n"
				+ "The port must not being used by another program at the same time, or the server start-up will fail");
		TextButton helpDlgOkBtn = new TextButton("OK", uiManager.skin);
		helpDlg.button(helpDlgOkBtn);
		helpDlgOkBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				helpDlg.hide();
				return true;
			}
		});
		
		final Dialog failDlg = new Dialog("Server start-up failed", uiManager.skin);
		/* HELP DIALOG */
		failDlg.text("Server start-up failed. Pheraps the port is already being used?");
		TextButton failDlgOkBtn = new TextButton("OK", uiManager.skin);
		failDlg.button(failDlgOkBtn);
		failDlg.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				failDlg.hide();
				return true;
			}
		});

		// Add actions to the buttons
		backBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.server.stop();
				uiManager.main.setScreen(uiManager.titleScreen);
				return true;
			}
		});
		helpBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				helpDlg.show(stage);
				return true;
			}
		});
		setBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.setScreen.prevScreen = thisScreen;
				uiManager.main.setScreen(uiManager.setScreen);
				return true;
			}
		});
		connectBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(uiManager.main.server.startServer(Integer.valueOf(srvPort.getText()))) {
					// If the server and the client have been started successfully, we can show the
					// joining screen
					uiManager.preGameScreen.setGameType(GameType.SERVER);
					uiManager.main.setScreen(uiManager.preGameScreen);
				}else {
					//Show the dialog to say there's was something wrong
					failDlg.show(stage);
				}
				return true;
			}
		});
		
		Table firstRowTable = new Table();
		firstRowTable.add(backBtn).fillX().expandX().space(cw * 0.005f);
		firstRowTable.add(instLab).height(50).fillX().expandX().space(cw * 0.25f);
		firstRowTable.add(setBtn).width(50).height(50).fillX().expandX().space(cw * 0.005f);
		firstRowTable.add(helpBtn).width(50).height(50).fillX().expandX().space(cw * 0.005f);
		firstRowTable.setOrigin(Align.center | Align.top);

		table.row().colspan(4);
		table.add(firstRowTable);

		table.row().colspan(2);
		table.add(srvPortL).expandX();
		table.add(srvPort).expandX();

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
		Gdx.gl.glClearColor(0, 0, 0, 0);
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
