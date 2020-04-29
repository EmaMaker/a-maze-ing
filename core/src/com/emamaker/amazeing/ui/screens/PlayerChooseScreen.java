package com.emamaker.amazeing.ui.screens;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.emamaker.amazeing.player.PlayerUtils;
import com.emamaker.amazeing.ui.UIManager;

public class PlayerChooseScreen implements Screen {

	UIManager uiManager;
	Stage stage;
	Table table;

	Label[] labels;
	int currentLabel = 0;
	ArrayList<MazePlayer> players = new ArrayList<MazePlayer>();

	Screen thisScreen;

	public PlayerChooseScreen(UIManager uiManager_) {
		uiManager = uiManager_;
		show();
//
//		stage = new Stage(new ScreenViewport());
//
//        Container<Table> tableContainer = new Container<Table>();
//
//        float sw = Gdx.graphics.getWidth();
//        float sh = Gdx.graphics.getHeight();
//
//        float cw = sw * 0.7f;
//        float ch = sh * 0.5f;
//
//        tableContainer.setSize(cw, ch);
//        tableContainer.setPosition((sw - cw) / 2.0f, (sh - ch) / 2.0f);
//        tableContainer.fillX();
//
//        Table table = new Table(uiManager.skin);
//
//        Label topLabel = new Label("A LABEL", uiManager.skin);
//        topLabel.setAlignment(Align.center);
//        Slider slider = new Slider(0, 100, 1, false, uiManager.skin);
//        Label anotherLabel = new Label("ANOTHER LABEL", uiManager.skin);
//        anotherLabel.setAlignment(Align.center);
//
//        CheckBox checkBoxA = new CheckBox("Checkbox Left", uiManager.skin);
//        CheckBox checkBoxB = new CheckBox("Checkbox Center", uiManager.skin);
//        CheckBox checkBoxC = new CheckBox("Checkbox Right", uiManager.skin);
//
//        Table buttonTable = new Table(uiManager.skin);
//
//        TextButton buttonA = new TextButton("LEFT", uiManager.skin);
//        TextButton buttonB = new TextButton("RIGHT", uiManager.skin);
//
//        table.row().colspan(3).expandX().fillX();
//        table.add(topLabel).fillX();
//        table.row().colspan(3).expandX().fillX();
//        table.add(slider).fillX();
//        table.row().colspan(3).expandX().fillX();
//        table.add(anotherLabel).fillX();
//        table.row().expandX().fillX();
//
//        table.add(checkBoxA).expandX().fillX();
//        table.add(checkBoxB).expandX().fillX();
//        table.add(checkBoxC).expandX().fillX();
//        table.row().expandX().fillX();;
//
//        table.add(buttonTable).colspan(3);
//
//        buttonTable.pad(16);
//        buttonTable.row().space(5);
//        buttonTable.add(buttonA).width(cw/3.0f);
//        buttonTable.add(buttonB).width(cw/3.0f);
//
//        tableContainer.setActor(table);
//        stage.addActor(tableContainer);

//	    float sw = Gdx.graphics.getWidth();
//	    float sh = Gdx.graphics.getHeight();

//		Table buttonTable = new Table();
//
//		table.add(buttonTable).colspan(3);
//		buttonTable.pad(16);
//		buttonTable.add(backBtn).width(80);
//		buttonTable.add(playBtn).width(80);
//
//		tableContainer.setActor(table);
//		stage.addActor(tableContainer);

	}

	@Override
	public void show() {
		thisScreen = this;

		labels = new Label[MazeSettings.MAXPLAYERS];
		stage = new Stage(new ScreenViewport());
		Container<Table> tableContainer = new Container<Table>();
		Table table = new Table();

		float cw = stage.getWidth();
		float ch = stage.getHeight();

		tableContainer.setSize(cw, ch);
		tableContainer.setPosition(0, 0);

		Label instLab = new Label("Use WASD, ARROWS, or button on controller to join the match", uiManager.skin);
		TextButton backBtn = new TextButton("<", uiManager.skin);
		TextButton setBtn = new TextButton("Settings", uiManager.skin);
		TextButton helpBtn = new TextButton("?", uiManager.skin);
		TextButton playBtn = new TextButton("Play!", uiManager.skin);

		final Dialog helpDlg = new Dialog("Help", uiManager.skin);
		/* HELP DIALOG */
		helpDlg.text("Here you can start a singleplayer or multiplayer game on the local machine:\n"
				+ "For keyboard players, pressing W,A,S,D or the directional arrows will toggle two different players.\n"
				+ "Pressing a button on a controller will toggle a player.\n"
				+ "You can edit game settings from the \"Settings\" menu or use the \"<\" button to go back to the main menu\n"
				+ "Press the \"Play!\" button to start the game with the players that have currently joined.\n"
				+ "Once a game is finished you will go back to this menu");
		TextButton helpDlgOkBtn = new TextButton("OK", uiManager.skin);
		helpDlg.button(helpDlgOkBtn);
		helpDlgOkBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				helpDlg.hide();
				return true;
			}
		});

		// Add actions to the buttons
		backBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(uiManager.titleScreen);
				return true;
			}
		});
		playBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (!players.isEmpty()) {
					hide();
					uiManager.main.gameManager.generateMaze(new HashSet<>(players));
				}
				return true;
			}
		});
		setBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.setScreen.setPrevScreen(thisScreen);
				uiManager.main.setScreen(uiManager.setScreen);
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

		// Labels to know if players joined
		for (int i = 0; i < labels.length; i++) {
			labels[i] = new Label("-- empty slot --", uiManager.skin);
		}

		/* BUILD UP TABLE */
		Table firstRowTable = new Table();

		firstRowTable.add(backBtn).width(50).height(50).fillX().expandX().space(cw * 0.005f);
		firstRowTable.add(instLab).height(50).fillX().expandX().space(cw * 0.25f);
		firstRowTable.add(setBtn).height(50).fillX().expandX().space(cw * 0.005f);
		firstRowTable.add(helpBtn).width(50).height(50).fillX().expandX().space(cw * 0.005f);
		firstRowTable.setOrigin(Align.center | Align.top);
		table.row().colspan(MazeSettings.MAXPLAYERS == 2 ? 2 : 4);

		table.add(firstRowTable);

		for (int i = 0; i < labels.length; i++) {
			if (i % 4 == 0)
				table.row().expandY().fillY();
			table.add(labels[i]).space(1);
		}
		table.row().colspan(MazeSettings.MAXPLAYERS == 2 ? 2 : 4);
		table.add(playBtn).fillX().width(cw * 0.06f);

		tableContainer.setActor(table);
		stage.addActor(tableContainer);

		uiManager.main.multiplexer.addProcessor(stage);
	}

	@Override
	public void hide() {
		uiManager.main.multiplexer.removeProcessor(stage);
	}

	MazePlayerLocal p;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();

		// Consantly search for new players to be added
		// First search for keyboard players (WASD and ARROWS)
		if (Gdx.input.isKeyJustPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.A)
				|| Gdx.input.isKeyJustPressed(Keys.S) || Gdx.input.isKeyJustPressed(Keys.D))
			PlayerUtils.togglePlayerWithKeys(players, Keys.W, Keys.S, Keys.A, Keys.D);
		if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.LEFT)
				|| Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.RIGHT))
			PlayerUtils.togglePlayerWithKeys(players, Keys.UP, Keys.DOWN, Keys.LEFT, Keys.RIGHT);

//		for (Controller c : Controllers.getControllers()) {
//			System.out.println(c.getButton(Xbox.A));

		// if (c.getButton(Xbox.Y)) {
//				p = getPlayerWithCtrl(c);
//				if (p == null)
//					p = new MazePlayerLocal(uiManager.main, c);
//				togglePlayer(p);
//			}
//		}
		
		// Update labels
		for (int i = 0; i < labels.length; i++) {
			labels[i].setText(i < players.size() ? "-- Player Ready! --" : "-- empty slot --");
		}
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
