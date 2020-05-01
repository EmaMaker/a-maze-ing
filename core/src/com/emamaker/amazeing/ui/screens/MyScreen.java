package com.emamaker.amazeing.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.emamaker.amazeing.ui.UIManager;

public class MyScreen implements Screen{

	//This method makes the UI super easy to scale and resize with just a little effort in code
	
	//Main stage. Must only contain tableContainer
	Stage stage;
	//Container for main stage table. This must only contain table
	Container<Table> tableContainer = new Container<Table>();
	//Table that contains all the stage. Must be cleared any time the window is resize. Look at buildTable1()
	Table table = new Table();
	
	UIManager uiManager; 
	MyScreen prevScreen;

	float width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
	static float sw, sh;
	float cw, ch;
	float cwmult = 0.8f, chmult = 1f;
	
	public MyScreen(UIManager uiManager_) {
		this.uiManager = uiManager_;
		stage = new Stage(new ScreenViewport());

		stage = new Stage(new ScreenViewport());
		tableContainer.setActor(table);
		stage.addActor(tableContainer);
		
//		table.setDebug(true);

		sw = width;
		sh = height;
		cw = sw * cwmult;
		ch = sh * chmult;
		
		createTable();
		buildTable();
	}
	
	//Classes that inherit from this must use createTable to prepare the stage (create actors and listeners) and buildTable() to layout them
	//buildTable1 make sure the table is cleared before it's layout, since position and sizes have to recalculated each time the window is resized
	public void createTable() {}
	
	public void buildTable() {
		table.clear();

		sw = width;
		sh = height;
		cw = sw * cwmult;
		ch = sh * chmult;

		tableContainer.setSize(cw, ch);
		tableContainer.setPosition((sw - cw) / 2.0f, (sh - ch) / 2.0f);
		tableContainer.fill();
	}
	
	public void update() {}

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
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		update();
		
	    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		this.width = width;
		this.height = height;
		buildTable();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public float containerDiagonal() {
		return (float) Math.sqrt( cw*cw + ch*ch );
	}
	
	public static float screenDiagonal() {
		return (float) Math.sqrt( sw*sw + sh*sh );
	}
	
	public void setPrevScreen(MyScreen s) {
		this.prevScreen = s;
	}

}
