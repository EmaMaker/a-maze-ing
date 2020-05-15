package com.emamaker.amazeing.player;

import java.util.Random;

import com.emamaker.amazeing.AMazeIng;

public class MazePlayerRemote extends MazePlayer{

	/*Remote controlled player to show other players on the server*/
	
	static Random rand = new Random();

	AMazeIng main;
	//UUID is stored a string, for kryonet ease of use
	
	boolean disposing = false;

	public MazePlayerRemote(String u) {
		this(u, true);
	}
	
	public MazePlayerRemote( String u, boolean b) {
		super(b);
		uuid = u;
	}

	
	@Override
	public void update() {
	}
	
}
