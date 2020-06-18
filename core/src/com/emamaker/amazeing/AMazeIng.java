package com.emamaker.amazeing;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;
import com.emamaker.amazeing.manager.GameManager;
import com.emamaker.amazeing.manager.managers.GameManagerLocal;
import com.emamaker.amazeing.manager.network.GameClient;
import com.emamaker.amazeing.manager.network.GameServer;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.powerups.PowerUps;
import com.emamaker.amazeing.ui.UIManager;
import com.emamaker.amazeing.utils.TextureLoader;
import com.emamaker.voxelengine.VoxelWorld;

public class AMazeIng extends Game {

	public VoxelWorld world = new VoxelWorld();
	FPSLogger fps = new FPSLogger();
	boolean generated = false;
	boolean gui = false;
	Random rand = new Random();

	public UIManager uiManager;
	public GameManager currentGameManager;
	public GameManagerLocal gameManager;
	public MazeSettings settings;
	public InputMultiplexer multiplexer = new InputMultiplexer();

	/* Local manager for local games and server host in multiplayer games */
	public GameServer server;
	public GameClient client;

	static AMazeIng game;

	public static Platform PLATFORM;

	public float delta = 0;

	public SpriteBatch spriteBatch;
	public Array<PooledEffect> effects = new Array<PooledEffect>();

	
	public AMazeIng(Platform p) {
		PLATFORM = p;
	}

	@Override
	public void create() {
		game = this;

		// Bullet init for physics
		Bullet.init();

		// Set windowed resolution
		Gdx.graphics.setWindowedMode(1280, 720);

		// Enable on-screen keyboard for mobile devices
		// Gdx.input.setOnscreenKeyboardVisible(true);

		// Voxel engine init. Call everything after this
		world.init(this);
		world.worldManager.generateChunks = false;
		world.worldManager.updateChunks = true;

		// Disable VoxelEngines's integrated camera input processor and enable our own
		Gdx.input.setInputProcessor(multiplexer);

		generated = false;
		setupGUI();
		setupGameManager();
		setupPowerUps();
		
		spriteBatch = new SpriteBatch();
	}

	public void setupGUI() {
		System.out.println("Setup UI Manager and TextureLoader");
		new TextureLoader();
		uiManager = new UIManager(this);
		settings = new MazeSettings();
		uiManager.setupScreens();
	}

	public void setupGameManager() {
		System.out.println("Setup Game Managers");
		gameManager = new GameManagerLocal();

		server = new GameServer();
		client = new GameClient();
	}

	public void setupPowerUps() {
		System.out.println("Setting up PowerUps");
		new PowerUps();
	}
	
	public void requestChangeToMap(int[][] todraw) {
		if(server.isRunning()) server.gameManager.requestChangeToMap(todraw);
		else if(!client.isRunning()) server.gameManager.requestChangeToMap(todraw);
	}

	@Override
	public void render() {
		super.render();
		delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());

		server.update();
		client.update();
		gameManager.update();
		
		spriteBatch.begin();
		// Update and draw effects:
		for (int i = effects.size - 1; i >= 0; i--) {
			PooledEffect effect = effects.get(i);
			effect.draw(spriteBatch, delta);
			if (effect.isComplete()) {
				effect.free();
				effects.removeIndex(i);
			}
		}
		spriteBatch.end();

	}

	@Override
	public void dispose() {
		gameManager.dispose();
		if (server.isRunning())
			client.stop(true);
		else
			client.stop();
		server.stop();
		world.dispose();
		clearEffects();		
	}

	public void clearEffects() {
		// Reset all effects:
		for (int i = effects.size - 1; i >= 0; i--)
		    effects.get(i).free(); //free all the effects back to the pool
		effects.clear(); //clear the current effects array
	}
	
	@Override
	public void resize(int width, int height) {
		world.resize(width, height);
		if (this.getScreen() != null)
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

	public static AMazeIng getMain() {
		return game;
	}

	public static boolean isDesktop() {
		return PLATFORM == Platform.DESKTOP;
	}

	public static boolean isMobile() {
		return PLATFORM == Platform.ANDROID || PLATFORM == Platform.IOS;
	}

	public static boolean isIOS() {
		return PLATFORM == Platform.IOS;
	}

	public static enum Platform {
		DESKTOP, ANDROID, IOS
	}
}
