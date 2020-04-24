package com.emamaker.amazeing.ui.screens;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.ui.UIManager;

public class PreGameScreen implements Screen {

	UIManager uiManager;
	Stage stage;
	Table table;

	Label[] labels;
	MazePlayer[] players, tmp;

	// GameType we are runnig. assuming server for default. If client, the StartGame
	// button shouldn't appear
	GameType type = GameType.SERVER;

	Screen thisScreen;

	public PreGameScreen(UIManager uiManager_) {
		uiManager = uiManager_;
	}

	@Override
	public void show() {
		thisScreen = this;

		labels = new Label[MazeSettings.MAXPLAYERS];
		players = new MazePlayer[MazeSettings.MAXPLAYERS];
		tmp = new MazePlayer[MazeSettings.MAXPLAYERS];

		stage = new Stage(new ScreenViewport());
		Container<Table> tableContainer = new Container<Table>();
		Table table = new Table();

		float cw = stage.getWidth();
		float ch = stage.getHeight();

		tableContainer.setSize(cw, ch);
		tableContainer.setPosition(0, 0);

		Label instLab = new Label("Waiting for players to join...", uiManager.skin);
		TextButton backBtn = new TextButton("Back", uiManager.skin);
		TextButton setBtn = new TextButton("Settings", uiManager.skin);
		TextButton helpBtn = new TextButton("?", uiManager.skin);
		TextButton playBtn = new TextButton("Start the match!", uiManager.skin);

		if(type == GameType.CLIENT) instLab.setText("Waiting for server to start the game...");
		
		// Labels to know if players joined
		for (int i = 0; i < labels.length; i++) {
			labels[i] = new Label("-- empty slot --", uiManager.skin);
		}

		// Add actions to the buttons
		backBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(type == GameType.SERVER ? uiManager.srvLaunchScreen : uiManager.srvJoinScreen);
				return true;
			}
		});
		if (type == GameType.SERVER) {
			// Add actions to the buttons
			playBtn.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					hide();
					uiManager.main.server.startGame();
					return true;
				}
			});
			// Add actions to the buttons
			setBtn.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					uiManager.setScreen.setPrevScreen(thisScreen);
					uiManager.main.setScreen(uiManager.setScreen);
					return true;
				}
			});
		}
		// Add actions to the buttons
		helpBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Make this appear help dialog (TODO)");
				return true;
			}
		});

		Table firstRowTable = new Table();

		firstRowTable.add(backBtn).width(50).height(50).fillX().expandX().space(cw * 0.005f);
		firstRowTable.add(instLab).height(50).fillX().expandX().space(cw * 0.25f);
		if (type == GameType.SERVER)
			firstRowTable.add(setBtn).height(50).fillX().expandX().space(cw * 0.005f);
		firstRowTable.add(helpBtn).width(50).height(50).fillX().expandX().space(cw * 0.005f);
		firstRowTable.setOrigin(Align.center | Align.top);
		table.row().colspan(4);

		table.add(firstRowTable);

		for (int i = 0; i < labels.length; i++) {
			if (i % 4 == 0)
				table.row().expandY().fillY();
			table.add(labels[i]).space(1);
		}

		if (type == GameType.SERVER) {
			table.row().colspan(4);
			table.add(playBtn).fillX().width(cw * 0.06f);
		}

		tableContainer.setActor(table);
		stage.addActor(tableContainer);

		uiManager.main.multiplexer.addProcessor(stage);
	}

	@Override
	public void hide() {
		uiManager.main.multiplexer.removeProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();

		// Constantly update player labels, comparing with the remote players present on
		// server
		tmp = type == GameType.SERVER
				? Arrays.copyOf(uiManager.main.server.remotePlayers.values().toArray(),
						uiManager.main.server.remotePlayers.values().size(), MazePlayer[].class)
				: Arrays.copyOf(uiManager.main.client.players.toArray(), uiManager.main.client.players.size(),
						MazePlayer[].class);
		if (!(Arrays.equals(players, tmp))) {
			// Update Labels
			for (int i = 0; i < tmp.length; i++) {
				players[i] = tmp[i];
				labels[i].setText(players[i].getName());
			}
		}
	}

	public void setGameType(GameType t) {
		type = t;
		show();
	}

	public GameType getGameType() {
		return type;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
