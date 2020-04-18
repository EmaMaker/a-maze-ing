package com.emamaker.amazeing;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.ui.UIManager;
import com.emamaker.voxelengine.VoxelWorld;

public class AMazeIng extends Game {

	public VoxelWorld world = new VoxelWorld();
	FPSLogger fps = new FPSLogger();
	boolean generated = false;
	boolean gui=false;
	Random rand = new Random();

	public UIManager uiManager;
	public GameManager gameManager;
	public InputMultiplexer multiplexer = new InputMultiplexer();

	@Override
	public void create() {
		//Bullet init for physics
		Bullet.init();
		
		//Set windowed resolution
		Gdx.graphics.setWindowedMode(1280, 720);
		
		//Voxel engine init. Call everything after this
		world.init(this);
		world.worldManager.generateChunks = false;
		world.worldManager.updateChunks = true;
		
		//Disable VoxelEngines's integrated camera input processor
		Gdx.input.setInputProcessor(multiplexer);
		
		generated = false;
		setupGUI();
		setupGameManager();
	}
	
	public void setupGUI() {
		System.out.println("Setup UI Manager");
		uiManager = new UIManager(this);
	}
	
	public void setupGameManager() {
		System.out.println("Setup Game Manager");
		gameManager = new GameManager(this);
	}

	float delta;
	@Override
	public void render() {
		super.render();
		if(gameManager != null) gameManager.update();
	}

	@Override
	public void dispose() {
		world.dispose();
		gameManager.dispose();
	}

	@Override
	public void resize(int width, int height) {
		world.resize(width, height);
		this.getScreen().resize(width, height);
	}

	@Override
	public void pause() {
		world.pause();
	}

	@Override
	public void resume() {
		world.resume();
	}
	
}
