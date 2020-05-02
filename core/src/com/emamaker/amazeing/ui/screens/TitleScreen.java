package com.emamaker.amazeing.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.emamaker.amazeing.ui.UIManager;

public class TitleScreen extends MyScreen {

	Label amazeingLab, quitDlgText;
	TextButton setBut, makeSrvBtn, joinSrvBtn, localBut, quitBut, quitDlgOkBtn, quitDlgCancelBtn;
	Dialog quitDlg;
	
	public TitleScreen(UIManager uiManager_) {
		super(uiManager_);
	}

	@Override
	public void createTable() {
		amazeingLab = new Label("A - MAZE - ING", uiManager.skin);
		setBut = new TextButton("CUSTOMIZE GAME SETTINGS", uiManager.skin);
		makeSrvBtn = new TextButton("START SERVER", uiManager.skin);
		joinSrvBtn = new TextButton("JOIN SERVER", uiManager.skin);
		localBut = new TextButton("LOCAL GAME", uiManager.skin);
		quitBut = new TextButton("QUIT GAME", uiManager.skin);

		/* QUIT DIALOG */
		quitDlg = new Dialog("Quit?", uiManager.skin);
		quitDlgText = new Label ("Are you sure you want to quit the game?", uiManager.skin);
		quitDlg.text(quitDlgText);		
		quitDlgCancelBtn = new TextButton("Cancel", uiManager.skin);
		quitDlgOkBtn = new TextButton("OK", uiManager.skin);
		quitDlg.button(quitDlgCancelBtn);
		quitDlg.button(quitDlgOkBtn);
		quitDlgOkBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Bye bye");
				Gdx.app.exit();
				return true;
			}
		});
		quitDlgCancelBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				quitDlg.hide();
				return true;
			}
		});
		

		// Add actions to the buttons
		localBut.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(uiManager.playersScreen);
				return true;
			}
		});
		quitBut.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				quitDlg.show(stage);
				buildTable();
				return true;
			}
		});
		makeSrvBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(uiManager.srvLaunchScreen);
				return true;
			}
		});
		joinSrvBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(uiManager.srvJoinScreen);
				return true;
			}
		});
		setBut.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(uiManager.setScreen);
				return true;
			}
		});

	}

	@Override
	public void buildTable() {
		super.buildTable();

		float d = containerDiagonal();

		amazeingLab.setFontScale(d * .005f);

		float spaceBetweenBtns = d * 0.015f;
		float btnHeight = spaceBetweenBtns * 2.4f;
		float btnWidth = cw*0.46f;
		float n = d * 0.0015f;
		
		float labScale = d * .00090f;

		quitDlg.setSize(cw*0.35f, ch*0.15f);
		quitDlg.setPosition((sw-quitDlg.getWidth())/2, (sh-quitDlg.getHeight())/2);
		quitDlgText.setFontScale(labScale*0.9f);
		quitDlgOkBtn.getLabel().setFontScale(labScale*0.9f);
		quitDlgCancelBtn.getLabel().setFontScale(labScale*0.9f);
		
		setBut.getLabel().setFontScale(n);
		makeSrvBtn.getLabel().setFontScale(n);
		joinSrvBtn.getLabel().setFontScale(n);
		localBut.getLabel().setFontScale(n);
		quitBut.getLabel().setFontScale(n);

		// Add actors to the group
		table.row().colspan(1);
		table.add(amazeingLab).spaceBottom(spaceBetweenBtns * 3);
		table.row();
		table.add(setBut).spaceBottom(spaceBetweenBtns).height(btnHeight).width(btnWidth);
		table.row();
		table.add(makeSrvBtn).spaceBottom(spaceBetweenBtns).height(btnHeight).width(btnWidth);
		table.row();
		table.add(joinSrvBtn).spaceBottom(spaceBetweenBtns).height(btnHeight).width(btnWidth);
		table.row();
		table.add(localBut).spaceBottom(spaceBetweenBtns).height(btnHeight).width(btnWidth);
		table.row();
		table.add(quitBut).spaceBottom(ch * 0.05f).height(btnHeight).width(btnWidth);

	}

}
