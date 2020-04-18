package com.emamaker.amazeing.player;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.net.Socket;
import com.emamaker.amazeing.AMazeIng;

public class MazePlayerRemote extends MazePlayer{

	/*Remote controlled player to show other players on the server*/
	
	static Random rand = new Random();

	// MazePlayer model building stuff
	public Model mazePlayerModel;
	public ModelInstance instance;
	ModelBuilder modelBuilder = new ModelBuilder();
	MeshPartBuilder meshBuilder;
	static int meshAttr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
	public Controller ctrl;

	// Physics using LibGDX's bullet wrapper
	public int kup, kdown, ksx, kdx;
	float startx, starty, startz;
	String name;
	AMazeIng main;
	Socket socket;
	
	boolean disposing = false;

	public MazePlayerRemote(Game main_, Socket s) {
		super(main_);
		socket = s;
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
	
	@Override
	public void dispose() {
		disposing = true;
		mazePlayerModel.dispose();
		disposing = false;
	}

}
