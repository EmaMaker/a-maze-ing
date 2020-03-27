package com.emamaker.amazeing.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.emamaker.amazeing.ui.UIManager;

public class TitleScreen implements Screen {

	UIManager uiManager;
	Stage stage;
	
	public TitleScreen(UIManager uiManager_) {
		uiManager = uiManager_;
		
		stage = new Stage(new ScreenViewport());
		Table table = new Table();

		VerticalGroup mainScreenGroup = new VerticalGroup().space(5).pad(5).fill();
		TextButton setBut = new TextButton("Customize Game Settings (TODO)", uiManager.skin);
		mainScreenGroup.addActor(setBut);
		TextButton servBut = new TextButton("Start Server and play online with friends (TODO)", uiManager.skin);
		mainScreenGroup.addActor(servBut);
		TextButton localBut = new TextButton("Start a game on the local machine", uiManager.skin);
		mainScreenGroup.addActor(localBut);
		TextButton quitBut = new TextButton("Quit game", uiManager.skin);

		table.setPosition(stage.getWidth() *0.5f-mainScreenGroup.getWidth(), stage.getHeight() * 0.2f, Align.center);
		table.add(mainScreenGroup);
		stage.addActor(table);
		
		//Add actions to the buttons
		localBut.addListener(new InputListener(){
		    @Override
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		    	hide();
//		    	uiManager.main.gameManager.generateMaze();
		    	uiManager.main.setScreen(uiManager.playersScreen);
		    	return true;
		    }
		});
		quitBut.addListener(new InputListener(){
		    @Override
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		    	System.out.println("Bye bye!");
		    	Gdx.app.exit();
		        return true;
		    }
		});
		servBut.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Make this appear server joining and setup screen (TODO)");
				return true;
			}
		});
		servBut.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Make this appear settings screen (TODO)");
				return true;
			}
		});
		
		//Add actors to the group
		mainScreenGroup.addActor(setBut);
		mainScreenGroup.addActor(servBut);
		mainScreenGroup.addActor(localBut);
		mainScreenGroup.addActor(quitBut);
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
