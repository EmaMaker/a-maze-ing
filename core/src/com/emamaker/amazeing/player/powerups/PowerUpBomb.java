package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.emamaker.amazeing.AMazeIng;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.TextureLoader;

public class PowerUpBomb extends PowerUp {

	int radius = 2;

	AMazeIng main = AMazeIng.getMain();
	
	public PowerUpBomb() {
		this("BOMB", TextureLoader.textureBomb, false, 1f, 1f, Gdx.files.internal("data/particles/explosion_small.particle"), Gdx.files.internal("data/particles"));
	}
	
	public PowerUpBomb(String name, Texture texture, boolean cont, float sx, float sz, FileHandle effectFile, FileHandle imageSrc) {
		super(name, texture, cont, sx, sz, effectFile, imageSrc);
	}

	@Override
	public boolean usePowerUp(MazePlayer  player) {
		super.usePowerUp(player);
		
		int px = (int) player.getPos().x;
		int pz = (int) player.getPos().z;
		
		System.out.println("Player in " + player.getPos());

		int tmptodraw[][] = new int[main.currentGameManager.mazeGen.w][main.currentGameManager.mazeGen.h];

		for (int i = 0; i < main.currentGameManager.mazeGen.w; i++)
			for (int k = 0; k < main.currentGameManager.mazeGen.h; k++)
				tmptodraw[i][k] = main.currentGameManager.mazeGen.todraw[i][k];

		for (int i = px - radius - 1; i < px + radius + 2; i++) {
			for (int k = pz - radius - 1; k < pz + radius + 2; k++) {
				if(player.getPos().dst(i, 1, k) <= radius) tmptodraw = main.currentGameManager.mazeGen.changeMap(tmptodraw, i, k, 0);
			}
		}

		AMazeIng.getMain().requestChangeToMap(tmptodraw);
		
		return true;
	}
}

class PowerUpBigBomb extends PowerUpBomb{
	
	public PowerUpBigBomb() {
		super("BIG BOMB", TextureLoader.textureBomb, false, 1.5f, 1.5f, Gdx.files.internal("data/particles/explosion.particle"), Gdx.files.internal("data/particles"));
		radius = 3;
	}
	
}
