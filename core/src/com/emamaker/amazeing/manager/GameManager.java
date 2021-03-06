package com.emamaker.amazeing.manager;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.AMazeIng.Platform;
import com.emamaker.amazeing.maze.MazeGenerator;
import com.emamaker.amazeing.maze.settings.MazeSettings;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.player.MazePlayerLocal;
import com.emamaker.amazeing.player.powerups.PowerUp;
import com.emamaker.amazeing.player.powerups.PowerUps;
import com.emamaker.amazeing.utils.MathUtils;
import com.emamaker.voxelengine.block.CellId;
import com.emamaker.voxelengine.player.Player;

public class GameManager {

	protected AMazeIng main;
	public MazeGenerator mazeGen;

	public boolean gameStarted = false;
	public boolean showGame = true;
	public boolean anyoneWon = false;

	public Stage stage;

	Random rand = new Random();

	GameType type = GameType.LOCAL;

	public ArrayList<MazePlayer> players = new ArrayList<MazePlayer>();
	public ArrayList<PowerUp> powerups = new ArrayList<PowerUp>();
	protected ArrayList<MazePlayer> toDeletePlayer = new ArrayList<MazePlayer>();
	protected ArrayList<PowerUp> toDeletePowerUps = new ArrayList<PowerUp>();

	protected PowerUp pup;

	TextButton pauseBtn;
	Dialog pauseDlg;
	Label pauseDlgText;
	TextButton pauseDlgResumeBtn, pauseDlgQuitBtn;
	boolean showingDialog = false;

	public GameManager(Game main_, GameType t) {
		main = (AMazeIng) main_;
		gameStarted = false;

		type = t;
		setShowGame(type != GameType.SERVER);

		mazeGen = new MazeGenerator(main, MazeSettings.MAZEX, MazeSettings.MAZEZ);
		stage = new Stage(new ScreenViewport());
	}

	public void generateMaze(Set<MazePlayer> pl, int todraw[][]) {
		main.setScreen(null);

		AMazeIng.getMain().multiplexer.addProcessor(stage);

		anyoneWon = false;

		if (AMazeIng.PLATFORM == Platform.DESKTOP) {
			if (pl != null) {
				for (MazePlayer p : players)
					if (!pl.contains(p))
						toDeletePlayer.add(p);

				// Check if new players have to be added
				for (MazePlayer p : pl)
					if (!players.contains(p))
						players.add(p);

				for (MazePlayer p : toDeletePlayer) {
					p.dispose();
					players.remove(p);
				}
				toDeletePlayer.clear();
			}
		} else {
			for (MazePlayer p : players)
				p.dispose();
			players.clear();
			players.addAll(pl);
		}

		for (int i = 0; i < MazeSettings.MAZEX; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < MazeSettings.MAZEZ; k++) {
					main.world.worldManager.setCell(i, j, k, CellId.ID_AIR);
				}
			}
		}

		mazeGen.setMazeSize(MazeSettings.MAZEX, MazeSettings.MAZEZ);
		mazeGen.generateMaze();
		resetCamera();
		gameStarted = true;

		showingDialog = false;

	}

	public void addTouchScreenInput() {
		if (getShowGame() && AMazeIng.isMobile()) {
			for (MazePlayer p : players) {
				if (p instanceof MazePlayerLocal) {
					stage.addActor(((MazePlayerLocal) p).tctrl);
					stage.addActor(((MazePlayerLocal) p).touchpadPowerUp);
				}
			}
		}
	}

	public void setupHud() {
		pauseBtn = new TextButton("Pause", main.uiManager.skin);
//		pauseBtn.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("data/pause.png"))));
//		pauseBtn.getStyle().imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("data/pause.png"))));

		pauseDlg = new Dialog("Pause", main.uiManager.skin);

		pauseDlgText = new Label("What do you want to do?", main.uiManager.skin);

		pauseDlgResumeBtn = new TextButton("Resume", main.uiManager.skin);
		pauseDlgQuitBtn = new TextButton("Quit", main.uiManager.skin);

		pauseDlg.text(pauseDlgText);
		pauseDlg.button(pauseDlgQuitBtn);
		pauseDlg.button(pauseDlgResumeBtn);

		pauseBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				pauseDlg.show(stage);
				return true;
			}
		});

		pauseDlgResumeBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				pauseDlg.hide();
				return true;
			}
		});
		pauseDlgQuitBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				quitGameByBtn();
				return true;
			}
		});

		stage.addActor(pauseBtn);
	}

	float cw, ch, d, labScale, buttonDim;

	public void hudUpdate() {
		resetCamera();
		setCamera(new Vector3((MazeSettings.MAZEX + 1) / 2, (MazeSettings.MAZEX + MazeSettings.MAZEZ) * 0.48f,
				MazeSettings.MAZEZ / 2 - 1.75f), new Vector3(0, -90, 0));

		cw = Gdx.graphics.getWidth();
		ch = Gdx.graphics.getHeight();
		d = MathUtils.pythagoreanTheorem(cw, ch);
		labScale = d * .00090f;
		buttonDim = d * 0.04f;
		pauseBtn.setSize(buttonDim, buttonDim);
		pauseBtn.setPosition((cw - pauseBtn.getWidth()) / 2, ch - pauseBtn.getHeight());
		pauseBtn.getLabel().setFontScale(labScale * 0.9f);

		pauseDlg.setSize(cw * 0.2f, ch * 0.15f);
		pauseDlg.setPosition((cw - pauseDlg.getWidth()) / 2, (ch - pauseDlg.getHeight()) / 2);
		pauseDlgResumeBtn.getLabel().setFontScale(labScale * 0.9f);
		pauseDlgQuitBtn.getLabel().setFontScale(labScale * 0.9f);
		pauseDlgText.setFontScale(labScale * 0.9f);

		stage.act();
		stage.draw();
	}

	public void renderWorld() {
		main.world.render();
	}

	public void renderPowerUps() {
		for (PowerUp p : powerups)
			p.render(main.world.modelBatch, main.world.environment);
	}

	public void renderPlayers() {
		for (MazePlayer p : players)
			renderPlayer(p);
	}

	public void updatePlayers() {
		for (MazePlayer p : players)
			updatePlayer(p);
	}

	public void updatePowerUps() {
		for (PowerUp p : toDeletePowerUps) {
			powerups.remove(p);
		}
		toDeletePowerUps.clear();
	}

	public void renderPlayer(MazePlayer p) {
		if (getShowGame())
			p.render(main.world.modelBatch, main.world.environment);
	}

	public void updatePlayer(MazePlayer p) {
		p.update();
	}

	public boolean checkPowerUp(MazePlayer p, PowerUp p1) {
		return (int) p1.getPosition().x == (int) p.getPos().x && (int) p1.getPosition().z == (int) p.getPos().z;
	}

	public void assignPowerUps() {
		if (players != null && !players.isEmpty()) {
			for (MazePlayer p : players) {
				for (PowerUp p1 : powerups) {
					if (checkPowerUp(p, p1)) {
						assignPowerUp(p, p1);
					}
				}
			}
		}
	}

	public void revokePowerUp(MazePlayer p) {
		p.disablePowerUp();
	}

	public void assignPowerUp(MazePlayer p, PowerUp p1, boolean immediateUse) {
		if (p.currentPowerUp == null) {
			toDeletePowerUps.add(p1);
			p.currentPowerUp = p1;
			if (immediateUse)
				p.usePowerUp();
		}
	}

	public void assignPowerUp(MazePlayer p, PowerUp p1) {
		this.assignPowerUp(p, p1, false);
	}

	public void usePowerUp(MazePlayer p) {
		p.usePowerUp();
	}

	public void checkWin() {
		for (MazePlayer p : players)
			if (checkWin(p)) {
				setFinished();
				return;
			}
	}

	public void setFinished() {
		anyoneWon = true;
		gameStarted = false;

		AMazeIng.getMain().multiplexer.removeProcessor(stage);
		for (MazePlayer p : players)
			p.disablePowerUp();
		main.clearEffects();
	}

	public void quitGameByBtn() {
		setFinished();
		if (pauseDlg != null) {
			pauseDlg.hide();
		}
	}

	public boolean getFinished() {
		return anyoneWon;
	}

	public void update() {
		main.currentGameManager = this;

		generalUpdate();
		if (gameStarted && !anyoneWon) {
			inGameUpdate();
			checkWin();
		}
	}

	public void generalUpdate() {

	}

	public void inGameUpdate() {
		if (players != null) {
			updatePlayers();
			updatePowerUps();
		}

	}

	public void spreadPlayers() {
		for (MazePlayer p : players) {
			int x = 1, z = 1;
			do {
				x = (Math.abs(rand.nextInt() - 1) % (mazeGen.w));
				z = (Math.abs(rand.nextInt() - 1) % (mazeGen.h));
			} while (thereIsPlayerInPos(x, z) || mazeGen.occupiedSpot(x, z));
			p.setPos(x + 0.5f, 2f, z + 0.5f);
		}
	}

	public void spawnPowerUps() {
		for (int i = 0; i < MazeSettings.START_POWERUPS; i++)
			spawnRandomPowerUp();
	}

	public void spawnRandomPowerUp() {
		int x = 1, z = 1, tries = 0;
		int maxtries = MazeSettings.MAZEX * MazeSettings.MAZEZ + 2;
		do {
			x = (Math.abs(rand.nextInt() - 1) % (mazeGen.w));
			z = (Math.abs(rand.nextInt() - 1) % (mazeGen.h));
			tries++;
		} while ((thereIsPlayerInPos(x, z) || mazeGen.occupiedSpot(x, z) || thereIsPowerUpInPos(x, z))
				&& tries < maxtries);
		if (tries < maxtries) {
			System.out.println("Spawning power-up in " + x + ", " + z);
			spawnPowerUp(x + .5f, z + .5f);
		}
	}

	public void spawnPowerUp(float x, float z) {
		PowerUp p = PowerUps.pickRandomPU();
		p.setPosition(x, 1.25f, z);
		powerups.add(p);
	}

	public void spawnPowerUpByName(String name, float x, float z) {
		PowerUp p = PowerUps.pickByName(name);
		p.setPosition(x, 1.25f, z);
		powerups.add(p);
	}

	public void clearPowerUps() {
		for (PowerUp p : powerups)
			if (p != null)
				p.dispose();
		powerups.clear();
		toDeletePowerUps.clear();
	}

	public void removePowerUp(PowerUp p) {
		toDeletePowerUps.add(p);
	}

	public String getPowerUpNameByPos(int x, int z) {
		PowerUp p = getPowerUpByPos(x, z);
		return p == null ? "" : p.name;
	}

	public PowerUp getPowerUpByPos(int x, int z) {
		for (PowerUp p : powerups)
			if ((int) p.getPosition().x == x || (int) p.getPosition().z == z)
				return p;
		return null;
	}

	Player generateNewPlayer(int kup, int kdown, int ksx, int kdx) {
		return generateNewPlayer(kup, kdown, ksx, kdx, "");
	}

	Player generateNewPlayer(int kup, int kdown, int ksx, int kdx, String name) {
		int x, z;
		do {
			x = (Math.abs(rand.nextInt() - 1) % (mazeGen.w));
			z = (Math.abs(rand.nextInt() - 1) % (mazeGen.h));
		} while (thereIsPlayerInPos(x, z) || mazeGen.occupiedSpot(x, z));
		if (name.equalsIgnoreCase(""))
			return new Player(kup, kdown, ksx, kdx, x + 0.5f, 4f, z + 0.5f);
		else
			return new Player(kup, kdown, ksx, kdx, x + 0.5f, 4f, z + 0.5f, name);
	}

	public boolean checkWin(MazePlayer p) {
		if ((int) p.getPos().x == mazeGen.WINX && (int) p.getPos().z == mazeGen.WINZ) {
			System.out.println(p.getName() + " won");
			return true;
		}
		return false;
	}

	public boolean thereIsPlayerInPos(int x, int z) {
		for (MazePlayer p : players) {
			if ((int) p.getPos().x == x || (int) p.getPos().z == z)
				return true;
		}
		return false;
	}

	public boolean thereIsPowerUpInPos(int x, int z) {
		for (PowerUp p : powerups) {
			if ((int) p.getPosition().x == x || (int) p.getPosition().z == z)
				return true;
		}
		return false;
	}

	public boolean areTherePlayersNearby(int x, int z, int range) {
		int i, k;
		for (MazePlayer p : players) {
			i = (int) p.getPos().x;
			k = (int) p.getPos().z;

			if ((x - i) * (x - i) + (k - z) * (k - z) <= range * range)
				return true;
		}
		return false;
	}

	public MazePlayer getRandomPlayer() {
		return players.get(Math.abs(rand.nextInt() % players.size()));
	}

	public void requestChangeToMap(int[][] todraw) {
		if (gameStarted)
			mazeGen.show(todraw);
	}

	public void resetCamera() {
		main.world.cam.position.set(0f, 0f, 0.5f); // Set cam position at origin
		main.world.cam.lookAt(0, 0, 0); // Direction to look at, for setting direction (perhaps better to set manually)
//		 main.world.cam.near = 1f;            // Minimum distance visible
//		 main.world.cam.far = 300f;            // Maximum distance visible
		main.world.cam.up.set(0f, 1f, 0f); // Up is in y positive direction
		main.world.cam.view.idt(); // Reset rotation matrix
		main.world.cam.update();
	}

	public void setCamera(Vector3 position, Vector3 rotation) {
		main.world.cam.translate(position); // set cam absolute position
		main.world.cam.rotate(rotation.x, 0f, 1f, 0f); // set cam absolute rotation on axis X
		main.world.cam.rotate(rotation.y, 1f, 0f, 0f); // set cam absolute rotation on axis Y
		main.world.cam.rotate(rotation.z, 0f, 0f, 1f); // set cam absolute rotation on axis Z
		main.world.cam.update();
	}

	protected boolean getShowGame() {
		return showGame;
	}

	void setShowGame(boolean g) {
		showGame = g;
	}

	public void generateMaze() {
		generateMaze(null, null);
	}

	public void generateMaze(int todraw[][]) {
		generateMaze(null, todraw);
	}

	public void generateMaze(Set<MazePlayer> pl) {
		generateMaze(pl, null);
	}

	public void dispose() {
		for (MazePlayer p : players)
			if (!p.isDisposed())
				p.dispose();
		players.clear();
	}

}
