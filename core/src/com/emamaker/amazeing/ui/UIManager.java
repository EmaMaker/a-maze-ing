package com.emamaker.amazeing.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.ui.screens.PlayerChooseScreen;
import com.emamaker.amazeing.ui.screens.PreGameScreen;
import com.emamaker.amazeing.ui.screens.ServerJoinScreen;
import com.emamaker.amazeing.ui.screens.ServerLaunchScreen;
import com.emamaker.amazeing.ui.screens.SettingsScreen;
import com.emamaker.amazeing.ui.screens.TitleScreen;

public class UIManager {
	
	public Skin skin;
	public AMazeIng main;
	float delta;
	public TitleScreen titleScreen;
	public PlayerChooseScreen playersScreen;
	public SettingsScreen setScreen;
	public ServerJoinScreen srvJoinScreen;
	public ServerLaunchScreen srvLaunchScreen;
	public PreGameScreen preGameScreen;
	
	public UIManager(Game main_) {
		main = (AMazeIng)main_;
		
		//Load the sinks
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
	}
	
	public void setupScreens() {
		//Load all the screens after loading the skin
		titleScreen = new TitleScreen(this);
		playersScreen = new PlayerChooseScreen(this);
		setScreen = new SettingsScreen(this);
		srvJoinScreen = new ServerJoinScreen(this);
		srvLaunchScreen = new ServerLaunchScreen(this);
		preGameScreen = new PreGameScreen(this);
		
		main.setScreen(titleScreen);
	}
	
	public void dispose() {
		titleScreen.dispose();
	}

	public BitmapFont generatefont(int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/default.fnt"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = size;
		parameter.magFilter = Texture.TextureFilter.Linear;
		parameter.minFilter = Texture.TextureFilter.Linear;
		BitmapFont font32 = generator.generateFont(parameter); // font size 32 pixels
		font32.getData().setScale(0.15f);
		generator.dispose();
		return font32;
	}
	
}
