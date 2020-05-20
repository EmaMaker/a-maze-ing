package com.emamaker.amazeing.player.powerups;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.emamaker.amazeing.player.MazePlayer;

public class PowerUpFeather extends PowerUpTemporized {

	public PowerUpFeather() {
		super("FEATHER", new Texture(Gdx.files.internal("data/powerups/feather.png")), true, 10,
				1f, 1f);
	}

	@Override
	public void temporizedEffect(MazePlayer player) {
		player.speedMult = 2f;
	}

	@Override
	public void temporizedEffectExpired(MazePlayer player) {
		player.speedMult = 1f;
	}

}
