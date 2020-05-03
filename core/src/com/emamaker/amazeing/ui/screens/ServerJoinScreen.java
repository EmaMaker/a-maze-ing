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

public class ServerJoinScreen extends MyScreen {

	Label instLab, srvIpL, helpDlgText, failDlgText;
	TextButton backBtn, connectBtn, helpBtn, helpDlgOkBtn, failDlgOkBtn;
	TextArea srvIp;

	Container<Table> firstRowContainer;
	Table firstRowTable;
	
	Dialog helpDlg, failDlg;
	
	public ServerJoinScreen(UIManager uiManager_) {
		super(uiManager_);
		chmult=.8f;
	}

	@Override
	public void createTable() {
		super.createTable();
		
		firstRowTable = new Table();
		firstRowContainer = new Container<Table>();
		firstRowContainer.setActor(firstRowTable);
		
		instLab = new Label("Enter ip address and port and connect to the server!", uiManager.skin);
		backBtn = new TextButton("<", uiManager.skin);
		connectBtn = new TextButton("Connect to the server!", uiManager.skin);
		helpBtn = new TextButton("?", uiManager.skin);
		srvIpL = new Label("Server IP: ", uiManager.skin);
		srvIp = new TextArea("", uiManager.skin);

		helpDlg = new Dialog("Help", uiManager.skin);
		/* HELP DIALOG */
		helpDlgText = new Label("Here you can connect to a server to play with your friends over the network.\n"
				+ "The server host should provide you with address and port info to connect to the server", uiManager.skin);
		
		helpDlg.text(helpDlgText);
		helpDlgOkBtn = new TextButton("OK", uiManager.skin);
		
		helpDlg.button(helpDlgOkBtn);
		helpDlgOkBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				helpDlg.hide();
				return true;
			}
		});

		failDlg = new Dialog("Server start-up failed", uiManager.skin);
		/* HELP DIALOG */
		failDlgText = new Label("Connection to the server failed. Check your internet connection and address/port combination.\n"
				+ "Or Pheraps there's no server running there?", uiManager.skin);
		failDlg.text(failDlgText);
		failDlgOkBtn = new TextButton("OK", uiManager.skin);
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
				buildTable();
				return true;
			}
		});
		connectBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				try {
					String addr = srvIp.getText().split(":")[0];
					String port = srvIp.getText().split(":")[1];

					if (uiManager.main.client.start(addr, Integer.valueOf(port))) {
						hide();
						uiManager.preGameScreen.setGameType(uiManager.main.server.isRunning() ? GameType.SERVER : GameType.CLIENT);
						uiManager.main.setScreen(uiManager.preGameScreen);
					} else {
						failDlg.show(stage);
						buildTable();
					}
				} catch (Exception e) {
					failDlg.show(stage);
					buildTable();
				}
				return true;
			}
		});
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

		helpDlg.setSize(cw*0.7f, ch*0.2f);
		helpDlg.setPosition((sw-helpDlg.getWidth())/2, (sh-helpDlg.getHeight())/2);
		helpDlgText.setFontScale(labScale*0.9f);
		helpDlgOkBtn.getLabel().setFontScale(labScale*0.9f);

		failDlg.setSize(cw*0.7f, ch*0.2f);
		failDlg.setPosition((sw-failDlg.getWidth())/2, (sh-failDlg.getHeight())/2);
		failDlgText.setFontScale(labScale*0.9f);
		failDlgOkBtn.getLabel().setFontScale(labScale*0.9f);
		
		instLab.setFontScale(labScale);
		srvIpL.setFontScale(labScale);
		backBtn.getLabel().setFontScale(labScale);
		helpBtn.getLabel().setFontScale(labScale);
		connectBtn.getLabel().setFontScale(labScale);

		firstRowTable.add(backBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);
		firstRowTable.add(instLab).space(cw * 0.25f).width(cw / 2);
		firstRowTable.add(helpBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);

		table.row().colspan(4);
		table.add(firstRowContainer);

		table.row().colspan(2).fillX().expandX();
		table.add(srvIpL).space(buttonDim).width(buttonDim*4f).height(buttonDim*0.5f).fillY().expandY();
		table.add(srvIp).space(buttonDim).width(buttonDim*4f).height(buttonDim*0.5f).fillY().expandY();
		table.row().colspan(4);
//		table.add(connectBtn).fillX().width(buttonDim*3f).height(buttonDim);
		table.add(connectBtn).fillX().expandX().height(buttonDim);
	}

}
