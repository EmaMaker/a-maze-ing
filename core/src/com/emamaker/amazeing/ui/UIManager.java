package com.emamaker.amazeing.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.ui.screens.PlayerChooseScreen;
import com.emamaker.amazeing.ui.screens.TitleScreen;

public class UIManager {
	
	public Skin skin;
	public AMazeIng main;
	float delta;
	public TitleScreen titleScreen;
	public PlayerChooseScreen playersScreen;
	
	public UIManager(Game main_) {
		main = (AMazeIng)main_;
		
		//Load the sinks
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		//Load all the screens after loading the skin
		titleScreen = new TitleScreen(this);
		playersScreen = new PlayerChooseScreen(this);
		main.setScreen(titleScreen);
	}
	
	public void dispose() {
		titleScreen.dispose();
	}
	
}
