package com.emamaker.amazeing;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.emamaker.amazeing.manager.managers.GameManager;
import com.emamaker.amazeing.manager.managers.GameManagerLocal;
import com.emamaker.amazeing.manager.network.GameClient;
import com.emamaker.amazeing.manager.network.GameServer;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.powerups.PowerUps;
import com.emamaker.amazeing.ui.UIManager;
import com.emamaker.voxelengine.VoxelWorld;

import java.util.Random;

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

	public AMazeIng(Platform p) {
		PLATFORM = p;
	}

	@Override
	public void create() {
		game = this;

		// Bullet init for physics
		Bullet.init();

		// Set windowed resolution
		Gdx.graphics.setWindowedMode(640, 480);

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
	}

	public void setupGUI() {
		System.out.println("Setup UI Manager");
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

	@Override
	public void render() {
		super.render();
		server.update();
		client.update();
		gameManager.update();
	}

	@Override
	public void dispose() {
		world.dispose();
		gameManager.dispose();
		client.stop();
		server.stop();
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
