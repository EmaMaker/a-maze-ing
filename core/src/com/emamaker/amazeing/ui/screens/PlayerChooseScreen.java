package com.emamaker.amazeing.ui.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.mappings.Xbox;
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
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.ui.UIManager;

public class PlayerChooseScreen implements Screen {

	UIManager uiManager;
	Stage stage;
	Table table;
	
	Label[] labels = new Label[8];
	int currentLabel = 0;
	HashMap<MazePlayer, Label> players = new HashMap<MazePlayer, Label>();
	
	public PlayerChooseScreen(UIManager uiManager_) {
		uiManager = uiManager_;
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

	    float sw = Gdx.graphics.getWidth();
	    float sh = Gdx.graphics.getHeight();
		
		stage = new Stage(new ScreenViewport());
		Container<Table> tableContainer = new Container<Table>();
		Table table = new Table();

		float cw = stage.getWidth();
		float ch = stage.getHeight();
		
		tableContainer.setSize(cw, ch);
		tableContainer.setPosition(0, 0);
		
		Label instLab = new Label("Use WASD, ARROWS, or button on controller to join the match", uiManager.skin);
		TextButton backBtn = new TextButton("Back", uiManager.skin);
		TextButton setBtn = new TextButton("Settings", uiManager.skin);
		TextButton helpBtn = new TextButton("?", uiManager.skin);
		TextButton playBtn = new TextButton("Play", uiManager.skin);
		
		//Labels to know if players joined
		for(int i = 0; i < labels.length; i++) {
			labels[i] = new Label("Not joined yet", uiManager.skin);
		}

		// Add actions to the buttons
		backBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				hide();
				uiManager.main.setScreen(uiManager.titleScreen);
				return true;
			}
		});
		// Add actions to the buttons
		playBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(!players.isEmpty()) {
					hide();
					uiManager.main.gameManager.generateMaze(players.keySet());
				}
				return true;
			}
		});
		// Add actions to the buttons
		setBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Make this appear settings screen (TODO)");
				return true;
			}
		});
		// Add actions to the buttons
		helpBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Make this appear help dialog (TODO)");
				return true;
			}
		});
		
		Table firstRowTable = new Table();
		
		firstRowTable.add(backBtn).width(50).height(50).fillX().expandX().space(cw*0.005f);
		firstRowTable.add(instLab).height(50).fillX().expandX().space(cw*0.25f);
		firstRowTable.add(setBtn).height(50).fillX().expandX().space(cw*0.005f);
		firstRowTable.add(helpBtn).width(50).height(50).fillX().expandX().space(cw*0.005f);
		firstRowTable.setOrigin(Align.center | Align.top);
		table.row().colspan(4);

		table.add(firstRowTable);

		for(int i = 0; i < labels.length; i++) {
			if(i % 4 == 0) table.row().expandY().fillY();
			table.add( labels[i] ).space(1);
		}
		table.row().colspan(4);
		table.add(playBtn).fillX().width(cw*0.06f);
		
		
		tableContainer.setActor(table);
		stage.addActor(tableContainer);
		
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
		uiManager.main.multiplexer.addProcessor(stage);
	}

	@Override
	public void hide() {
		uiManager.main.multiplexer.removeProcessor(stage);		
	}

	MazePlayer p;
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
		
		if(!uiManager.main.gameManager.gameStarted) {
			//Consantly search for new players to be added
			//First search for keyboard players (WASD and ARROWS)
			if(Gdx.input.isKeyJustPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.A) || Gdx.input.isKeyJustPressed(Keys.S) || Gdx.input.isKeyJustPressed(Keys.D)) {
				p = getPlayerWithKeys(Keys.W, Keys.S, Keys.A, Keys.D);
				if(p == null) p = new MazePlayer(uiManager.main, Keys.W, Keys.S, Keys.A, Keys.D);
				togglePlayer(p);
			}
			if(Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
				p = getPlayerWithKeys(Keys.UP, Keys.DOWN, Keys.LEFT, Keys.RIGHT);
				if(p == null) p = new MazePlayer(uiManager.main, Keys.UP, Keys.DOWN, Keys.LEFT, Keys.RIGHT);
				togglePlayer(p);
			}
			for(Controller c : Controllers.getControllers()) {
				if(c.getButton(Xbox.Y)){
					p = getPlayerWithCtrl(c);
					if(p == null) p = new MazePlayer(uiManager.main, c);
					togglePlayer(p);
				}
			}
		}
	}
	
	public void togglePlayer(MazePlayer p) {
		try {
			if(alreadyAddedPlayer(p)) {
				players.get(p).setText("Not Joined Yet");
				players.remove(p);
			}else {
				players.put(p, labels[players.size()]);
				players.get(p).setText("Player Read");
			}
		}catch(Exception e) {
			System.out.println("All players already joined");
		}
	}

	public boolean alreadyAddedPlayerWithKeys(int... keys) {
		return getPlayerWithKeys(keys) != null;
	}
	
	public boolean alreadyAddedPlayer(MazePlayer p) {
		return players.containsKey(p);
	}
	
	public MazePlayer getPlayerWithKeys(int... keys) {
		for(MazePlayer p : players.keySet()) {
			for(int k : keys) {
				if(p.kup == k || p.kdown == k || p.ksx == k || p.kdx == k) return p;
			}
		}
		return null;
	}

	public boolean alreadyAddedPlayerWithCtrl(Controller ctrl) {
		return getPlayerWithCtrl(ctrl) != null;
	}
	
	public MazePlayer getPlayerWithCtrl(Controller ctrl) {
		for(MazePlayer p : players.keySet()) {
			if(p.ctrl == ctrl) return p;
		}
		return null;
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
		// TODO Auto-generated method stub

	}

}
