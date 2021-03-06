package com.emamaker.amazeing.ui.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.AMazeIng.Platform;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.emamaker.amazeing.player.PlayerUtils;
import com.emamaker.amazeing.ui.UIManager;

import java.util.ArrayList;
import java.util.HashSet;

public class PlayerChooseScreen extends MyScreen {

	Label[] labels;
	CheckBox[] buttons;
	int currentLabel = 0;
	ArrayList<MazePlayer> players = new ArrayList<MazePlayer>();

	MyScreen thisScreen;

	Container<Table> firstRowContainer;
	Table firstRowTable;

	Label instLab, helpDlgText;
	TextButton backBtn, setBtn, helpBtn, playBtn, helpDlgOkBtn;
	Dialog helpDlg;

	int totalPlayers = 0;

	public PlayerChooseScreen(UIManager uiManager_) {
		super(uiManager_);

		chmult = 0.8f;
	}

	@Override
	public void createTable() {
		super.createTable();
		thisScreen = this;

		firstRowContainer = new Container<Table>();
		firstRowTable = new Table();

		firstRowContainer.setActor(firstRowTable);

		instLab = new Label(
				AMazeIng.PLATFORM == Platform.DESKTOP ? "Use WASD, ARROWS, or button on controller to join the match"
						: "Tap the buttons below to join the match",
				uiManager.skin);
		backBtn = new TextButton("<", uiManager.skin);
		setBtn = new TextButton("Settings", uiManager.skin);
		helpBtn = new TextButton("?", uiManager.skin);
		playBtn = new TextButton("Play!", uiManager.skin);
		/* HELP DIALOG */
		helpDlg = new Dialog("Help", uiManager.skin);
//		helpDlg.setResizable(true);
		helpDlgText = new Label("Here you can start a singleplayer or multiplayer game on the local machine:\n"
				+ "For keyboard players, pressing W,A,S,D or the directional arrows will toggle two different players.\n"
				+ "Pressing a button on a controller will toggle a player.\n"
				+ "Mobile players can tap the buttons below to join the game.\n"
				+ "You can edit game settings from the \"Settings\" menu or use the \"<\" button to go back to the main menu\n"
				+ "Press the \"Play!\" button to start the game with the players that have currently joined.\n"
				+ "Once a game is finished you will go back to this menu", uiManager.skin);
		helpDlg.text(helpDlgText);
		helpDlgOkBtn = new TextButton("OK", uiManager.skin);
		helpDlg.button(helpDlgOkBtn);
		helpDlgOkBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				helpDlg.hide();
//				hideDialog();
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
				if (AMazeIng.PLATFORM != Platform.DESKTOP) {
					for (MazePlayer p : players)
						p.dispose();
					players.clear();

					for (int i = 0; i < buttons.length; i++)
						if (buttons[i].isChecked())
							players.add(new MazePlayerLocal(new Touchpad(0f, uiManager.skin), i));
				}
				if (!players.isEmpty()) {
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
				buildTable();
				return true;
			}
		});

	}

	@Override
	public void buildTable() {
		super.buildTable();
		firstRowTable.clear();

		totalPlayers = 0;

		if (AMazeIng.PLATFORM == Platform.DESKTOP) {
			labels = new Label[MazeSettings.MAXPLAYERS];
			// Labels to know if players joined
			for (int i = 0; i < labels.length; i++) {
				labels[i] = new Label("-- empty slot --", uiManager.skin);
			}
		} else {
			buttons = new CheckBox[MazeSettings.MAXPLAYERS];
			// Labels to know if players joined
			for (int i = 0; i < buttons.length; i++) {
				buttons[i] = new CheckBox("-- empty slot --", uiManager.skin);
			}
		}

		float d = containerDiagonal();
		float labScale = d * .00090f;
		float buttonDim = d * 0.05f;

		firstRowContainer.setSize(cw, ch * 0.2f);
		firstRowContainer.setPosition(tableContainer.getX(), ch * 0.1f);
		firstRowContainer.fill();

		helpDlg.setSize(cw * 0.7f, ch * 0.3f);
		helpDlg.setPosition((sw - helpDlg.getWidth()) / 2, (sh - helpDlg.getHeight()) / 2);
		helpDlgText.setFontScale(labScale * 0.8f);
		helpDlgOkBtn.getLabel().setFontScale(labScale * 0.8f);

		instLab.setFontScale(labScale);
		backBtn.getLabel().setFontScale(labScale);
		setBtn.getLabel().setFontScale(labScale);
		helpBtn.getLabel().setFontScale(labScale);
		playBtn.getLabel().setFontScale(labScale);

		firstRowTable.add(backBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);
		firstRowTable.add(instLab).space(cw * 0.25f).width(cw / 2);
		firstRowTable.add(setBtn).fillX().expandX().space(cw * 0.005f).height(buttonDim);
		firstRowTable.add(helpBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);

		table.row().colspan(MazeSettings.MAXPLAYERS == 2 ? 2 : 4);

		table.add(firstRowContainer);

		if (AMazeIng.PLATFORM == Platform.DESKTOP) {
			for (int i = 0; i < labels.length; i++) {
				labels[i].setFontScale(labScale);
				if (i % 4 == 0)
					table.row().expandY().fillY();
				table.add(labels[i]).space(1);
			}
		} else {
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].getLabel().setFontScale(labScale);
				if (i % 4 == 0)
					table.row().expandY().fillY();
				table.add(buttons[i]).space(1);
			}
		}

		table.row().colspan(MazeSettings.MAXPLAYERS == 2 ? 2 : 4);
//		table.add(playBtn).fillX().width(buttonDim*2f).height(buttonDim);
		table.add(playBtn).fillX().expandX().height(buttonDim);
	}

	MazePlayerLocal p;

	@Override
	public void update() {
		// Consantly search for new players to be added
		// First search for keyboard players (WASD and ARROWS)
		if (PlayerUtils.wasdJustPressed())
			PlayerUtils.togglePlayerWithKeys(players, PlayerUtils.WASDKEYS);
		if (PlayerUtils.arrowsJustPressed())
			PlayerUtils.togglePlayerWithKeys(players, PlayerUtils.ARROWKEYS);

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
		if (AMazeIng.PLATFORM == Platform.DESKTOP) {
			for (int i = 0; i < labels.length; i++) {
				labels[i].setText(i < players.size() ? "-- Player Ready! --" : "-- empty slot --");
			}
		} else {
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].setText(buttons[i].isChecked() ? " -- Player Ready -- " : " -- empty slot -- ");
			}
		}
	}

}
