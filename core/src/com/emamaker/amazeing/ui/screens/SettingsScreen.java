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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.emamaker.amazeing.maze.settings.MazeSetting;
import com.emamaker.amazeing.maze.settings.MazeSettingDimension;
import com.emamaker.amazeing.maze.settings.MazeSettingMaxPlayers;
import com.emamaker.amazeing.ui.UIManager;

public class SettingsScreen implements Screen{
	
	UIManager uiManager;
	Stage stage;
	Screen prevScreen;
	
	public SettingsScreen(UIManager uiManager_) {
		this.uiManager = uiManager_;
//		float sw = Gdx.graphics.getWidth();
//	    float sh = Gdx.graphics.getHeight();
		
		stage = new Stage(new ScreenViewport());
		Container<Table> tableContainer = new Container<Table>();
		Table table = new Table();

		float cw = stage.getWidth();
		float ch = stage.getHeight();
		
		tableContainer.setSize(cw, ch);
		tableContainer.setPosition(0, 0);
		
		Label instLab = new Label("Here you can customize game settings!", uiManager.skin);
		TextButton backBtn = new TextButton("<", uiManager.skin);
		TextButton helpBtn = new TextButton("?", uiManager.skin);

		final Dialog helpDlg = new Dialog("Help", uiManager.skin);
		/* HELP DIALOG */
		helpDlg.text("Here you can customize can settings:\n"
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

		// Add actions to the buttons
		backBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(prevScreen == null ? uiManager.titleScreen : prevScreen);
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
		
		
		
		
		
		Table firstRowTable = new Table();
		firstRowTable.add(backBtn).width(50).height(50).fillX().expandX().space(cw*0.005f);
		firstRowTable.add(instLab).height(50).fillX().expandX().space(cw*0.25f);
		firstRowTable.add(helpBtn).width(50).height(50).fillX().expandX().space(cw*0.005f);
		firstRowTable.setOrigin(Align.center | Align.top);

		table.row().colspan(4);
		table.add(firstRowTable);

		table.row().colspan(4);
		table.add(setSettings());
		
		tableContainer.setActor(table);
		stage.addActor(tableContainer);
	}
	
	VerticalGroup setSettings() {
		VerticalGroup group = new VerticalGroup().space(5).pad(5).fill();
		//Add various settings here
		MazeSetting setDim = new MazeSettingDimension("MAZE DIMENSIONS: ", new String[] {
				"10x10", "20x20", "30x30"
		}, this.uiManager);
		MazeSetting setPlayers = new MazeSettingMaxPlayers("MAX NUMBER OF PLAYERS: ", new String[] {
				"2", "4", "6", "8", "10", "15", "20"
		}, this.uiManager);
		group.addActor(setDim.getTable());
		group.addActor(setPlayers.getTable());
		
		return group;
	}
	
	public void setPrevScreen(Screen s) {
		this.prevScreen = s;
	}
	
	@Override
	public void show() {
		uiManager.main.multiplexer.addProcessor(stage);
	}

	@Override
	public void hide() {
		uiManager.main.multiplexer.removeProcessor(stage);		
	}

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();		
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
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
