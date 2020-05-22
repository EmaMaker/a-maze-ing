package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.graphics.Texture;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.TextureLoader;

public class PowerUpBomb extends PowerUp {

	int radius = 1;

	AMazeIng main = AMazeIng.getMain();
	
	public PowerUpBomb() {
		this("BOMB", TextureLoader.textureBomb, false, 1f, 1f);
	}
	
	public PowerUpBomb(String name, Texture texture, boolean cont, float sx, float sz) {
		super(name, texture, cont, sx, sz);
	}

	@Override
	public boolean usePowerUp(MazePlayer  player) {
		super.usePowerUp(player);
		
		int px = (int) player.getPos().x;
		int pz = (int) player.getPos().z;

		int tmptodraw[][] = new int[main.currentGameManager.mazeGen.w][main.currentGameManager.mazeGen.h];

		for (int i = 0; i < main.currentGameManager.mazeGen.w; i++)
			for (int k = 0; k < main.currentGameManager.mazeGen.h; k++)
				tmptodraw[i][k] = main.currentGameManager.mazeGen.todraw[i][k];

		for (int i = px - radius; i < px + radius + 1; i++) {
			for (int k = pz - radius; k < pz + radius + 1; k++) {
				tmptodraw = main.currentGameManager.mazeGen.changeMap(tmptodraw, i, k, 0);
			}
		}
		
		AMazeIng.getMain().currentGameManager.requestChangeToMap(tmptodraw);
		
		return true;
	}
}

class PowerUpBigBomb extends PowerUpBomb{
	
	public PowerUpBigBomb() {
		super("BIG BOMB", TextureLoader.textureBomb, false, 1.5f, 1.5f);
		radius = 2;
	}
	
}
