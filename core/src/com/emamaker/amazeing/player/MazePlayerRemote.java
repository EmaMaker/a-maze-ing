package com.emamaker.amazeing.player;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.emamaker.amazeing.AMazeIng;

public class MazePlayerRemote extends MazePlayer{

	/*Remote controlled player to show other players on the server*/
	
	static Random rand = new Random();

	String name;
	AMazeIng main;
	//UUID is stored a string, for kryonet ease of use
	public String uuid;
	
	boolean disposing = false;

	public MazePlayerRemote(Game main_, String u) {
		this(main_, u, true);
	}
	
	public MazePlayerRemote(Game main_, String u, boolean b) {
		super(main_, b);
		uuid = u;
	}

	public void setName(String name_) {
		this.name = name_;
	}

	public String getName() {
		return name;
	}

	
	@Override
	public void update() {
	}
	
}
