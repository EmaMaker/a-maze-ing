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
import com.emamaker.amazeing.ui.UIManager;

public class ServerJoinScreen implements Screen {

	Stage stage;
	UIManager uiManager;

	public ServerJoinScreen(UIManager uiManager_) {

		uiManager = uiManager_;

		stage = new Stage(new ScreenViewport());
		Container<Table> tableContainer = new Container<Table>();
		Table table = new Table();

		float cw = stage.getWidth();
		float ch = stage.getHeight();

		tableContainer.setSize(cw, ch);
		tableContainer.setPosition(0, 0);

		Label instLab = new Label("Enter ip address and port and connect to the server!", uiManager.skin);
		TextButton backBtn = new TextButton("Main menu", uiManager.skin);
		TextButton connectBtn = new TextButton("Connect to the server!", uiManager.skin);
		TextButton helpBtn = new TextButton("?", uiManager.skin);
		Label srvIpL = new Label("Server IP: ", uiManager.skin);
		Label srvPortL = new Label("Server Port: ", uiManager.skin);
		final TextArea srvIp = new TextArea("", uiManager.skin);
		final TextArea srvPort = new TextArea("", uiManager.skin);

		final Dialog helpDlg = new Dialog("Help", uiManager.skin);
		/* HELP DIALOG */
		helpDlg.text("Here you can connect to a server to play with your friends over the network.\n"
				+ "The server host should provide you with address and port info to connect to the server");
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
		failDlg.text("Connection to the server failed. Check your internet connection and address/port combination.\n"
				+ "Or Pheraps there's no server running there?");
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
				uiManager.main.client.stop();
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
		connectBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (!uiManager.main.client.start(srvIp.getText(), Integer.valueOf(srvPort.getText())))
					failDlg.show(stage);
				return true;
			}
		});

		Table firstRowTable = new Table();
		firstRowTable.add(backBtn).fillX().expandX().space(cw * 0.005f);
		firstRowTable.add(instLab).height(50).fillX().expandX().space(cw * 0.25f);
		firstRowTable.add(helpBtn).width(50).height(50).fillX().expandX().space(cw * 0.005f);
		firstRowTable.setOrigin(Align.center | Align.top);

		table.row().colspan(4);
		table.add(firstRowTable);

		table.row().colspan(2);
		table.add(srvIpL).expandX();
		table.add(srvIp).expandX();
		table.row().colspan(2);
		table.add(srvPortL).fillX().expandX();
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
