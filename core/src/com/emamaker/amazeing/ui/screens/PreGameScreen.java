package com.emamaker.amazeing.ui.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.ui.UIManager;

public class PreGameScreen extends MyScreen {

	Label[] labels;
	MazePlayer[] players;
	int nPlayers, nPlayersOld;

	// GameType we are runnig. assuming server for default. If client, the StartGame
	// button shouldn't appear
	GameType type = GameType.SERVER;
	MyScreen thisScreen;

	Container<Table> firstRowContainer;
	Table firstRowTable;

	Label instLab;
	TextButton backBtn, setBtn, helpBtn, playBtn;
	Dialog helpDlg;
	
	public PreGameScreen(UIManager uiManager_) {
		super(uiManager_);
		chmult = 0.8f;
	}

	@Override
	public void createTable() {
		super.createTable();
		thisScreen = this;

		labels = new Label[MazeSettings.MAXPLAYERS];
		players = new MazePlayer[MazeSettings.MAXPLAYERS];
		nPlayers = 0;
		nPlayersOld = 0;

		firstRowTable = new Table();
		firstRowContainer = new Container<Table>();

		instLab = new Label("Waiting for players to join...", uiManager.skin);
		backBtn = new TextButton("Back", uiManager.skin);
		setBtn = new TextButton("Settings", uiManager.skin);
		helpBtn = new TextButton("?", uiManager.skin);
		playBtn = new TextButton("Start the match!", uiManager.skin);

		/* HELP DIALOG */
		helpDlg = new Dialog("Help", uiManager.skin);
		helpDlg.text("An online game is about to start!\n"
				+ "If you're a client, just wait for the server to start the game.\n"
				+ "If you're a server, wait for players and start the game pressing the \"Start the match!\" button.\n"
				+ "How to join (for both client and server):\n"
				+ "On a computer players can join or leave the game pressing WASD, Arrow buttons or\n"
				+ "a button on the controller\n" + "On mobile players can be toggled using the buttons below.");
		TextButton helpDlgOkBtn = new TextButton("OK", uiManager.skin);
		helpDlg.button(helpDlgOkBtn);
		helpDlgOkBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				helpDlg.hide();
				return true;
			}
		});

		if (type == GameType.CLIENT)
			instLab.setText("Waiting for server to start the game...");

		// Labels to know if players joined
		for (int i = 0; i < labels.length; i++) {
			labels[i] = new Label("-- empty slot --", uiManager.skin);
		}

		// Add actions to the buttons
		backBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				if (type == GameType.SERVER) {
					uiManager.main.server.stop();
					uiManager.main.setScreen(uiManager.srvLaunchScreen);
				} else if (type == GameType.CLIENT) {
					uiManager.main.client.stop();
					uiManager.main.setScreen(uiManager.srvJoinScreen);
				}
				return true;
			}
		});
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
				hide();
				uiManager.setScreen.setPrevScreen(thisScreen);
				uiManager.main.setScreen(uiManager.setScreen);
				System.out.println("Bup");
				return true;
			}
		});
		// Add actions to the buttons
		helpBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				helpDlg.show(stage);
				return true;
			}
		});

		firstRowContainer.setActor(firstRowTable);
		tableContainer.setActor(table);
		stage.addActor(tableContainer);
	}

	@Override
	public void buildTable() {
		super.buildTable();
		firstRowTable.clear();

		float d = containerDiagonal();
		float labScale = d * .00090f;
		float buttonDim = d * 0.05f;
		
		firstRowContainer.setSize(cw, ch * 0.2f);
		firstRowContainer.setPosition(tableContainer.getX(), ch * 0.1f);
		firstRowContainer.fill();
		
		instLab.setFontScale(labScale);
		backBtn.getLabel().setFontScale(labScale);
		setBtn.getLabel().setFontScale(labScale);
		helpBtn.getLabel().setFontScale(labScale);
		playBtn.getLabel().setFontScale(labScale);

		firstRowTable.add(backBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);
		firstRowTable.add(instLab).space(cw * 0.25f).width(cw / 2);
		if (type == GameType.SERVER)
			firstRowTable.add(setBtn).fillX().expandX().space(cw * 0.005f).height(buttonDim);
		firstRowTable.add(helpBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);

		table.row().colspan(MazeSettings.MAXPLAYERS == 2 ? 2 : 4);

		table.add(firstRowContainer);

		for (int i = 0; i < labels.length; i++) {
			labels[i].setFontScale(labScale);
			if (i % 4 == 0)
				table.row().expandY().fillY();
			table.add(labels[i]).space(1);
		}

		if (type == GameType.SERVER) {
			table.row().colspan(4);
			table.add(playBtn).fillX().width(buttonDim * 2f).height(buttonDim);
		}
	}

	@Override
	public void update() {
		instLab.setText(type.toString() + ": Waiting for players to join...");
		// Constantly update player labels, comparing with the remote players present on
		// server
		nPlayers = type == GameType.SERVER ? uiManager.main.server.remotePlayers.values().size()
				: uiManager.main.client.players.size();
		if (nPlayers != nPlayersOld) {
			// Update Labels
			for (int i = 0; i < labels.length; i++) {
				labels[i].setText(i < nPlayers ? "-- Player Ready! --" : "-- empty slot --");
			}
		}
		nPlayersOld = nPlayers;
	}

	public void setGameType(GameType t) {
		type = t;
		show();
	}

	public GameType getGameType() {
		return type;
	}

}
