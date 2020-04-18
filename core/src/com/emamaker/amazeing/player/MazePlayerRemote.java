package com.emamaker.amazeing.player;

import java.util.Random;
import java.util.UUID;

import com.badlogic.gdx.Game;
import com.emamaker.amazeing.AMazeIng;

public class MazePlayerRemote extends MazePlayer{

	/*Remote controlled player to show other players on the server*/
	
	static Random rand = new Random();

	String name;
	AMazeIng main;
	UUID uuid;
	
	boolean disposing = false;

	public MazePlayerRemote(Game main_, UUID u) {
		super(main_);
		uuid = u;
	}
	
	public void updateRemoteTransform(String s) {
		float x = 0, y = 0, z = 0, i = 0, j = 0, k = 0, l = 0;
		setTransform(x, y, z, i, j, k, l);
	}

	public void setName(String name_) {
		this.name = name_;
	}

	public String getName() {
		return name;
	}

	public void setPlaying() {
		disposing = false;
	}

	
	@Override
	public void update() {
	}
	
}
