package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.Gdx;
import com.emamaker.amazeing.player.MazePlayer;
import com.emamaker.amazeing.utils.TextureLoader;

public class PowerUpFeather extends PowerUpTemporized {

	public PowerUpFeather() {
		super("FEATHER", TextureLoader.textureFeather, true, 10,
				1f, 1f, Gdx.files.internal("data/particles/feather.particle"), Gdx.files.internal("data/powerups"));
	}

	@Override
	public void temporizedEffect(MazePlayer player) {
		super.temporizedEffect(player);
		player.speedMult = 2f;
	}

	@Override
	public void temporizedEffectExpired(MazePlayer player) {
		super.temporizedEffectExpired(player);
		player.speedMult = 1f;
	}

}
