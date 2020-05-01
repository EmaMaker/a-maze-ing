package com.emamaker.amazeing.ui.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.emamaker.amazeing.maze.settings.MazeSetting;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.ui.UIManager;

public class SettingsScreen extends MyScreen {

	Label instLab;
	TextButton backBtn, resetBtn, saveBtn, helpBtn;
	ScrollPane scrollPane;

	Container<Table> firstRowContainer;
	Table firstRowTable;

	public SettingsScreen(UIManager uiManager_) {
		super(uiManager_);
		chmult = 0.8f;
	}

	@Override
	public void createTable() {
		super.createTable();
		firstRowContainer = new Container<Table>();
		firstRowTable = new Table();
		
		firstRowContainer.setActor(firstRowTable);

		instLab = new Label("Here you can customize game settings!", uiManager.skin);
		backBtn = new TextButton("<", uiManager.skin);
		resetBtn = new TextButton("Reset All", uiManager.skin);
		saveBtn = new TextButton("Save", uiManager.skin);
		helpBtn = new TextButton("?", uiManager.skin);
		scrollPane = new ScrollPane(setSettings(), uiManager.skin);
		final Dialog helpDlg = new Dialog("Help", uiManager.skin);
		/* HELP DIALOG */
		helpDlg.text("Here you can customize game settings:\n"
				+ "Maze Size: changes the size of the maze. Mazes are always squares. This affects both local and online games.\n"
				+ "Max. Players: changes the max number of players that can join the game. This affects both local and online games.");
		TextButton helpDlgOkBtn = new TextButton("OK", uiManager.skin);
		helpDlg.button(helpDlgOkBtn);
		helpDlgOkBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				helpDlg.hide();
				return true;
			}
		});
		/* BACK DIALOG */
		final Dialog backDlg = new Dialog("Go Back", uiManager.skin);
		backDlg.text("Are you sure you want to go back without saving changes?\nThis cannot be reverted");
		TextButton backDlgCancelBtn = new TextButton("Cancel", uiManager.skin);
		TextButton backDlgOkBtn = new TextButton("OK", uiManager.skin);
		backDlg.button(backDlgCancelBtn);
		backDlg.button(backDlgOkBtn);
		backDlgOkBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				backDlg.hide();
				MazeSettings.restoreStates();
				uiManager.main.setScreen(prevScreen == null ? uiManager.titleScreen : prevScreen);
				return true;
			}
		});
		backDlgCancelBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				backDlg.hide();
				return true;
			}
		});
		/* RESET DIALOG */
		final Dialog resetDlg = new Dialog("Reset All Settings", uiManager.skin);
		resetDlg.text("Are you sure you want to reset all settings?\nThis cannot be reverted");
		TextButton resetDlgCancelBtn = new TextButton("Cancel", uiManager.skin);
		TextButton resetDlgOkBtn = new TextButton("OK", uiManager.skin);
		resetDlg.button(resetDlgOkBtn);
		resetDlg.button(resetDlgCancelBtn);
		resetDlgOkBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				MazeSettings.resetAll();
				resetDlg.hide();
				return true;
			}
		});
		resetDlgCancelBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				resetDlg.hide();
				return true;
			}
		});

		/* ACTIONS TO BUTTONS */
		backBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				backDlg.show(stage);
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
		resetBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				resetDlg.show(stage);
				return true;
			}
		});
		saveBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(prevScreen == null ? uiManager.titleScreen : prevScreen);
				return true;
			}
		});

	}

	@Override
	public void buildTable() {
		super.buildTable();

		firstRowTable.clear();

		
		float d = containerDiagonal();
		float labScale = d * .00080f;
		float buttonDim = d * 0.05f;

		firstRowContainer.setSize(cw, ch * 0.2f);
		firstRowContainer.setPosition(tableContainer.getX(), ch * 0.1f);
		firstRowContainer.fill();

		instLab.setFontScale(labScale);
		backBtn.getLabel().setFontScale(labScale);
		helpBtn.getLabel().setFontScale(labScale);
		resetBtn.getLabel().setFontScale(labScale);
		saveBtn.getLabel().setFontScale(labScale);

		firstRowTable.add(backBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);
		firstRowTable.add(instLab).space(cw * 0.25f);
		firstRowTable.add(helpBtn).fillX().expandX().space(cw * 0.005f).width(buttonDim).height(buttonDim);;

		table.row().colspan(4);
		table.add(firstRowContainer);
		table.row().colspan(2);
		table.add(scrollPane).fill().expand();


		table.row();
		table.add(resetBtn).fillX().expandX().width(buttonDim*2f).height(buttonDim);
		table.add(saveBtn).fillX().expandX().width(buttonDim*2f).height(buttonDim);

//		table.add(resetBtn).fillX().expandX().pad(buttonDim/2).height(buttonDim);
//		table.add(saveBtn).fillX().expandX().pad(buttonDim/2).height(buttonDim);
	}
	
	public Table setSettings() {
		Table table = new Table();

		table.row().colspan(2);
		table.add(MazeSettings.setDim.getTable());
		table.row().colspan(2);
		table.add(MazeSettings.setPlayers.getTable());
		
		return table;
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		for(MazeSetting s : MazeSettings.settings) s.buildTable();
	}

	void saveStates() {
		MazeSettings.setDim.saveState();
		MazeSettings.setPlayers.saveState();
	}

}
