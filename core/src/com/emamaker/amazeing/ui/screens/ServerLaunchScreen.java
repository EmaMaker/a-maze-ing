package com.emamaker.amazeing.ui.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.emamaker.amazeing.manager.GameType;
import com.emamaker.amazeing.ui.UIManager;

public class ServerLaunchScreen extends MyScreen {

	MyScreen thisScreen;
	Label instLab, srvPortL;
	TextButton backBtn, connectBtn, setBtn, helpBtn;
	TextArea srvPort;
	
	Table firstRowTable;
	Container<Table> firstRowContainer;
	
	public ServerLaunchScreen(UIManager uiManager_) {
		super(uiManager_);
		thisScreen = this;
		chmult = .8f;
	}
	
	@Override
	public void createTable() {
		super.createTable();		
		
		firstRowTable = new Table();
		firstRowContainer = new Container<Table>();
		
		instLab = new Label("Enter the port the server must start on", uiManager.skin);
		backBtn = new TextButton("<", uiManager.skin);
		connectBtn = new TextButton("Launch the server!", uiManager.skin);
		setBtn = new TextButton("Settings", uiManager.skin);
		helpBtn = new TextButton("?", uiManager.skin);
		srvPortL = new Label("Port: ", uiManager.skin);
		srvPort = new TextArea("", uiManager.skin);

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
		
		firstRowContainer.setActor(firstRowTable);
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
		helpBtn.getLabel().setFontScale(labScale);

		firstRowTable.add(backBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);
		firstRowTable.add(instLab).space(cw * 0.25f).width(cw / 2);
		firstRowTable.add(helpBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);

		table.row().colspan(4);
		table.add(firstRowContainer);

		table.row().colspan(2).fillX().expandX();
		table.add(srvPortL).space(buttonDim).width(buttonDim*4f).height(buttonDim*0.5f).fillY().expandY();
		table.add(srvPort).space(buttonDim).width(buttonDim*4f).height(buttonDim*0.5f).fillY().expandY();
		table.row().colspan(4);
		table.add(connectBtn).fillX().width(buttonDim*3f).height(buttonDim);
	}


}
