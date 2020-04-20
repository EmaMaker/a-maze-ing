package com.emamaker.amazeing.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
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

public class ServerLaunchScreen implements Screen {

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
		TextButton backBtn = new TextButton("Main menu and quit server", uiManager.skin);
		TextButton connectBtn = new TextButton("Launch the server!", uiManager.skin);
		TextButton gameBtn = new TextButton("Launch the game!", uiManager.skin);
		TextButton helpBtn = new TextButton("?", uiManager.skin);
		final TextArea srvPort = new TextArea("9999", uiManager.skin);

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
				System.out.println("Make this appear help dialog (TODO)");
				return true;
			}
		});
		connectBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				uiManager.main.server.startServer(Integer.valueOf(srvPort.getText()));
				return true;
			}
		});
		gameBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				uiManager.main.server.startGame();
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

		table.row().colspan(4);
		table.add(srvPort).fillX().expandX();

		table.row().colspan(4);
		table.add(connectBtn).fillX().expandX();

		table.row().colspan(4);
		table.add(gameBtn).fillX().expandX();
		
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
