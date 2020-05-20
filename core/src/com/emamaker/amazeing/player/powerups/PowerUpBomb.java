package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.maze.MazeGenerator;
import com.emamaker.amazeing.player.MazePlayer;

public class PowerUpBomb extends PowerUp {

	int radius = 1;

	public PowerUpBomb() {
		this("BOMB", new Texture(Gdx.files.internal("data/powerups/bomb.png")), false, 1f, 1f);
	}
	
	public PowerUpBomb(String name, Texture texture, boolean cont, float sx, float sz) {
		super(name, texture, cont, sx, sz);
	}

	@Override
	public boolean usePowerUp(MazePlayer  player) {
		super.usePowerUp(player);
		
		int px = (int) player.getPos().x;
		int pz = (int) player.getPos().z;

		int tmptodraw[][] = new int[MazeGenerator.w][MazeGenerator.h];

		for (int i = 0; i < MazeGenerator.w; i++)
			for (int k = 0; k < MazeGenerator.h; k++)
				tmptodraw[i][k] = MazeGenerator.todraw[i][k];

		for (int i = px - radius; i < px + radius + 1; i++) {
			for (int k = pz - radius; k < pz + radius + 1; k++) {
				tmptodraw = MazeGenerator.changeMap(tmptodraw, i, k, 0);
			}
		}
		
		AMazeIng.getMain().currentGameManager.requestChangeToMap(tmptodraw);
		
		return true;
	}
}

class PowerUpBigBomb extends PowerUpBomb{
	
	public PowerUpBigBomb() {
		super("BIG BOMB", new Texture(Gdx.files.internal("data/powerups/bomb.png")), false, 1.5f, 1.5f);
		radius = 2;
	}
	
}
